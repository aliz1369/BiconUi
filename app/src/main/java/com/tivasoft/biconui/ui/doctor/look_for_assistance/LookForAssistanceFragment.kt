package com.tivasoft.biconui.ui.doctor.look_for_assistance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tivasoft.biconui.data.model.network.requests.doctor.RejectAssistanceRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.BookAssistance
import com.tivasoft.biconui.data.model.network.responses.doctor.AssistanceData
import com.tivasoft.biconui.ui.doctor.look_for_assistance.LookForAssistanceFragmentDirections
import com.tivasoft.biconui.ui.doctor.look_for_assistance.adapter.AssistanceAdapter
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.DoctorIntent
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentLookForAssistanceBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.utils.AssistanceListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LookForAssistanceFragment : BaseFragment() {
    private var _binding: FragmentLookForAssistanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LookForAssistanceViewModel by viewModels()

    private lateinit var assistanceAdapter: AssistanceAdapter
    private var assistantJob: Job? = null
    private var detailsJob: Job? = null
    private var confirmJob: Job? = null
    private var rejectJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLookForAssistanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAssistanceList()
        subscribeObservers()
        getAssistanceDetails()

        binding.appBar.apply {
            title.setText(R.string.assistance_list)
            back.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun getAssistanceDetails() {
        lifecycleScope.launch {
            viewModel.channel.send(DoctorIntent.GetAssistanceDetails)
        }
    }

    private fun setupAssistanceList() {
        assistanceAdapter = AssistanceAdapter()
        assistanceAdapter.setOnAssistanceListener(object : AssistanceListener {
            override fun onAccepted(id: String) {
                lifecycleScope.launch {
                    viewModel.channel.send(
                        DoctorIntent.ConfirmAssistance(BookAssistance(id))
                    )
                }
            }

            override fun onRejected(id: String) {
                lifecycleScope.launch {
                    viewModel.channel.send(
                        DoctorIntent.RejectAssistance(RejectAssistanceRequest(id))
                    )
                }
            }

            override fun onClicked(assistance: AssistanceData) {
                findNavController().navigate(
                    LookForAssistanceFragmentDirections
                        .actionLookForAssistanceToAssistanceAttachments(assistance)
                )
            }
        })
        binding.rvAssistanceList.adapter = assistanceAdapter
    }

    private fun getAllAssistance() {
        lifecycleScope.launch {
            viewModel.channel.send(DoctorIntent.GetAllAssistance)
        }
    }

    private fun subscribeObservers() {
        subscribeObserverForAssistance()
        subscribeObserverForDetails()
        subscribeObserverForConfirm()
        subscribeObserverForReject()
    }

    private fun subscribeObserverForAssistance() {
        assistantJob = lifecycleScope.launch {
            viewModel.assistanceDataState.collect { assistance ->
                when (assistance) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        assistanceAdapter.updateItems(assistance.data.data)
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            assistance.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", assistance.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    private fun subscribeObserverForDetails() {
        detailsJob = lifecycleScope.launch {
            viewModel.assistanceDetailsDataState.collect { details ->
                when (details) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        assistanceAdapter.updateItems(arrayListOf(details.data.data))
                    }
                    is DataState.Failed -> {
                        val errorMessage = details.errorResponse.getErrorMessage()
                        if (errorMessage.equals("Assistant not found", true))
                            getAllAssistance()
                        else
                            Toast.makeText(
                                requireContext(),
                                errorMessage,
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
                        getAssistanceDetails()
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
                        getAssistanceDetails()
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
        assistantJob?.cancel()
        detailsJob?.cancel()
        confirmJob?.cancel()
        rejectJob?.cancel()
    }
}