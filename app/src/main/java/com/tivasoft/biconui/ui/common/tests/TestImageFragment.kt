package com.tivasoft.biconui.ui.common.tests

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tivasoft.biconui.data.model.network.requests.test.TestResultRequest
import com.tivasoft.biconui.data.model.network.responses.tests.TestData
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.TestIntent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.QuestionImageBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TestImageFragment : BaseFragment() {
    private var _binding: QuestionImageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TestViewModel by viewModels()
    private lateinit var testData: TestData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = QuestionImageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObserver()
        testData = requireArguments().getParcelable("testData")!!

        binding.apply {
            loadNext()
            btnNext.setOnClickListener {
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
            tvQuestionTitle.text = item.question
            answer1.setOnClickListener {
                viewModel.scoreList[viewModel.currentIndex] = item.options[0].score
            }
            answer2.setOnClickListener {
                viewModel.scoreList[viewModel.currentIndex] = item.options[1].score
            }
            answer3.setOnClickListener {
                viewModel.scoreList[viewModel.currentIndex] = item.options[2].score
            }
            answer4.setOnClickListener {
                viewModel.scoreList[viewModel.currentIndex] = item.options[3].score
            }
            btnNext.text = getString(
                R.string.test_next,
                viewModel.currentIndex + 1,
                testData.items.size
            )
            Glide.with(requireContext()).load(item.options[0].itemValue)
                .into(object : CustomViewTarget<RadioButton, Drawable>(answer1) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        answer1.buttonDrawable = resource
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                    }

                    override fun onResourceCleared(placeholder: Drawable?) {
                    }
                })
            Glide.with(requireContext()).load(item.options[1].itemValue)
                .into(object : CustomViewTarget<RadioButton, Drawable>(answer2) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        answer2.buttonDrawable = resource
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                    }

                    override fun onResourceCleared(placeholder: Drawable?) {
                    }
                })
            Glide.with(requireContext()).load(item.options[2].itemValue)
                .into(object : CustomViewTarget<RadioButton, Drawable>(answer3) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        answer3.buttonDrawable = resource
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                    }

                    override fun onResourceCleared(placeholder: Drawable?) {
                    }
                })
            Glide.with(requireContext()).load(item.options[3].itemValue)
                .into(object : CustomViewTarget<RadioButton, Drawable>(answer4) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        answer4.buttonDrawable = resource
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                    }

                    override fun onResourceCleared(placeholder: Drawable?) {
                    }
                })
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