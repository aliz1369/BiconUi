package com.tivasoft.biconui.ui.patient.map.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tivasoft.biconui.ui.patient.map.ClinicFragment
import com.tivasoft.biconui.ui.patient.map.MapFragment

class ClinicTabAdapter(
    fragment: MapFragment,
    private val attachments: List<String>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = attachments.size

    override fun createFragment(position: Int): Fragment =
        ClinicFragment(attachments[position])
}