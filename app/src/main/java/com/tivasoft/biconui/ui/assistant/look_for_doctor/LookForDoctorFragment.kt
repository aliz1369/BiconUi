package com.tivasoft.biconui.ui.assistant.look_for_doctor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.tivasoft.biconui.data.model.network.requests.assistant.RejectDoctorByAssistanceRequest
import com.tivasoft.biconui.data.model.network.requests.assistant.BookDoctorRequest
import com.tivasoft.biconui.data.model.network.responses.doctor.AssistanceData
import com.tivasoft.biconui.ui.assistant.look_for_doctor.adapter.DoctorAdapter
import com.tivasoft.biconui.utils.*
import com.tivasoft.biconui.utils.channel_intents.AssistanceIntent
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentLookForAssistanceBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.utils.AssistanceListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LookForDoctorFragment : BaseFragment() {
    private var _binding: FragmentLookForAssistanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LookForDoctorViewModel by viewModels()
    private lateinit var adapter: DoctorAdapter

    private var getDetailsJob: Job? = null
    private var getAllDoctorsJob: Job? = null
    private var confirmJob: Job? = null
    private var rejectJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLookForAssistanceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDoctorList()
        subscribeObservers()
        getDoctorDetails()

        binding.appBar.apply {
            title.setText(R.string.doctor_list)
            back.setOnClickListener { findNavController().popBackStack() }
            extraIcon.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                 /*   findNavController().navigate(
                        LookForDoctorFragmentDirections.actionLookForDoctorToSearchDoctor()
                    )*/
                }
            }
        }
    }

    private fun getAllDoctor() {
        lifecycleScope.launch {
            viewModel.channel.send(AssistanceIntent.GetDoctors)
        }
    }

    private fun getDoctorDetails() {
        lifecycleScope.launch {
            viewModel.channel.send(AssistanceIntent.GetDoctorDetails)
        }
    }

    private fun setupDoctorList() {
        adapter = DoctorAdapter()
        adapter.setOnAssistanceListener(object : AssistanceListener {
            override fun onAccepted(id: String) {
                lifecycleScope.launch {
                    viewModel.channel.send(
                        AssistanceIntent.ConfirmDoctor(BookDoctorRequest(id))
                    )
                }
            }

            override fun onRejected(id: String) {
                lifecycleScope.launch {
                    viewModel.channel.send(
                        AssistanceIntent.RejectDoctor(RejectDoctorByAssistanceRequest(id))
                    )
                }
            }

            override fun onClicked(assistance: AssistanceData) {
            }
        })

        binding.rvAssistanceList.adapter = adapter
    }

    private fun subscribeObservers() {
        subscribeObserverForDoctor()
        subscribeObserverForDetails()
        subscribeObserverForConfirm()
        subscribeObserverForReject()
    }

    private fun subscribeObserverForDoctor() {
        getAllDoctorsJob = lifecycleScope.launch {
            viewModel.doctorDataState.collect { doctor ->
                when (doctor) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        adapter.updateItems(doctor.data.data)
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            doctor.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", doctor.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    private fun subscribeObserverForDetails() {
        getDetailsJob = lifecycleScope.launch {
            viewModel.doctorDetailsDataState.collect { details ->
                when (details) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        adapter.updateItems(arrayListOf(details.data.data))
                        binding.appBar.extraIcon.visibility = View.GONE
                    }
                    is DataState.Failed -> {
                        binding.appBar.extraIcon.visibility = View.VISIBLE
                        val errorMessage = details.errorResponse.getErrorMessage()
                        if (errorMessage.equals("Doctor not found", true))
                            getAllDoctor()
                        else
                            Toast.makeText(
                                requireContext(),
                                details.errorResponse.getErrorMessage(),
                                Toast.LENGTH_LONG
                            ).show()
                        Log.e("TAG", details.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    private fun subscribeObserverForConfirm() {
        confirmJob = lifecycleScope.launch {
            viewModel.confirmDataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        getDoctorDetails()
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

    private fun subscribeObserverForReject() {
        rejectJob = lifecycleScope.launch {
            viewModel.rejectAssistanceDataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        getDoctorDetails()
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
        getDetailsJob?.cancel()
        getAllDoctorsJob?.cancel()
        confirmJob?.cancel()
        rejectJob?.cancel()
    }
}