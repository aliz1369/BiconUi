package com.tivasoft.biconui.ui.patient.tab_userprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tivasoft.biconui.utils.DataState
import com.bumptech.glide.Glide
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentUserProfileBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.ui.patient.tab_userprofile.ScheduleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileExpandedFragment : BaseFragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val schedulesViewModel: SchedulesViewModel by activityViewModels()
    private val args: UserProfileExpandedFragmentArgs by navArgs()

    private lateinit var scheduleAdapter: ScheduleAdapter
    private var scheduleJob: Job? = null

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

        setupScheduleList()
        subscribeObserver()

        binding.apply {
            exandMore.visibility = View.GONE
            moreIndicator.visibility = View.GONE
            settings.visibility = View.VISIBLE
            itemProfile.visibility = View.VISIBLE
            itemAttachment.visibility = View.VISIBLE
            itemAccountSecurity.visibility = View.VISIBLE
            itemGeneralSettings.visibility = View.VISIBLE
            itemAccountLogout.visibility = View.VISIBLE
            itemFinancialPackages.visibility = View.VISIBLE
            itemAdminFinancialPackages.visibility = View.VISIBLE
            username.text = args.userName
            gender.text = args.gender
            age.text = args.age
            Glide.with(requireContext()).load(args.photo)
                .placeholder(R.drawable.rounded_edit_text)
                .error(R.drawable.rounded_edit_text)
                .into(userIcon)
        }
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            itemAccountLogout.setOnClickListener {
                showLogoutDialog()
            }
            itemFinancialPackages.setOnClickListener {
                findNavController().navigate(
                    UserProfileExpandedFragmentDirections.actionUserProfileToPackageList(
                        isAdmin = false
                    )
                )
            }
            itemAdminFinancialPackages.setOnClickListener {
                findNavController().navigate(
                    UserProfileExpandedFragmentDirections.actionUserProfileToPackageList(
                        isAdmin = true
                    )
                )
            }
            itemProfile.setOnClickListener {
              /*  findNavController().navigate(
                    UserProfileExpandedFragmentDirections.actionUserProfileToAccountInformation()
                )*/
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scheduleJob?.cancel()
    }

    private fun setupScheduleList() {
        scheduleAdapter = ScheduleAdapter()
        binding.scheduleList.adapter = scheduleAdapter
    }

    private fun subscribeObserver() {
        scheduleJob = lifecycleScope.launch {
            schedulesViewModel.allSchedulesDataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        schedulesViewModel.temp = ArrayList(dataState.data)
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
                            dataState.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", dataState.errorResponse.exception.toString())
                    }
                }
            }
        }
    }
}