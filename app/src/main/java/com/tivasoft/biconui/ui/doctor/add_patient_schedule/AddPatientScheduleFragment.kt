package com.tivasoft.biconui.ui.doctor.add_patient_schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.tivasoft.biconui.data.model.local.ui.ScheduleEntity
import com.tivasoft.biconui.data.model.network.requests.doctor.ScheduleRequest
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.DoctorIntent
import com.bumptech.glide.Glide
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentAddPatientScheduleBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class AddPatientScheduleFragment : BaseFragment() {
    private var _binding: FragmentAddPatientScheduleBinding? = null
    private val binding get() = _binding!!

    private val addScheduleViewModel: AddScheduleViewModel by viewModels()
    private val args: AddPatientScheduleFragmentArgs by navArgs()

    private var isEdit = false
    private var entity: ScheduleEntity? = null
    private var patientId: String = ""
    private var schedulesJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPatientScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObserver()
        binding.apply {
            timeStart.setIs24HourView(true)
            timeEnd.setIs24HourView(true)
            if (timeStart.currentHour < 23)
                timeEnd.currentHour = timeStart.currentHour + 1
            else {
                setDateToTomorrow()
            }
        }
        isEdit = args.isEdit
        binding.patientName.text = args.patientName
        patientId = args.id
        Glide.with(requireContext()).load(args.patientPhoto)
            .placeholder(R.drawable.rounded_edit_text)
            .error(R.drawable.rounded_edit_text)
            .into(binding.patientIcon)
//        arguments?.getSerializable("entity")?.let {
//            entity = it as ScheduleEntity
//        }

        setListener()
        if (isEdit) {
            loadEditData()
        } else {
            binding.apply {
                typeVideoCall.isChecked = true
                datePicker.minDate = System.currentTimeMillis() - 1000
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setListener() {
        binding.apply {
            connectionType.setOnCheckedChangeListener { group, _ ->
                group.forEach {
                    (it as RadioButton).apply {
                        val icon = this.compoundDrawables[1]
                        if (isChecked)
                            icon.setTint(ContextCompat.getColor(requireContext(), R.color.cyan))
                        else
                            icon.setTint(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                }
            }

            submit.setOnClickListener {
                sendResult()
            }
        }
    }


    private fun loadEditData() {
        val startTimeIndex = entity?.startTime?.indexOf(":") ?: 0
        val endTimeIndex = entity?.endTime?.indexOf(":") ?: 0
        binding.apply {
            patientName.text = entity?.patientName
            description.setText(entity?.description)
            connectionType.check(checkedConnectionType())
            datePicker.updateDate(entity?.year!!, entity?.month!!, entity?.dayOfMonth!!)
            timeStart.currentHour = entity?.startTime?.substring(0, startTimeIndex)?.toInt() ?: 0
            timeStart.currentMinute = entity?.startTime?.substring(startTimeIndex + 1)?.toInt() ?: 0
            timeEnd.currentHour = entity?.endTime?.substring(0, endTimeIndex)?.toInt() ?: 0
            timeEnd.currentMinute = entity?.endTime?.substring(endTimeIndex + 1)?.toInt() ?: 0
        }
    }

    private fun sendResult() {
        binding.apply {
            val calendarStart = Calendar.getInstance()
            val calendarEnd = Calendar.getInstance()
            calendarStart.set(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                timeStart.hour,
                timeStart.minute
            )
            val start = calendarStart.timeInMillis
            calendarEnd.set(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                timeEnd.hour,
                timeEnd.minute
            )
            val end = calendarEnd.timeInMillis
            if (!isEdit && start < System.currentTimeMillis()) {
                Toast.makeText(
                    requireContext(),
                    R.string.schedule_in_past_error,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                lifecycleScope.launch {
                    addScheduleViewModel.channel.send(
                        DoctorIntent.CreateSchedule(
                            ScheduleRequest(
                                connectionType = getConnectionType(),
                                startTime = start,
                                endTime = end,
                                description = binding.description.text.toString(),
                                patient = patientId
                            )
                        )
                    )
                }
            }
        }
    }

    private fun getConnectionType(): Int {
        return when (binding.connectionType.checkedRadioButtonId) {
            R.id.type_voice_call -> 2
            R.id.type_video_call -> 1
            else -> 3
        }
    }

    private fun subscribeObserver() {
        schedulesJob = lifecycleScope.launch {
            addScheduleViewModel.dataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                   //     (requireActivity() as MainActivity).setAppointmentBottomSheetState(false)
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            dataState.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", dataState.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    private fun getDayOfWeek(): Int {
        binding.apply {
            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            return calendar.get(Calendar.DAY_OF_WEEK) - 1
        }
    }

    private fun checkedConnectionType(): Int {
        binding.apply {
            return when (entity?.connectionType?.ordinal) {
                0 -> typeVoiceCall.id
                1 -> typeVideoCall.id
                else -> typeChat.id
            }
        }
    }

    private fun setDateToTomorrow() {
        binding.apply {
            val calendar = Calendar.getInstance()

            calendar[Calendar.DATE] = datePicker.dayOfMonth
            calendar[Calendar.MONTH] = datePicker.month
            calendar[Calendar.YEAR] = datePicker.year

            calendar.add(Calendar.DATE, 1)
            datePicker.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
            )
            timeStart.currentHour = 13
            timeEnd.currentHour = 14
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        schedulesJob?.cancel()
    }
}