package com.tivasoft.biconui.ui.patient.mood_level

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.tivasoft.biconui.data.model.local.ui.MoodLevel
import com.tivasoft.biconui.ui.patient.mood_level.adapter.MoodLevelAdapter
import com.tivasoft.biconui.databinding.FragmentMoodLevelBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoodLevelFragment : BaseFragment() {
    private var _binding: FragmentMoodLevelBinding? = null
    private val binding get() = _binding!!
    private val moods = ArrayList<MoodLevel>()
    private lateinit var moodAdapter: MoodLevelAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoodLevelBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()

        binding.apply {
            moodAdapter = MoodLevelAdapter(moods)
            val layoutManager = LinearLayoutManager(context)
            rvMoodLevel.layoutManager = layoutManager
            rvMoodLevel.itemAnimator = DefaultItemAnimator()
            rvMoodLevel.adapter = moodAdapter

        }
    }

    private fun setData() {
        val mood1 = MoodLevel("text 1", "name test 1", "date test 1", "+1")
        val mood2 = MoodLevel("text 2", "name test 2", "date test 2", "+5")
        val mood3 = MoodLevel("text 3", "name test 3", "date test 3", "+3")
        val mood4 = MoodLevel("text 4", "name test 4", "date test 4", "-5")
        val mood5 = MoodLevel("text 5", "name test 5", "date test 5", "+7")
        moods.add(mood1)
        moods.add(mood2)
        moods.add(mood3)
        moods.add(mood4)
        moods.add(mood5)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}