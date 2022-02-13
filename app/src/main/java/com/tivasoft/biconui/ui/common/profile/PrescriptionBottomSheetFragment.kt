package com.tivasoft.biconui.ui.common.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.tivasoft.biconui.data.model.local.ui.chat.SocketMessage
import com.tivasoft.biconui.data.model.network.responses.common.PrescriptionType
import com.tivasoft.biconui.ui.common.profile.conversation.ConversationViewModel
import com.tivasoft.biconui.ui.doctor.tab_prescriptions.PrescriptionsAdapter
import com.tivasoft.biconui.ui.doctor.tab_prescriptions.PrescriptionsViewModel
import com.tivasoft.biconui.utils.*
import com.tivasoft.biconui.utils.channel_intents.SearchIntent
import com.tivasoft.biconui.utils.channel_intents.SocketIntent
import com.tivasoft.biconui.ui.MainActivity
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.BottomSheetPrescriptionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PrescriptionBottomSheetFragment : Fragment() {
    private var _binding: BottomSheetPrescriptionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PrescriptionsViewModel by viewModels()
    private val conversationViewModel: ConversationViewModel by viewModels()

    private lateinit var prescriptionsAdapter: PrescriptionsAdapter

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var uiJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPrescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getAllPrescription()
        subscribeObserver()
    }

    private fun setupRecyclerView() {
        prescriptionsAdapter = PrescriptionsAdapter()
        prescriptionsAdapter.setOnListClickListener { type, id ->
            lifecycleScope.launch {
                val entity = SocketMessage(
                    sender = sharedPreferences.getString("userId", "") ?: "",
                    receiver = sharedPreferences.getString("conversationId", "") ?: "",
                    content = id,
                    type = PrescriptionType.values().first { it.value == type }
                        .name.toLowerCase(Locale.getDefault())
                )
                conversationViewModel.channel.send(SocketIntent.SendMessage(entity))
                (requireActivity() as MainActivity).setPrescriptionBottomSheetState(false)
            }
        }

        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        layoutManager.spanSizeLookup = spanSizeLookup

        val spacing = requireContext()
            .resources
            .getDimensionPixelSize(R.dimen._16sdp)
        val itemDecoration = GridItemDecoration(spacing)

        binding.apply {
            prescriptionList.layoutManager = layoutManager
            prescriptionList.addItemDecoration(itemDecoration)
            prescriptionList.adapter = prescriptionsAdapter
        }
    }

    private val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return prescriptionsAdapter.getItemViewType(position)
        }
    }

    private fun getAllPrescription() {
        lifecycleScope.launch {
            viewModel.channel.send(SearchIntent.GetAllPrescription)
        }
    }

    private fun subscribeObserver() {
        uiJob = lifecycleScope.launch {
            viewModel.allPrescriptionDataState.collect { prescriptions ->
                when (prescriptions) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        prescriptionsAdapter.updateItems(prescriptions.data.data)
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            prescriptions.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", prescriptions.errorResponse.exception.toString())
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