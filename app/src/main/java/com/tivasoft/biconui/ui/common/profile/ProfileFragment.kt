package com.tivasoft.biconui.ui.common.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tivasoft.biconui.ui.common.profile.adapter.ProfileTabAdapter
import com.tivasoft.biconui.ui.doctor.tab_doctor_profile.adapter.DoctorProfileTabAdapter
import com.tivasoft.biconui.ui.doctor.tab_prescriptions.PrescriptionsViewModel
import com.tivasoft.biconui.utils.channel_intents.SearchIntent
import com.google.android.material.tabs.TabLayoutMediator
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentProfileBinding
import com.tivasoft.biconui.ui.MainActivity
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val prescriptionsViewModel: PrescriptionsViewModel by viewModels()

    private lateinit var profileTabAdapter: ProfileTabAdapter
    private lateinit var doctorProfileTabAdapter: DoctorProfileTabAdapter

    private var isPatient = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isPatient = sharedPreferences.getInt("accountType", 1) == 1
        val statusBarColorRes =
            if (isPatient) R.color.profile_background else R.color.doctor_profile_background
        setupAdapters()
        setupPager()
        (requireActivity() as MainActivity).apply {
            window.statusBarColor = requireContext().getColor(statusBarColorRes)
            setConversationNavGraph()
            setPrescriptionNavGraph()
        }

        lifecycleScope.launch {
            prescriptionsViewModel.channel.send(SearchIntent.GetAllPrescription)
        }
    }

    /**
     * Initiate ProfileTabAdapter and DoctorProfileTabAdapter.
     */
    private fun setupAdapters() {
        profileTabAdapter = com.tivasoft.biconui.ui.common.profile.adapter.ProfileTabAdapter(this)
        doctorProfileTabAdapter = DoctorProfileTabAdapter(this)
    }

    /**
     * Initial setup for Pager and TabLayout.
     */
    private fun setupPager() {
        binding.apply {
            pager.adapter = when (isPatient) {
                true -> profileTabAdapter
                false -> doctorProfileTabAdapter
            }
            TabLayoutMediator(tabLayout, pager) { _, _ ->
                if (!isPatient) {
                    val color = ContextCompat.getColor(
                        requireContext(),
                        R.color.doctor_profile_background
                    )
                    tabLayout.setBackgroundColor(color)
                    profileLayout.setBackgroundColor(color)
                }
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (requireActivity() as MainActivity).setChatBottomSheetVisibility(false)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).setChatBottomSheetVisibility(false)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setChatBottomSheetVisibility(true)
    }
}