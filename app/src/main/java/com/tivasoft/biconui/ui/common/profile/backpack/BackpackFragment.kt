package com.tivasoft.biconui.ui.common.profile.backpack

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tivasoft.biconui.data.model.network.responses.common.Backpack
import com.tivasoft.biconui.data.model.network.responses.tests.TestData
import com.tivasoft.biconui.utils.GridItemDecoration
import com.tivasoft.biconui.ui.common.tests.TestViewModel
import com.tivasoft.biconui.ui.common.tests.TestsNameFragment
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.TestIntent
import com.tivasoft.biconui.ui.MainActivity
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentBackpackBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BackpackFragment(
    private val items: MutableList<Backpack>
) : BaseFragment() {

    private var _binding: FragmentBackpackBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TestViewModel by viewModels()

    private lateinit var backpackRecyclerAdapter: BackpackRecyclerAdapter
    private var uiJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBackpackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        uiJob?.cancel()
    }

    private fun setupRecyclerView() {
        val spacing = requireContext()
            .resources
            .getDimensionPixelSize(R.dimen._8sdp)
        val itemDecoration = GridItemDecoration(spacing, isBackpack = true)

        backpackRecyclerAdapter = BackpackRecyclerAdapter()
        binding.apply {
            backpackList.adapter = backpackRecyclerAdapter
            backpackList.addItemDecoration(itemDecoration)
        }

        backpackRecyclerAdapter.updateItems(items)
        backpackRecyclerAdapter.setOnItemClickListener { itemPosition ->
            lifecycleScope.launch {
                val selectedTestId = items[itemPosition].id
                viewModel.channel.send(TestIntent.GetSingleTest(selectedTestId))
            }
        }
    }

    private fun subscribeObserver() {
        uiJob = lifecycleScope.launch {
            viewModel.testItem.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        navigateToTest(dataState.data.data)
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

    private fun navigateToTest(data: TestData) {
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            val fragment = TestsNameFragment()
            val bundle = Bundle()
            bundle.putParcelable("testData", data)
            fragment.arguments = bundle
            addToBackStack("backpack")
            replace(R.id.fragment_bottom_sheet_test, fragment)
        }
        (requireActivity() as MainActivity)
            .setBottomSheetState(true)
    }

}