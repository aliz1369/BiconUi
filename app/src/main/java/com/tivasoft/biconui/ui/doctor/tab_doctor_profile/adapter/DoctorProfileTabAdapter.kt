package com.tivasoft.biconui.ui.doctor.tab_doctor_profile.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tivasoft.biconui.ui.common.profile.ProfileFragment
import com.tivasoft.biconui.ui.doctor.tab_doctor_profile.DoctorProfileFragment
import com.tivasoft.biconui.ui.doctor.tab_prescriptions.PrescriptionsFragment

class DoctorProfileTabAdapter(fragment: ProfileFragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DoctorProfileFragment()
            1 -> PrescriptionsFragment()
            else -> DoctorProfileFragment()
        }
    }
}