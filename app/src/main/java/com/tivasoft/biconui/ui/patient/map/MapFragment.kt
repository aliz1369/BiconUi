package com.tivasoft.biconui.ui.patient.map

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdate
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.district.DistrictItem
import com.amap.api.services.district.DistrictResult
import com.amap.api.services.district.DistrictSearch
import com.amap.api.services.district.DistrictSearchQuery
import com.tivasoft.biconui.data.model.network.requests.doctor.AllClinicRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.LocationClinic
import com.tivasoft.biconui.ui.patient.map.adapter.ClinicTabAdapter
import com.tivasoft.biconui.ui.patient.map.adapter.DistrictAdapter
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.PatientIntent
import com.google.android.material.tabs.TabLayoutMediator
import com.mancj.materialsearchbar.MaterialSearchBar
import com.tivasoft.biconui.databinding.FragmentMapBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.utils.DistrictItemListener
import com.tivasoft.biconui.utils.TopSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.pow

@AndroidEntryPoint
class MapFragment : BaseFragment(), AMapGestureListener, AMap.OnMarkerClickListener,
    DistrictSearch.OnDistrictSearchListener, MaterialSearchBar.OnSearchActionListener,
    DistrictItemListener {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()

    private lateinit var clinicTabAdapter: ClinicTabAdapter
    private lateinit var locationClient: AMapLocationClient
    private lateinit var districtSearch: DistrictSearch
    private lateinit var districtAdapter: DistrictAdapter

    private var uiJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObserver()

        binding.mapPlaceholder.onCreate(savedInstanceState)
        askPermissions()
        setupDistrictSearch()
        setupClinicBottomSheet()
        setupAMap()
        setupLocation()
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            100
        )
    }

    private fun setupDistrictSearch() {
        binding.apply {
            mapSearch.setOnSearchActionListener(this@MapFragment)
            val layoutManager = LinearLayoutManager(requireContext())
            rvDistrictList.layoutManager = layoutManager
            rvDistrictList.itemAnimator = DefaultItemAnimator()
            districtAdapter = DistrictAdapter(this@MapFragment)
            rvDistrictList.adapter = districtAdapter

            districtSearch = DistrictSearch(requireContext())
            districtSearch.setOnDistrictSearchListener(this@MapFragment)
            mapPlaceholder.map.setOnMapClickListener {
                bottomSheetClinic.root.apply {
                    mapSearch.visibility = View.VISIBLE
                    visibility = View.GONE
                }
            }
        }
    }

    private fun setupClinicBottomSheet() {
        binding.bottomSheetClinic.apply {
            val behavior = TopSheetBehavior.from(bottomSheetClinicLayout)
            behavior.state = TopSheetBehavior.STATE_COLLAPSED
            behavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == TopSheetBehavior.STATE_EXPANDED) {
                        sliderLabel.visibility = View.VISIBLE
                        clinicImageSliderLayout.visibility = View.VISIBLE
                        clinicAvailability.visibility = View.GONE
                    } else if (newState == TopSheetBehavior.STATE_COLLAPSED) {
                        sliderLabel.visibility = View.GONE
                        clinicImageSliderLayout.visibility = View.GONE
                        clinicAvailability.visibility = View.VISIBLE
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
    }

    private fun setupAdapter(attachments: List<String>) {
        binding.bottomSheetClinic.apply {
            clinicTabAdapter = ClinicTabAdapter(
                this@MapFragment, attachments
            )
            pager.adapter = clinicTabAdapter
            TabLayoutMediator(tabLayout, pager) { _, _ ->
            }.attach()
        }
    }

    private fun setupAMap() {
        binding.apply {
            val locationStyle = MyLocationStyle()
            locationStyle.interval(2000)
            locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
            locationStyle.showMyLocation(true)
            mapPlaceholder.map.apply {
                myLocationStyle = locationStyle
                isMyLocationEnabled = true
                uiSettings.isMyLocationButtonEnabled = true
                setAMapGestureListener(this@MapFragment)
                setOnMarkerClickListener(this@MapFragment)
                setOnMapClickListener {
                    bottomSheetClinic.root.apply {
                        mapSearch.visibility = View.VISIBLE
                        visibility = View.GONE
                    }
                }
            }
        }
        setupMapStyle()
    }

    private fun setupMapStyle() {
        binding.mapPlaceholder.map.setCustomMapStyle(
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
    }

    private fun setupLocation() {
        locationClient = AMapLocationClient(requireContext())
        val locationOptions = AMapLocationClientOption()
        locationOptions.interval = 5000
        locationOptions.isGpsFirst = true
        locationOptions.isMockEnable = true

        locationClient.setLocationOption(locationOptions)
        locationClient.startLocation()
    }

    override fun onDoubleTap(p0: Float, p1: Float) {
    }

    override fun onSingleTap(p0: Float, p1: Float) {
    }

    override fun onFling(p0: Float, p1: Float) {
    }

    override fun onScroll(p0: Float, p1: Float) {
    }

    override fun onLongPress(p0: Float, p1: Float) {
    }

    override fun onDown(p0: Float, p1: Float) {
    }

    override fun onUp(p0: Float, p1: Float) {
    }

    override fun onMapStable() {
        binding.mapPlaceholder.map.apply {
            val distance = (maxZoomLevel - cameraPosition.zoom).pow(5).toInt()
            val latLong = cameraPosition.target
            val locationEntity = LocationClinic(
                latitude = latLong.latitude,
                longitude = latLong.longitude
            )
            val entity = AllClinicRequest(
                distance = distance,
                location = locationEntity
            )
            lifecycleScope.launch {
                viewModel.channel.send(PatientIntent.GeNearClinics(entity))
            }
        }
    }

    private fun subscribeObserver() {
        uiJob = lifecycleScope.launch {
            viewModel.nearClinicDataState.collect { nearClinic ->
                when (nearClinic) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        nearClinic.data.data.map {
                            if (!viewModel.clinics.contains(it))
                                viewModel.clinics.add(it)
                            val latLong = LatLng(
                                it.location.latitude,
                                it.location.longitude
                            )
                            val markerOptions = MarkerOptions().position(latLong)
                            binding.mapPlaceholder.map.addMarker(markerOptions)
                        }
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            nearClinic.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", nearClinic.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val origin = Location("origin")
        val destination = Location("destination")
        origin.latitude = locationClient.lastKnownLocation.latitude
        origin.longitude = locationClient.lastKnownLocation.longitude
        binding.bottomSheetClinic.apply {
            val item = viewModel.clinics.firstOrNull {
                it.location.latitude == marker.position.latitude &&
                        it.location.longitude == marker.position.longitude
            }
            if (item != null) {
                destination.latitude = item.location.latitude
                destination.longitude = item.location.longitude
                clinicName.text = item.name
                degree.text = item.doctor.educationDegree
                val distanceInKm = origin.distanceTo(destination)
                distance.text = distanceInKm.toInt().toString()
                setupAdapter(item.attachment)
                binding.mapSearch.visibility = View.GONE
                root.visibility = View.VISIBLE
            }
        }
        return true
    }

    override fun onDistrictSearched(result: DistrictResult) {
        binding.rvDistrictList.visibility = View.VISIBLE
        result.district.let { districtAdapter.updateItems(it) }
    }

    override fun onSearchStateChanged(enabled: Boolean) {
    }

    override fun onSearchConfirmed(text: CharSequence?) {
        val districtSearchQuery = DistrictSearchQuery()
        districtSearchQuery.keywords = text.toString()
        districtSearch.query = districtSearchQuery
        districtSearch.searchDistrictAsyn()
    }

    override fun onButtonClicked(buttonCode: Int) {
        when (buttonCode) {
            MaterialSearchBar.BUTTON_BACK -> binding.rvDistrictList.visibility = View.GONE
        }
    }

    override fun onDistrictsAction(district: DistrictItem) {
        val mCameraUpdate: CameraUpdate = CameraUpdateFactory.newCameraPosition(
            CameraPosition(
                LatLng(district.center.latitude, district.center.longitude),
                18F,
                0F,
                0F
            )
        )
        binding.mapPlaceholder.map.animateCamera(mCameraUpdate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        locationClient.stopLocation()
        uiJob?.cancel()
    }
}