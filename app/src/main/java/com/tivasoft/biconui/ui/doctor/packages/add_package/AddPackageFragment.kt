package com.tivasoft.biconui.ui.doctor.packages.add_package

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tivasoft.biconui.data.model.local.ui.ItemEntity
import com.tivasoft.biconui.data.model.network.requests.doctor.CreatePackageRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.ItemPackageData
import com.tivasoft.biconui.data.model.network.responses.doctor.ItemData
import com.tivasoft.biconui.ui.doctor.packages.add_package.adapter.SpinnerItemAdapter
import com.tivasoft.biconui.ui.doctor.packages.add_package.adapter.ItemInPackageAdapter
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.PackageIntent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentAddPackageBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.utils.ItemPackageRemoveListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddPackageFragment : BaseFragment(), ItemPackageRemoveListener {
    private var _binding: FragmentAddPackageBinding? = null
    private val binding get() = _binding!!

    private val addPackageViewModel: AddPackageViewModel by viewModels()

    private val packageItems = ArrayList<ItemEntity>()
    private lateinit var itemInPackageAdapter: ItemInPackageAdapter
    private val itemPackageData = ArrayList<ItemPackageData>()
    private lateinit var addItemBehavior: BottomSheetBehavior<ConstraintLayout>
    private var id: String = ""
    private var title: String = ""
    private var unit: Int = 0
    private var value: Int = 0
    private var free: Boolean = false

    private var createPackageJob: Job? = null
    private var periodsJob: Job? = null
    private var itemsJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPackageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addItemBehavior = BottomSheetBehavior.from(binding.clBottomSheet)
        setUpAdapter()
        getPeriods()
        subscribeObservers()
        binding.apply {
            btnAddItems.setOnClickListener {
                getItemType()
            }
            btnAddBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnAddItem.setOnClickListener {
                value = if (etTimeOrCount.visibility == View.VISIBLE) {
                    etTimeOrCount.text.toString().toInt()
                } else {
                    0
                }
                val entity = ItemPackageData(
                    name = title,
                    value = value,
                    free = free,
                    unit = unit
                )
                itemPackageData.add(entity)
                itemInPackageAdapter.updateItems(itemPackageData)
                addItemBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            btnAddPackage.setOnClickListener {
                when {
                    etTitle.text.isNullOrBlank() -> {
                        etTitle.error = getString(R.string.empty_filed)
                    }
                    etDescription.text.isNullOrBlank() -> {
                        etDescription.error = getString(R.string.empty_filed)
                    }
                    etPrice.text.isNullOrBlank() -> {
                        etPrice.error = getString(R.string.empty_filed)
                    }
                    itemPackageData.isEmpty() -> Toast.makeText(
                        requireContext(),
                        R.string.items_empty_error,
                        Toast.LENGTH_LONG
                    )
                        .show()
                    else -> {
                        val entity = CreatePackageRequest(
                            title = etTitle.text.toString(),
                            description = etDescription.text.toString(),
                            price = etPrice.text.toString().toInt(),
                            period = spinnerExpireTime.selectedItem.toString(),
                            items = itemPackageData
                        )
                        lifecycleScope.launch {
                            addPackageViewModel.channel.send(PackageIntent.CreatePackage(entity))
                        }
                    }
                }
            }
        }
    }

    private fun getPeriods() {
        lifecycleScope.launch {
            addPackageViewModel.channel.send(PackageIntent.GetPeriods)
        }
    }

    private fun getItemType() {
        lifecycleScope.launch {
            addPackageViewModel.channel.send(PackageIntent.GetItems)
        }
    }

    private fun setUpAdapter() {
        itemInPackageAdapter = ItemInPackageAdapter(this)
        binding.rvPackageItems.adapter = itemInPackageAdapter
    }

    private fun subscribeObservers() {
        subscribeObserverForCreate()
        subscribeObserverForPeriods()
        subscribeObserverForItems()
    }

    private fun subscribeObserverForCreate() {
        createPackageJob = lifecycleScope.launch {
            addPackageViewModel.createPackageDataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        findNavController().popBackStack()
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

    private fun subscribeObserverForPeriods() {
        periodsJob = lifecycleScope.launch {
            addPackageViewModel.periodDataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        val data = dataState.data.data
                        val period = ArrayList<String>()
                        for (item in data.indices) {
                            period.add(data[item].month)
                        }
                        val dataAdapter: ArrayAdapter<String> =
                            ArrayAdapter<String>(
                                requireContext(),
                                R.layout.spinner_item, period
                            )

                        binding.spinnerExpireTime.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                }

                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    p2: Int,
                                    p3: Long
                                ) {
                                }
                            }
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerExpireTime.adapter = dataAdapter
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

    private fun subscribeObserverForItems() {
        itemsJob = lifecycleScope.launch {
            addPackageViewModel.itemDataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        val data = dataState.data.data
                        val items = ArrayList<ItemData>()
                        for (item in data.indices) {
                            items.add(data[item])
                        }
                        val spinnerArrayAdapter =
                            SpinnerItemAdapter(
                                requireContext(),
                                R.layout.spinner_item,
                                items
                            )

                        binding.spinnerItems.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }

                                @SuppressLint("SetTextI18n")
                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    position: Int,
                                    p3: Long
                                ) {
                                    val item = spinnerArrayAdapter.getItem(position)!!
                                    id = item.id
                                    unit = item.unit
                                    title = item.title
                                    if (item.unit < 3) {
                                        free = false
                                        binding.etTimeOrCount.visibility = View.VISIBLE
                                    } else {
                                        free = true
                                        binding.etTimeOrCount.visibility = View.GONE
                                    }
                                }
                            }
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerItems.adapter = spinnerArrayAdapter
                        addItemBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        createPackageJob?.cancel()
        periodsJob?.cancel()
        itemsJob?.cancel()
    }

    override fun onRemoveAction(item: ItemPackageData) {
        itemInPackageAdapter.removeItems(item)
        itemPackageData.remove(item)
    }
}