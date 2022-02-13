package com.tivasoft.biconui.ui.common.profile.add_schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tivasoft.biconui.data.model.network.responses.doctor.AllPatientData
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.DoctorIntent
import com.tivasoft.biconui.databinding.BottomSheetPatientListBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.utils.PatientItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PatientListFragment : BaseFragment(), PatientItemListener {
    private var _binding: BottomSheetPatientListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientListViewModel by viewModels()

    private lateinit var adapter: PatientListAdapter
    private var uiJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPatientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObserver()
        allPatient()
        adapter = PatientListAdapter(this)
        binding.apply {
            patientList.adapter = adapter
        }
    }

    private fun allPatient() {
        lifecycleScope.launch {
            viewModel.channel.send(DoctorIntent.GetAllPatient)
        }
    }

    fun subscribeObserver() {
        uiJob = lifecycleScope.launch {
            viewModel.allPatientDataState.collect {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        uiJob?.cancel()
    }

    override fun onPatientAction(patient: AllPatientData) {
        findNavController().navigate(
            PatientListFragmentDirections.actionPatientListToAddPatientSchedule(
                isEdit = false,
                id = patient.id,
                patientName = patient.patientName,
                patientPhoto = patient.patientIconUrl ?: ""
            )
        )
    }
}