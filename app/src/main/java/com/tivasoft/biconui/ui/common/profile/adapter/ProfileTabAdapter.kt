package com.tivasoft.biconui.ui.common.profile.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tivasoft.biconui.ui.common.profile.ProfileFragment
import com.tivasoft.biconui.ui.patient.tab_doctor.DoctorFragment
import com.tivasoft.biconui.ui.patient.tab_userprofile.UserProfileFragment

class ProfileTabAdapter(fragment: ProfileFragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserProfileFragment()
            else -> DoctorFragment()
        }
    }
}