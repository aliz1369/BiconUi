package com.tivasoft.biconui.ui.common.tests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentTestResultBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestResultFragment : BaseFragment() {
    private var _binding: FragmentTestResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val result = requireArguments().getInt("testResult")
        binding.apply {
            testResult.text = getString(
                R.string.test_score,
                result
            )
            finish.setOnClickListener {
                /*      (requireActivity() as MainActivity)
                          .setBottomSheetState(false)*/
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}