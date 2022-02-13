package com.tivasoft.biconui.ui.common.profile.conversation_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tivasoft.biconui.data.model.network.requests.doctor.PatientIdModel
import com.tivasoft.biconui.ui.common.profile.adapter.ConversationListAdapter
import com.tivasoft.biconui.ui.patient.tab_userprofile.UserProfileViewModel
import com.tivasoft.biconui.utils.*
import com.tivasoft.biconui.utils.channel_intents.AuthIntent
import com.tivasoft.biconui.utils.channel_intents.ConversationIntent
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentChatListBinding
import com.tivasoft.biconui.ui.MainActivity
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.utils.ConversationActionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConversationListFragment : BaseFragment(), ConversationActionsListener {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserProfileViewModel by viewModels()
    private val viewModel: ConversationListViewModel by viewModels()

    private lateinit var conversationListAdapter: ConversationListAdapter

    private var isAssistance = false
    private var isPatient = true

    private var uiJob: Job? = null
    private var getMeJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isPatient = sharedPreferences.getInt("accountType", 1) == 1
        if (isPatient) {
            binding.apply {
                root.setBackgroundResource(R.drawable.menu_top_round)
                root.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.doctor_profile_background
                )
            }
        }
        binding.apply {
            menuChat.setOnClickListener {
                (requireActivity() as MainActivity).setChatBottomSheetDrag(true)
            }
        }
        subscribeObserver()
        subscribeGetMeObserver()
        getMe()
        setupAdapters()
        setupConversationList()
    }

    private fun setupAdapters() {
        conversationListAdapter = ConversationListAdapter()
        conversationListAdapter.setOnItemClickListener(this)
    }

    private fun setupConversationList() {
        binding.conversationList.adapter = conversationListAdapter
        lifecycleScope.launch {
            if (isPatient)
                viewModel.channel.send(ConversationIntent.GetDoctorsByPatient)
            else
                viewModel.channel.send(ConversationIntent.GetAllPatientOfDoctor)
        }
    }

    private fun getMe() {
        lifecycleScope.launch {
            userViewModel.channel.send(AuthIntent.GetMeIntent)
        }
    }


    private fun subscribeObserver() {
        uiJob = lifecycleScope.launch {
            viewModel.dataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        val items = viewModel.sortByType(dataState.data.data)
                        conversationListAdapter.updateItems(items)
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

    private fun subscribeGetMeObserver() {
        getMeJob = lifecycleScope.launch {
            userViewModel.dataState.collect { getMe ->
                when (getMe) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        binding.apply {
                            if (isPatient) {
                                name.text = getString(R.string.doctor_list)
                                val params = name.layoutParams as ConstraintLayout.LayoutParams
                                params.apply {
                                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                                    startToEnd = ConstraintLayout.LayoutParams.UNSET
                                    marginStart = 0
                                }
                                name.layoutParams = params
                                name.requestLayout()
                                // icon.visibility = View.INVISIBLE
                                eduFieldAndDegree.visibility = View.INVISIBLE
                            } else {
                                name.text = getMe.data.data.fullName
                                /* Glide.with(requireContext()).load(getMe.data.data.photo)
                                     .placeholder(R.drawable.rounded_edit_text)
                                     .error(R.drawable.rounded_edit_text)
                                     .into(icon)*/
                                isAssistance = getMe.data.data.doctor?.isAssistant!!
                                eduFieldAndDegree.text = getString(
                                    R.string.field_and_degree,
                                    getMe.data.data.doctor?.educationField,
                                    getMe.data.data.doctor?.educationDegree
                                )
                            }
                        }
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            getMe.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", getMe.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onConfirmAction(patientId: String) {
        if (isAssistance) {
            Toast.makeText(
                requireContext(),
                getString(R.string.assistant_patient_list_error, getString(R.string.confirm)),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            lifecycleScope.launch {
                viewModel.channel.send(
                    ConversationIntent.ConfirmNewPatient(PatientIdModel(patientId))
                )
            }
        }
    }

    override fun onRejectAction(patientId: String) {
        if (isAssistance) {
            Toast.makeText(
                requireContext(),
                getString(R.string.assistant_patient_list_error, getString(R.string.reject)),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            lifecycleScope.launch {
                viewModel.channel.send(
                    ConversationIntent.RejectPatient(PatientIdModel(patientId))
                )
            }
        }
    }

    override fun onItemClickListener(conversationId: String, name: String) {
        findNavController().navigate(
            ConversationListFragmentDirections.actionConversationListToConversation(
                conversationId = conversationId,
                name = name
            )
        )
    }
}