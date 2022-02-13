package com.tivasoft.biconui.ui.doctor.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.tivasoft.biconui.data.model.local.ui.SearchDoctorFilterModel
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentFilterBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * Contains Filter UI.
 */
@AndroidEntryPoint
class FilterFragment : BaseFragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            if (sharedPreferences.contains("userId")) {
                root.setBackgroundResource(R.color.doctor_profile_background)
                val textColor = ContextCompat.getColor(requireContext(), R.color.welcome_bg)
                val tint = ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.profile_background
                )
                tvFilter.setTextColor(textColor)
                tvGender.setTextColor(textColor)
                tvGrade.setTextColor(textColor)
                maleRadioButton.apply {
                    setTextColor(textColor)
                    backgroundTintList = tint
                }
                femaleRadioButton.apply {
                    setTextColor(textColor)
                    backgroundTintList = tint
                }
                otherRadioButton.apply {
                    setTextColor(textColor)
                    backgroundTintList = tint
                }
                grade3.apply {
                    setTextColor(textColor)
                    backgroundTintList = tint
                }
                grade5.apply {
                    setTextColor(textColor)
                    backgroundTintList = tint
                }
                grade7.apply {
                    setTextColor(textColor)
                    backgroundTintList = tint
                }
            }
            btnConfirm.setOnClickListener {
                val genderId: Int = rgGender.checkedRadioButtonId
                val radioButtonGender = view.findViewById<RadioButton>(genderId)
                val gradeId: Int = rgGrade.checkedRadioButtonId
                val radioButtonGrade = view.findViewById<RadioButton>(gradeId)

                val gender = if (rgGender.checkedRadioButtonId != -1) {
                    radioButtonGender.text.toString().lowercase(Locale.getDefault())
                } else ""
                val grade = if (rgGrade.checkedRadioButtonId != -1) {
                    radioButtonGrade.text.toString()
                } else ""

                findNavController().apply {
                    previousBackStackEntry?.savedStateHandle?.apply {
                        val filter = SearchDoctorFilterModel(
                            genderType = gender,
                            grade = grade
                        )
                        set("filter", filter)
                    }
                    popBackStack()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}