package com.tivasoft.biconui.ui.patient.tab_doctor

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.maps.model.CustomMapStyleOptions
import com.tivasoft.biconui.data.model.network.responses.doctor.SpecialtiesData
import com.tivasoft.biconui.ui.patient.search.SearchViewModel
import com.tivasoft.biconui.ui.patient.tab_doctor.adapter.SpecialtiesAdapter
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.SearchIntent
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.tivasoft.biconui.databinding.FragmentDoctorBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.ui.common.profile.ProfileFragmentDirections
import com.tivasoft.biconui.utils.SpecialtyItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DoctorFragment : BaseFragment(), SpecialtyItemListener {

    private var _binding: FragmentDoctorBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var specialtiesAdapter: SpecialtiesAdapter
    private var uiJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        subscribeObserver()
        requestLocationPermission(savedInstanceState)
    }

    private fun setupAdapter(){
        binding.apply {
            val layoutManager = LinearLayoutManager(requireContext())
            rvSpecialty.layoutManager = layoutManager
            rvSpecialty.itemAnimator = DefaultItemAnimator()
            specialtiesAdapter = SpecialtiesAdapter()
            specialtiesAdapter.setOnItemClickListener(this@DoctorFragment)
            rvSpecialty.adapter = specialtiesAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.map.onDestroy()
        _binding = null
        uiJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    private fun navigateToSearch(category: String, categoryId: String) {
        val action = if (category.isEmpty()) {
            ProfileFragmentDirections.actionProfileToSearch()
        } else {
            ProfileFragmentDirections.actionProfileToSearchByCategory(
                categoryId = categoryId,
                searchCategory = category
            )
        }
        findNavController().navigate(action)
    }

    private fun navigateToMap() {
        findNavController().navigate(
            ProfileFragmentDirections.actionProfileToMap()
        )
    }

    private fun requestLocationPermission(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            searchViewModel.channel.send(SearchIntent.GetAllSpecialties)
        }
        Dexter.withContext(context)
            .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    binding.apply {
                        map.onCreate(savedInstanceState)
                        map.map.setCustomMapStyle(
                            CustomMapStyleOptions()
                                .setEnable(true)
                                .setStyleDataPath(
                                    getFileFromAssets(
                                        requireContext(),
                                        "style.data"
                                    ).absolutePath
                                )
                                .setStyleExtraPath(
                                    getFileFromAssets(
                                        requireContext(),
                                        "style_extra.data"
                                    ).absolutePath
                                )
                        )
                        val mUiSettings = map.map.uiSettings
                        mUiSettings.setAllGesturesEnabled(false)
                        mUiSettings.isZoomControlsEnabled = false
                        mUiSettings.isCompassEnabled = false
                        mUiSettings.isScaleControlsEnabled = false
                        mUiSettings.setZoomInByScreenCenter(false)
                        doctorSearch.setOnClickListener {
                            navigateToSearch("", "")
                        }
                        map.map.setOnMapClickListener {
                            navigateToMap()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    response?.let {
                        if (response.isPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }
                }
            }).check()
    }

    private fun subscribeObserver() {
        uiJob = lifecycleScope.launch {
            searchViewModel.allSpecialtiesDataState.collect {
                when (it) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        specialtiesAdapter.updateItems(it.data.data)
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

    override fun onSpecialtyAction(specialty: SpecialtiesData) {
        navigateToSearch(specialty.name, specialty.id)
    }
}