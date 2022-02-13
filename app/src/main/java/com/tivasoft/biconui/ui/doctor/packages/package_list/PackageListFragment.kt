package com.tivasoft.biconui.ui.doctor.packages.package_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tivasoft.biconui.ui.doctor.packages.package_list.adapter.PackageAdapter
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.PackageIntent
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentPackageListBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PackageListFragment : BaseFragment() {
    private var _binding: FragmentPackageListBinding? = null
    private val binding get() = _binding!!

    private val packageListViewModel: PackageListViewModel by viewModels()
    private val args: PackageListFragmentArgs by navArgs()

    private lateinit var packageAdapter: PackageAdapter

    private var isPatient = false
    private var isAdmin = false
    private var doctorId = ""

    private var uiJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPackageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isPatient = sharedPreferences.getInt("accountType", 1) == 1
        isAdmin = args.isAdmin
        doctorId = args.doctorId

        if (isPatient) {
            val color = ContextCompat.getColor(
                requireContext(),
                R.color.profile_background
            )
            binding.apply {
                root.setBackgroundColor(color)
                packageAppbar.setBackgroundColor(color)
            }
        }
        getPackages()
        setAdapter()
        subscribeObserver()
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getPackages() {
        lifecycleScope.launch {
            val intent = when {
                doctorId.isNotBlank() -> PackageIntent.GetDoctorPackagesById(doctorId)
                isAdmin -> PackageIntent.GetAdminPackages
                isPatient -> PackageIntent.GetPatientPackages
                else -> PackageIntent.GetDoctorPackages
            }
            packageListViewModel.channel.send(intent)
        }
    }

    private fun setAdapter() {
        packageAdapter = PackageAdapter(isPatient)
        binding.rvPackageList.adapter = packageAdapter
        packageAdapter.setOnActivationClickListener { packageId ->
            lifecycleScope.launch {
                packageListViewModel.channel.send(PackageIntent.ChangePackageActivation(packageId))
            }
        }
    }

    private fun subscribeObserver() {
        uiJob = lifecycleScope.launch {
            packageListViewModel.packageDataState.collect { packages ->
                when (packages) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        packageAdapter.updateItems(packages.data.data)
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            packages.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", packages.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        uiJob?.cancel()
    }
}