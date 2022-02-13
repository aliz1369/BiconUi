package com.tivasoft.biconui.ui.patient.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.tivasoft.biconui.data.model.network.requests.patient.BookRequest
import com.tivasoft.biconui.data.model.network.requests.patient.FilterModel
import com.tivasoft.biconui.data.model.network.responses.auth.SearchData
import com.tivasoft.biconui.ui.patient.search.adapter.SearchAdapter
import com.tivasoft.biconui.ui.patient.search.adapter.SpecialtiesAdapter
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.SearchIntent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tivasoft.biconui.databinding.FragmentSearchBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.utils.SearchedItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SearchFragment : BaseFragment(), SearchedItemListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var specialtiesAdapter: SpecialtiesAdapter
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>

    private var allDoctorsJob: Job? = null
    private var specialtiesJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBottomSheet()
        subscribeObservers()

        binding.apply {
//            if (arguments != null && requireArguments().getBoolean("isFromQuestionnaire")) {
//                //     showSearch()
//                searchText.visibility = View.GONE
//                searchFilter.visibility = View.GONE
//            }
            searchClose.setOnClickListener {
                searchText.setText("")
                searchClose.visibility = View.GONE
                //searchFilter.visibility = View.GONE
                //   searchList.adapter = suggestionAdapter
            }
            searchText.apply {
                setOnClickListener {
                    if (text.toString().isNotEmpty())
                        searchClose.visibility = View.VISIBLE
                }
                addTextChangedListener {
                    searchClose.visibility = View.VISIBLE
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        showSearch()
                    }
                    lifecycleScope.launch {
                        viewModel.channel.send(SearchIntent.GetAllDoctor(searchText.text.toString()))
                    }
                    false
                }
            }
            searchFilter.setOnClickListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            searchBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        allDoctorsJob?.cancel()
        specialtiesJob?.cancel()
    }

    private fun setupRecyclerView() {
        specialtiesAdapter = SpecialtiesAdapter()
        searchAdapter = SearchAdapter()
        searchAdapter.setOnItemClickListener(this)
        binding.searchList.adapter = searchAdapter
    }

    private fun setupBottomSheet() {
        binding.bottomSheetSearchFilter.apply {
            rvSpecialties.adapter = specialtiesAdapter
            behavior = BottomSheetBehavior.from(searchFilterLayout)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            searchFilterClear.setOnClickListener {
                gender.clearCheck()
                consulting.clearCheck()
                classRadioGroup.clearCheck()
                specialtiesAdapter.clearCheck()
            }

            searchFilterConfirm.setOnClickListener {
                lifecycleScope.launch {
                    val entity = FilterModel(
                        genderType = if (gender.checkedRadioButtonId != -1)
                            getSelectedRadioButton(gender.checkedRadioButtonId) else "",
                        specialties = specialtiesAdapter.getSelectedId(),
                        consulting = if (consulting.checkedRadioButtonId != -1)
                            getSelectedRadioButton(consulting.checkedRadioButtonId) else "",
                        clas = if (classRadioGroup.checkedRadioButtonId != -1) getSelectedRadioButton(
                            classRadioGroup.checkedRadioButtonId
                        ) else "",
                        search = binding.searchText.text.toString()
                    )
                    viewModel.channel.send(SearchIntent.GetFilteredDoctor(entity))
                }
            }
        }
    }

    private fun showSearch() {
        lifecycleScope.launch {
            viewModel.channel.send(SearchIntent.GetAllSpecialties)
        }

        binding.apply {
            searchClose.visibility = View.GONE
            searchFilter.visibility = View.VISIBLE
            searchList.adapter = searchAdapter
        }
    }

    private fun getSelectedRadioButton(selectedId: Int): String =
        binding.root.findViewById<RadioButton>(selectedId).text.toString()
            .lowercase(Locale.getDefault())

    private fun subscribeObservers() {
        subscribeObserverForAllDoctors()
        subscribeObserverForAllSpecialties()
    }

    private fun subscribeObserverForAllDoctors() {
        allDoctorsJob = lifecycleScope.launch {
            viewModel.searchAllDoctorsDataState.collect { allDoctors ->
                when (allDoctors) {
                    is DataState.Success -> {
                        binding.bottomSheetSearchFilter.apply {
                            gender.clearCheck()
                            consulting.clearCheck()
                            classRadioGroup.clearCheck()
                            specialtiesAdapter.clearCheck()
                        }
                        searchAdapter.updateItems(allDoctors.data.data)
                        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            allDoctors.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", allDoctors.errorResponse.exception.toString())
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun subscribeObserverForAllSpecialties() {
        specialtiesJob = lifecycleScope.launch {
            viewModel.allSpecialtiesDataState.collect { allSpecialties ->
                when (allSpecialties) {
                    is DataState.Success -> {
                        binding.bottomSheetSearchFilter.apply {
                            val layoutManager = LinearLayoutManager(context)
                            rvSpecialties.layoutManager = layoutManager
                            rvSpecialties.itemAnimator = DefaultItemAnimator()
                            rvSpecialties.adapter = specialtiesAdapter
                            specialtiesAdapter.updateItems(allSpecialties.data.data)
                        }
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            allSpecialties.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", allSpecialties.errorResponse.exception.toString())
                    }
                    else -> Unit
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
            SearchFragmentDirections.actionSearchToPackageList(
                doctorId = doctorId
            )
        )
    }
}