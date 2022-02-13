package com.tivasoft.biconui.ui.doctor.tab_doctor_profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tivasoft.biconui.data.model.local.ui.ScheduleItem
import com.tivasoft.biconui.data.model.local.ui.ScheduleType
import com.tivasoft.biconui.data.model.network.requests.RegisterPushyTokenRequest
import com.tivasoft.biconui.ui.patient.tab_userprofile.ScheduleAdapter
import com.tivasoft.biconui.ui.patient.tab_userprofile.UserProfileViewModel
import com.tivasoft.biconui.utils.*
import com.tivasoft.biconui.utils.channel_intents.AuthIntent
import com.tivasoft.biconui.utils.channel_intents.PatientIntent
import com.tivasoft.biconui.utils.channel_intents.ScheduleIntent
import com.tivasoft.biconui.ui.MainActivity
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentDoctorProfileBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.ui.common.profile.ProfileFragmentDirections
import com.tivasoft.biconui.utils.DoctorScheduleRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DoctorProfileFragment : BaseFragment(), DoctorScheduleRefreshListener {
    private var _binding: FragmentDoctorProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserProfileViewModel by viewModels()
    private val doctorProfileViewModel: DoctorProfileViewModel by activityViewModels()

    private lateinit var scheduleAdapter: ScheduleAdapter

    private var getMeJob: Job? = null
    private var schedulesJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentDoctorProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setFragmentHolderBackground(
            R.color.doctor_profile_background
        )

        scheduleAdapter = ScheduleAdapter()
        subscribeObservers()

        lifecycleScope.launch {
            viewModel.channel.send(AuthIntent.GetMeIntent)
            doctorProfileViewModel.channel.send(
                ScheduleIntent.GetSchedulesByDate(
                    System.currentTimeMillis()
                )
            )
        }
        binding.apply {
        /*    lookForAssistant.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileToLookForAssistance()
                )
            }

            lookForDoctor.setOnClickListener {
                *//*findNavController().navigate(
                    ProfileFragmentDirections.actionProfileToLookForDoctor()
                )*//*
            }*/

            scheduleList.adapter = scheduleAdapter
            expandMore.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileToExpandedSchedules()
                )
            }
        }
    }

    private fun subscribeObservers() {
        subscribeObserverForGetMe()
        subscribeObserverForSchedules()
    }

    private fun subscribeObserverForGetMe() {
        getMeJob = lifecycleScope.launch {
            viewModel.dataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        if (dataState.data.data.doctor?.isAssistant!!) {
                         /*   binding.lookForAssistant.visibility = View.GONE
                            binding.lookForDoctor.visibility = View.VISIBLE*/
                            sharedPreferences.edit().putBoolean("isAssistant", true).apply()
                        } else {
                          /*  binding.lookForAssistant.visibility = View.VISIBLE
                            binding.lookForDoctor.visibility = View.GONE*/
                            sharedPreferences.edit().putBoolean("isAssistant", false).apply()
                        }
                        val userId = dataState.data.data.id

                        sharedPreferences.edit().putString("userId", userId).apply()
                        lifecycleScope.launch {
                            val entity = RegisterPushyTokenRequest(
                                user = userId ?: "",
                                token = sharedPreferences.getString("pushyToken", "") ?: ""
                            )
                            viewModel.patientChannel.send(PatientIntent.RegisterPushy(entity))
                        }
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

    private fun subscribeObserverForSchedules() {
        schedulesJob = lifecycleScope.launch {
            doctorProfileViewModel.schedules.collect { schedules ->
                when (schedules) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        val data = schedules.data.filterNot { it.id == "" }
                        val items: List<ScheduleItem> = when {
                            data.isEmpty() -> {
                                listOf(
                                    ScheduleItem(
                                        id = "",
                                        scheduleType = ScheduleType.EMPTY
                                    )
                                )
                            }
                            else -> data.subList(0, minOf(data.size, 3))
                        }
                        scheduleAdapter.updateItems(ArrayList(items))
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            schedules.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", schedules.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    override fun onScheduleAdded() {
        lifecycleScope.launch {
            doctorProfileViewModel.channel.send(
                ScheduleIntent.GetSchedulesByDate(
                    System.currentTimeMillis()
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        schedulesJob?.cancel()
        getMeJob?.cancel()
    }
}