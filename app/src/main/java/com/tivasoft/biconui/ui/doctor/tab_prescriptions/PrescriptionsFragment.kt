package com.tivasoft.biconui.ui.doctor.tab_prescriptions

import android.content.Intent
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
import androidx.recyclerview.widget.GridLayoutManager
import com.tivasoft.biconui.data.model.network.responses.common.PrescriptionType
import com.tivasoft.biconui.data.model.network.responses.tests.TestData
import com.tivasoft.biconui.utils.*
import com.tivasoft.biconui.utils.channel_intents.SearchIntent
import com.tivasoft.biconui.utils.channel_intents.TestIntent
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentPrescriptionsBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import com.tivasoft.biconui.ui.common.profile.ProfileFragmentDirections
import com.unity3d.player.UnityPlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrescriptionsFragment : BaseFragment() {
    private var _binding: FragmentPrescriptionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PrescriptionsViewModel by viewModels()
    private lateinit var prescriptionsAdapter: PrescriptionsAdapter

    private var isPatient = true
    private var prescriptionsJob: Job? = null
    private var testsJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrescriptionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isPatient = sharedPreferences.getInt("accountType", 1) == 1

        setupRecyclerView()
        subscribeObservers()
        getAllPrescription()

        if (!isPatient) {
            val color = ContextCompat.getColor(
                requireContext(),
                R.color.doctor_profile_background
            )
            binding.prescriptionLayout.setBackgroundColor(color)
        }
    }

    private fun getAllPrescription() {
        lifecycleScope.launch {
            viewModel.channel.send(SearchIntent.GetAllPrescription)
        }
    }

    private fun setupRecyclerView() {
        prescriptionsAdapter = PrescriptionsAdapter()
        prescriptionsAdapter.setOnListClickListener { type, id ->
            when (type) {
                PrescriptionType.TEST.value -> {
                    lifecycleScope.launch {
                        viewModel.testsChannel.send(TestIntent.GetSingleTest(id))
                    }
                }
                PrescriptionType.BREATH.value -> {
                    navigateToTest(null)
                }
                PrescriptionType.GAME.value -> {
                    writeIntoTextFile(
                        gameId = id,
                        doctorId = "doctorId",
                        patientId = "patientId"
                    )
                    val intent = Intent(requireContext(), UnityPlayerActivity::class.java)
                    startActivity(intent)
                }
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
            expandMore.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileToExpandedPrescription()
                )
            }
        }
    }

    private fun subscribeObservers() {
        subscribeObserverForPrescriptions()
        subscribeObserverForTest()
    }

    private fun subscribeObserverForPrescriptions() {
        prescriptionsJob = lifecycleScope.launch {
            viewModel.allPrescriptionDataState.collect { prescriptions ->
                when (prescriptions) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        val data = prescriptions.data.data.subList(
                            0, minOf(
                                if (!isPatient) 5 else 6,
                                prescriptions.data.data.size
                            )
                        )
                        prescriptionsAdapter.updateItems(ArrayList(data))
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

    private fun subscribeObserverForTest() {
        testsJob = lifecycleScope.launch {
            viewModel.testItem.collect { testItem ->
                when (testItem) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        navigateToTest(testItem.data.data)
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            testItem.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", testItem.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    private val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return prescriptionsAdapter.getItemViewType(position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        prescriptionsJob?.cancel()
        testsJob?.cancel()
    }

    private fun navigateToTest(data: TestData?) {
      //  (requireActivity() as MainActivity).setTestsNavGraph(data)
    }
}