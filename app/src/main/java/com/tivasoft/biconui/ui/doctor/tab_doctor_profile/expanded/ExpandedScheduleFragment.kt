package com.tivasoft.biconui.ui.doctor.tab_doctor_profile.expanded

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.tivasoft.biconui.ui.doctor.tab_doctor_profile.DoctorProfileViewModel
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.ScheduleIntent
import com.bumptech.glide.Glide
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentDoctorProfileExpandedBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.utils.CustomLinearLayoutManager
import com.tivasoft.biconui.utils.DoctorScheduleRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class ExpandedScheduleFragment : BaseFragment() {
    private var _binding: FragmentDoctorProfileExpandedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DoctorProfileViewModel by activityViewModels()

    private lateinit var scheduleAdapter: ExpandedScheduleAdapter
    private lateinit var dayPickerAdapter: DayPickerAdapter

    private lateinit var scheduleLayoutManager: LinearLayoutManager
    private lateinit var dayPickerLayoutManager: LinearLayoutManager

    private lateinit var smoothScroller: LinearSmoothScroller
    private lateinit var listener: DoctorScheduleRefreshListener

    private var schedulesJob: Job? = null


    var allPixelsDate: Int = 0
    private var finalWidthDate: Int = 0
    private var itemWidthDate: Float = 0f
    private var paddingDate: Float = 0f
    private var firstItemWidthDate: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentDoctorProfileExpandedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDayPicker()
        setupScheduleList()
        subscribeObserver()

        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
            }
            itemProfile.setOnClickListener {
                /* findNavController().navigate(
                     ExpandedScheduleFragmentDirections.actionExpandedSchedulesToAccountInformation()
                 )*/
            }
            bookSchedule.setOnClickListener {
                /*  (requireActivity() as MainActivity).apply {
                      setAppointmentNavGraph()
                      setAppointmentBottomSheetState(true)
                  }*/
            }
            calendar.setOnClickListener {
                findNavController().navigate(
                    ExpandedScheduleFragmentDirections.actionExpandedSchedulesToCalendar()
                )
            }
            itemAccountLogout.setOnClickListener {
                showLogoutDialog()
            }
            itemFinancialPackages.setOnClickListener {
                findNavController().navigate(
                    ExpandedScheduleFragmentDirections.actionExpandedSchedulesToPackageList(
                        isAdmin = false
                    )
                )
            }
            itemCreateFinancialPackages.setOnClickListener {
                findNavController().navigate(
                    ExpandedScheduleFragmentDirections.actionExpandedSchedulesToAddPackage()
                )
            }
            getSchedules()
            Glide.with(requireContext()).load(R.drawable.sample_image).into(profilePic)
        }
    }

    private fun setupDayPicker() {
        dayPickerAdapter = DayPickerAdapter()
        smoothScroller = object : LinearSmoothScroller(context) {
            override fun calculateDtToFit(
                viewStart: Int,
                viewEnd: Int,
                boxStart: Int,
                boxEnd: Int,
                snapPreference: Int
            ): Int {
                return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
            }
        }
        dayPickerLayoutManager = CustomLinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.apply {
            dayPicker.adapter = dayPickerAdapter
            dayPicker.layoutManager = dayPickerLayoutManager
            finalWidthDate = dayPicker.measuredWidth
            itemWidthDate = resources.getDimension(R.dimen._36sdp)
            paddingDate = (finalWidthDate!! - itemWidthDate!!) / 2

            dayPickerAdapter.setOnListItemClickListener { position ->
                scrollTo(position)
                viewModel.apply {
                    calendar.set(Calendar.DAY_OF_MONTH, position + 1)
                    lifecycleScope.launch {
                        channel.send(
                            ScheduleIntent.GetSchedulesByDate(
                                calendar.timeInMillis
                            )
                        )
                    }
                    setTodayHeader()
                }
            }
        }
    }

    private fun calculatePositionAndScrollDate(recyclerView: RecyclerView) {
        var expectedPositionDate =
            Math.round((allPixelsDate + paddingDate - firstItemWidthDate) / itemWidthDate)
        if (expectedPositionDate == -1) {
            expectedPositionDate = 0
        } else if (expectedPositionDate >= (recyclerView.adapter?.itemCount!! - 2)) {
            expectedPositionDate = expectedPositionDate - 1
        }
        scrollListToPositionDate(recyclerView, expectedPositionDate)

    }

    private fun scrollListToPositionDate(recyclerView: RecyclerView, expectedPositionDate: Int) {
        val targetScrollPosDate =
            expectedPositionDate * itemWidthDate!! + firstItemWidthDate!! - paddingDate!!
        val missingPxDate = targetScrollPosDate - allPixelsDate
        if (missingPxDate != 0f) {
            recyclerView.smoothScrollBy(missingPxDate.toInt(), 0)
        }
    }

    private fun setupScheduleList() {
        scheduleAdapter = ExpandedScheduleAdapter()
        scheduleLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun scrollVerticallyBy(
                dy: Int,
                recycler: RecyclerView.Recycler?,
                state: RecyclerView.State?
            ): Int {
                val scrollRange = super.scrollVerticallyBy(dy, recycler, state)
                val overScroll: Int = dy - scrollRange
                binding.apply {
                    if (overScroll < 0 && dayPicker.visibility == View.GONE) {
                        dayPicker.visibility = View.VISIBLE
                        dayPicker.startAnimation(animated("fadein"))
                        scrollToToday()
                    }
                }
                return scrollRange
            }
        }
        binding.apply {
            scheduleList.adapter = scheduleAdapter
            scheduleList.layoutManager = scheduleLayoutManager
            scheduleList.setHasFixedSize(true)
        }
    }

    private fun scrollTo(position: Int) {
        smoothScroller.targetPosition = position
        dayPickerLayoutManager.startSmoothScroll(smoothScroller)
    }

    private fun subscribeObserver() {
        schedulesJob = lifecycleScope.launch {
            viewModel.schedules.combine(
                viewModel.daysOfMonth
            ) { dataState, days ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        scheduleAdapter.updateItems(dataState.data)
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
                when (days) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        if (dayPickerAdapter.itemCount == 0) {
                            val data = viewModel.getDays(days.data.data)
                            dayPickerAdapter.updateItems(data)
                        } else Unit
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            days.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", days.errorResponse.exception.toString())
                    }
                    else -> {

                    }
                }
            }.collect()
        }
    }

    private fun scrollToToday() {
        val initialPosition = when {
            viewModel.isCurrentMonth() -> viewModel.getToday() - 1
            else -> 0
        }
        dayPickerLayoutManager.scrollToPositionWithOffset(
            initialPosition,
            (resources.displayMetrics.widthPixels / 2.4).roundToInt()
        )
    }

    private fun getSchedules() {
        lifecycleScope.launch {
            viewModel.channel.send(ScheduleIntent.GetScheduledDays(System.currentTimeMillis()))
        }
    }

    fun setOnScheduleRefreshListener(listener: DoctorScheduleRefreshListener) {
        this.listener = listener
    }

    private fun setTodayHeader() {
        viewModel.apply {
            binding.today.text = when {
                isCurrentMonth() && calendar.get(Calendar.DAY_OF_MONTH) == getToday() ->
                    getString(R.string.today)
                else -> "${
                    calendar.getDisplayName(
                        Calendar.MONTH, Calendar.LONG, Locale.getDefault()
                    )
                }, ${calendar.get(Calendar.DAY_OF_MONTH)} ${calendar.get(Calendar.YEAR)}"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setTodayHeader()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        schedulesJob?.cancel()
    }
}