package com.tivasoft.biconui.ui.auth.signin_register

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentSigninregisterBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tivasoft.biconui.data.model.network.requests.auth.Login
import com.tivasoft.biconui.data.model.network.requests.auth.Register
import com.tivasoft.biconui.data.model.network.requests.auth.Verify
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.AuthIntent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.Navigation

import androidx.navigation.NavController
import com.tivasoft.biconui.ui.MainActivity


@AndroidEntryPoint
class SignInFragment : BaseFragment() {
    private var _binding: FragmentSigninregisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var registersheet: BottomSheetBehavior<ConstraintLayout>
    private val registerViewModel: RegisterViewModel by viewModels()
    private val signinViewModel: SignInViewModel by viewModels()
    private var verificationCodeJob: Job? = null
    private var meJob: Job? = null
    private var verifyCode: String = ""
    private var pass1: String = ""
    private var pass2: String = ""
    private var pass3: String = ""
    private var pass4: String = ""
    private var pass5: String = ""
    private var pass6: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninregisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startbg()
        //  val accountType = sharedPreferences.getInt("accountType", 1)
        subscribeObserver()
        val registerFragmentBinding = binding.bottomsheetRegister
        registersheet = BottomSheetBehavior.from(registerFragmentBinding.registerBottomsheet)
        // registersheet.state = BottomSheetBehavior.STATE_COLLAPSED
        if (sharedPreferences.contains("userId")) {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToProfileNavGraph())
        }
        binding.apply {
            btnSignIn.setOnClickListener {
                clMainSignin.visibility = View.GONE
                clMainSignin.startAnimation(animated("scale"))
                clDarkFade.visibility = View.VISIBLE
                clDarkFade.startAnimation(animated("fadein"))
                clSigninEnterphone.visibility = View.VISIBLE
                clSigninEnterphone.startAnimation(animated("slidein"))
                Handler(Looper.getMainLooper()).postDelayed({
                    backToMain.visibility = View.VISIBLE
                    etPhoneNumber.requestFocus()
                    showKeyboard()
                }, 500)
            }
            btnConfirm.setOnClickListener {
                if (etPhoneNumber.text.isNullOrBlank()) {
                    etPhoneNumber.error = getString(R.string.phone_number_empty)
                } /*else if (!Pattern.matches(pattern, etPhoneNumber.text.toString())) {
                    etPhoneNumber.error = getString(R.string.wront_phone_number_format)
                }*/ else {
                    val entity = Login(
                        username = etPhoneNumber.text.toString()
                    )
                    lifecycleScope.launch {
                        signinViewModel.channel.send(AuthIntent.LoginIntent(entity))
                    }
                }
            }
            backToEnterphone.setOnClickListener {
                clMainEnterconfirmcode.visibility = View.GONE
                clMainEnterconfirmcode.startAnimation(animated("slideoutright"))
                clSigninEnterphone.visibility = View.VISIBLE
                clSigninEnterphone.startAnimation(animated("slideinleft"))
            }
            backToMain.setOnClickListener {
                hideKeyboard()
                backToMain.visibility = View.GONE
                clSigninEnterphone.visibility = View.GONE
                clSigninEnterphone.startAnimation(animated("slideoutright"))
                clMainSignin.visibility = View.VISIBLE
                clMainSignin.startAnimation(animated("scalein"))
                clDarkFade.visibility = View.GONE
                clDarkFade.startAnimation(animated("fadeout"))/*
                Handler(Looper.getMainLooper()).postDelayed({
                }, 500)*/
            }
            binding.btnRegister.setOnClickListener {
                registersheet.state = BottomSheetBehavior.STATE_EXPANDED
            }

            binding.etNumber1.addTextChangedListener {
                if (it?.isNotEmpty()!!) {
                    pass1 = it.toString()
                    binding.etNumber2.requestFocus()
                }
            }
            binding.etNumber2.addTextChangedListener {
                if (it?.isNotEmpty()!!) {
                    pass2 = it.toString()
                    binding.etNumber3.requestFocus()
                }
            }
            binding.etNumber3.addTextChangedListener {
                if (it?.isNotEmpty()!!) {
                    pass3 = it.toString()
                    binding.etNumber4.requestFocus()
                }
            }
            binding.etNumber4.addTextChangedListener {
                if (it?.isNotEmpty()!!) {
                    pass4 = it.toString()
                    binding.etNumber5.requestFocus()
                }
            }
            binding.etNumber5.addTextChangedListener {
                if (it?.isNotEmpty()!!) {
                    pass5 = it.toString()
                    binding.etNumber6.requestFocus()
                }
            }
            binding.etNumber6.addTextChangedListener {
                pass6 = it.toString()
                if (binding.etNumber1.text.isNotBlank() &&
                    binding.etNumber2.text.isNotBlank() &&
                    binding.etNumber3.text.isNotBlank() &&
                    binding.etNumber4.text.isNotBlank() &&
                    binding.etNumber5.text.isNotBlank() &&
                    binding.etNumber6.text.isNotBlank()
                ) {
                    verifyCode = "${pass1}${pass2}${pass3}${pass4}${pass5}${pass6}"
                    login()
                }
            }
            btnSendAgain.setOnClickListener {
                login()
            }
        }
        registerFragmentBinding.apply {
            btnRegisterAsuser.setOnClickListener {
                clRegisterEnterphone.visibility = View.GONE
                clRegisterEnterphone.startAnimation(animated("slideout"))
                clRegisterEnterconfirmation.visibility = View.VISIBLE
                clRegisterEnterconfirmation.startAnimation(animated("slidein"))
            }

            btnSendAgain.setOnClickListener {
                clRegisterEnterconfirmation.visibility = View.GONE
                clRegisterEnterconfirmation.startAnimation(animated("slideout"))
                clCreateProfile.visibility = View.VISIBLE
                clCreateProfile.startAnimation(animated("slidein"))
            }
            btnSubmitRegisterAsuser.setOnClickListener {
                registerViewModel.loading()
                registersheet.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            registerViewModel.registerStatus.observe(viewLifecycleOwner, { status ->
                if (status) {
                    binding.clMainSignin.visibility = View.GONE
                    binding.clSigninLoading.visibility = View.VISIBLE
                    binding.clDarkFade.visibility = View.VISIBLE
                    var progressed = 0
                    while (progressed < 101) {
                        binding.progress.setProgressCompat(progressed, true)
                        progressed++
                    }
                    if (progressed == 100) {
                        registerViewModel.loading()

                    }
                }
            })
        }

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        registerFragmentBinding.clCreateProfile.visibility = View.GONE
                        registerFragmentBinding.clRegisterEnterconfirmation.visibility =
                            View.GONE
                        registerFragmentBinding.clRegisterEnterphone.visibility = View.VISIBLE
                        hideKeyboard()

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        }
        registersheet.addBottomSheetCallback(bottomSheetCallback)

    }

    private fun login() {
        val entity = Verify(
            phoneNumber = binding.etPhoneNumber.text.toString(),
            code = verifyCode.toInt()
        )
        lifecycleScope.launch {
            signinViewModel.channel.send(AuthIntent.VerifyIntent(entity))
        }
    }

    private fun subscribeObserver() {
        subscribeGetVerificationCodeObserver()
        subscribeGetMeObserver()
    }

    private fun subscribeGetVerificationCodeObserver() {
        verificationCodeJob = lifecycleScope.launch {
            signinViewModel.dataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        Toast.makeText(
                            requireContext(),
                            dataState.data.data.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        sharedPreferences.edit()
                            .putString("phoneNumber", binding.etPhoneNumber.text.toString()).apply()
                        binding.apply {
                            tvChangeNumber.text = etPhoneNumber.text
                            clSigninEnterphone.visibility = View.GONE
                            clSigninEnterphone.startAnimation(animated("slideout"))
                            clMainEnterconfirmcode.visibility = View.VISIBLE
                            clMainEnterconfirmcode.startAnimation(animated("slidein"))
                            etNumber1.requestFocus()
                            showKeyboard()
                            subscribeTimer()
                        }
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

    private suspend fun subscribeTimer() {
        binding.apply {
            btnSendAgain.isEnabled = false
            val startingValue = 60
            var currentValue = startingValue
            while (currentValue > 0) {
                delay(1000)
                currentValue--
                btnSendAgain.text = currentValue.toString()
            }
        }
    }


    private fun subscribeGetMeObserver() {
        meJob = lifecycleScope.launch {
            signinViewModel.phoneVerifyDataState.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        val accountType = dataState.data.accountType
                        sharedPreferences.edit()
                            .putString("token", dataState.data.token)
                            .putInt("accountType", accountType)
                            .apply()
                        hideKeyboard()
                        binding.apply {
                            nsvMain.visibility = View.GONE
                            nsvMain.startAnimation(animated("slideupout"))
                           // (requireActivity() as MainActivity).setChatBottomSheetVisibility(true)
                            clMainLayout.visibility = View.VISIBLE

                        }
                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToProfileNavGraph())
                        }, 450)
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

    private fun startbg() {
        val uri = Uri.parse("android.resource://" + activity?.getPackageName() + "/" + R.raw.bg)
        binding.vvBg.setVideoURI(uri)
        binding.vvBg.start()
        binding.vvBg.setOnPreparedListener { it.isLooping = true }
    }

    override fun onResume() {
        super.onResume()
        startbg()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        verificationCodeJob?.cancel()
    }

    private fun showKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}