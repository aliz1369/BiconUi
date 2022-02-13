package com.tivasoft.biconui.ui.common.tests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tivasoft.biconui.databinding.QuestionStartBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestsNameFragment : BaseFragment() {
    private var _binding: QuestionStartBinding? = null
    private val binding get() = _binding!!

    private val args: TestsNameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = QuestionStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // (requireActivity() as MainActivity).setBottomSheetState(true)
        if (args.testData != null) {
            binding.apply {
                tvTitle.text = args.testData?.name
                tvTestDetails.text = args.testData?.description
                btnStart.setOnClickListener {
                    navigateToQuestions()
                }
            }
        } else {
            navigateToBreath()
        }
    }

    private fun navigateToQuestions() {
//        requireActivity().supportFragmentManager.commit {
//            setReorderingAllowed(true)
//            val fragment = when (args.testData?.type) {
//                1 -> TestImageFragment()
//                2 -> TestBooleanFragment()
//                3 -> TestTwoOptionsFragment()
//                4 -> TestMultipleChoiceFragment()
//                else -> TestNumberFragment()
//            }
//            fragment.arguments = requireArguments()
//            add(R.id.fragment_bottom_sheet_test, fragment)
//        }
    }

    private fun navigateToBreath() {
        findNavController().navigate(
            TestsNameFragmentDirections.actionTestNameToBreath()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}