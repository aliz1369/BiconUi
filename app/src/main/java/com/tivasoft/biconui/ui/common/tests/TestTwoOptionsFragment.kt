package com.tivasoft.biconui.ui.common.tests

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tivasoft.biconui.data.model.network.requests.test.TestResultRequest
import com.tivasoft.biconui.data.model.network.responses.tests.TestData
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.TestIntent
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.QuestionTwoOptionBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TestTwoOptionsFragment : BaseFragment() {
    private var _binding: QuestionTwoOptionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TestViewModel by viewModels()
    private lateinit var testData: TestData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = QuestionTwoOptionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObserver()
        testData = requireArguments().getParcelable("testData")!!
        binding.apply {
            loadNext()
            btnNext.setOnClickListener {
                val score = if (answer1.isChecked) 1 else 2
                viewModel.scoreList[viewModel.currentIndex] = score
                if (viewModel.currentIndex < testData.items.size - 1) {
                    viewModel.currentIndex += 1
                    loadNext()
                } else {
                    lifecycleScope.launch {
                        val result = TestResultRequest(
                            testId = testData.id,
                            scores = viewModel.scoreList
                        )
                        viewModel.channel.send(TestIntent.SendTestResult(result))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loadNext() {
        binding.apply {
            val item = testData.items[viewModel.currentIndex]
            viewModel.scoreList.add(0)
            questionTitle.text = item.question
            answer1.text = item.options[0].itemValue
            answer2.text = item.options[1].itemValue
            btnNext.text = getString(
                R.string.test_next,
                viewModel.currentIndex + 1,
                testData.items.size
            )
        }
    }

    private fun subscribeObserver() {
        lifecycleScope.launch {
            viewModel.testResult.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        requireActivity().supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            val fragment = TestResultFragment()
                            val bundle = Bundle()
                            bundle.putInt("testResult", dataState.data.data)
                            fragment.arguments = bundle
                            replace(R.id.fragment_bottom_sheet_test, fragment)
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

}