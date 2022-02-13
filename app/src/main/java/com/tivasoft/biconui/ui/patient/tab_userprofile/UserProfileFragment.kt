package com.tivasoft.biconui.ui.patient.tab_userprofile

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
import com.tivasoft.biconui.data.model.network.requests.RegisterPushyTokenRequest
import com.tivasoft.biconui.ui.common.profile.ProfileFragmentDirections
import com.tivasoft.biconui.utils.channel_intents.AuthIntent
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.PatientIntent
import com.bumptech.glide.Glide
import com.tivasoft.biconui.ui.MainActivity
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentUserProfileBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.ui.patient.tab_userprofile.ScheduleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileFragment : BaseFragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserProfileViewModel by viewModels()
    private val schedulesViewModel: SchedulesViewModel by activityViewModels()

    private lateinit var scheduleAdapter: ScheduleAdapter
    private var userInfoJob: Job? = null
    private var schedulesJob: Job? = null
    private var photoUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMe()
        getSchedules()
        setupScheduleList()
        subscribeObservers()
        (requireActivity() as MainActivity).setFragmentHolderBackground(
            R.color.profile_background
        )

        binding.apply {
            exandMore.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileToUserProfileExpanded(
                        userName = username.text.toString(),
                        age = age.text.toString(),
                        gender = gender.text.toString(),
                        photo = photoUrl
                    )
                )
            }
        }
    }

    private fun getMe() {
        lifecycleScope.launch {
            viewModel.channel.send(AuthIntent.GetMeIntent)
        }
    }

    private fun subscribeObservers() {
        subscribeObserversForUserInfo()
        subscribeObserversForSchedules()
    }

    private fun subscribeObserversForUserInfo() {
        userInfoJob = lifecycleScope.launch {
            viewModel.dataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        val userId = dataState.data.data.id
                        sharedPreferences.edit().putString("userId", userId).apply()
                        photoUrl = dataState.data.data.photo
                        binding.apply {
                            username.text = dataState.data.data.fullName
                            age.text = dataState.data.data.patient?.age
                            gender.text = dataState.data.data.patient?.genderType
                            Glide.with(requireContext()).load(photoUrl)
                                .placeholder(R.drawable.rounded_edit_text)
                                .error(R.drawable.rounded_edit_text)
                                .into(userIcon)
                        }
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

    private fun subscribeObserversForSchedules() {
        schedulesJob = lifecycleScope.launch {
            schedulesViewModel.allSchedulesDataState.collect { allSchedules ->
                when (allSchedules) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        schedulesViewModel.temp = ArrayList(allSchedules.data)
                        val temp = schedulesViewModel.temp.subList(
                            0, minOf(schedulesViewModel.temp.size, 3)
                        )
                        if (temp.size >= 3) {
                            binding.moreIndicator.text = getString(
                                R.string.schedule_more_indicator,
                                schedulesViewModel.temp.size - 3
                            )
                        } else {
                            binding.moreIndicator.text = getString(R.string.schedule_no_more)
                        }
                        scheduleAdapter.updateItems(ArrayList(temp))
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            allSchedules.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", allSchedules.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    private fun getSchedules() {
        lifecycleScope.launch {
            schedulesViewModel.channel.send(PatientIntent.GeAllSchedules)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        schedulesJob?.cancel()
        userInfoJob?.cancel()
    }

    private fun setupScheduleList() {
        scheduleAdapter = ScheduleAdapter()
        binding.scheduleList.adapter = scheduleAdapter
    }
}