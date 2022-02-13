package com.tivasoft.biconui.ui.patient.search.by_category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tivasoft.biconui.data.model.network.requests.patient.BookRequest
import com.tivasoft.biconui.data.model.network.responses.auth.SearchData
import com.tivasoft.biconui.ui.patient.search.adapter.SearchAdapter
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.SearchIntent
import com.tivasoft.biconui.databinding.FragmentSearchByCategoryBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.utils.SearchedItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchByCategoryFragment : BaseFragment(), SearchedItemListener {

    private var _binding: FragmentSearchByCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchByCategoryViewModel by viewModels()
    private val args: SearchByCategoryFragmentArgs by navArgs()

    private lateinit var adapter: SearchAdapter
    private var uiJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchByCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObserver()
        binding.apply {
            adapter = SearchAdapter()
            adapter.setOnItemClickListener(this@SearchByCategoryFragment)

            categoryTitle.text = args.searchCategory
            val id = args.categoryId
            getDoctors(id)
            searchList.adapter = adapter

            searchBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun getDoctors(id: String) {
        lifecycleScope.launch {
            viewModel.channel.send(SearchIntent.GetAllDoctorBySpecialties(id))
        }
    }

    private fun subscribeObserver() {
        uiJob = lifecycleScope.launch {
            viewModel.doctorsBySpecialtiesDataState.collect {
                when (it) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        adapter.updateItems(it.data.data)
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            it.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", it.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    override fun onBookAction(item: SearchData) {
        lifecycleScope.launch {
            viewModel.channel.send(
                SearchIntent.Book(
                    BookRequest(
                        doctor = item.id
                    )
                )
            )
        }
    }

    override fun onPackageClicked(doctorId: String) {
        findNavController().navigate(
            SearchByCategoryFragmentDirections.actionSearchToPackageList(
                doctorId = doctorId
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        uiJob?.cancel()
    }
}