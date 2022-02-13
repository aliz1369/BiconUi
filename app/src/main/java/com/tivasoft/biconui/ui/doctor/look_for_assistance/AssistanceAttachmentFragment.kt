package com.tivasoft.biconui.ui.doctor.look_for_assistance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tivasoft.biconui.data.model.network.requests.doctor.RejectAssistanceRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.BookAssistance
import com.tivasoft.biconui.ui.doctor.look_for_assistance.adapter.FileAdapter
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.DoctorIntent
import com.tivasoft.biconui.databinding.FragmentAssistanceAttachmentBinding
import com.tivasoft.biconui.ui.doctor.look_for_assistance.LookForAssistanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.tivasoft.biconui.BR

@AndroidEntryPoint
class AssistanceAttachmentFragment : Fragment() {
    private var _binding: FragmentAssistanceAttachmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LookForAssistanceViewModel by viewModels()
    private val args: AssistanceAttachmentFragmentArgs by navArgs()

    private lateinit var adapter: FileAdapter
    private var confirmJob: Job? = null
    private var rejectJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssistanceAttachmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val assistance = args.assistance

        subscribeObserverForConfirm()
        subscribeObserverForReject()

        val items = assistance.attachment

        binding.apply {
            setVariable(BR.assistance, assistance)
            adapter = FileAdapter()
            attachmentList.adapter = adapter
            adapter.updateItems(items)
            confirm.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.channel.send(
                        DoctorIntent.ConfirmAssistance(BookAssistance(assistance.id))
                    )
                }
            }
            reject.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.channel.send(
                        DoctorIntent.RejectAssistance(RejectAssistanceRequest(assistance.id))
                    )
                }
            }
            appBar.apply {
                title.text = assistance.fullName
                back.setOnClickListener { findNavController().popBackStack() }
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

    private fun subscribeObserverForReject() {
        rejectJob = lifecycleScope.launch {
            viewModel.rejectAssistanceDataState.collect { dataState ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        confirmJob?.cancel()
        rejectJob?.cancel()
    }
}