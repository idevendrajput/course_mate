package com.dr.coursemate.auth

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.dr.coursemate.utils.AppConstant.PHONE
import com.dr.coursemate.utils.AppConstant.SOMETHING_WENT_WRONG
import com.dr.coursemate.R
import com.dr.coursemate.databinding.FragmentVerifyOtpBinding
import com.dr.coursemate.utils.AppConstant.USER_NAME
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class VerifyOtp : Fragment() {

    private lateinit var binding: FragmentVerifyOtpBinding
    private lateinit var mContext: Context
    private var mAuth = Firebase.auth
    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private lateinit var phoneNumber: String
    private var verificationId: String? = null
    private var mResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyOtpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneNumber = arguments?.getString(PHONE).toString()

        callbacks()
        showInitLoading()
        startPhoneNumberVerification("+91$phoneNumber")

    }

    private fun showInitLoading() {
        binding.pb1.visibility = View.VISIBLE
        binding.pin.visibility = View.INVISIBLE
        binding.resend.visibility = View.INVISIBLE
    }

    private fun dismissInitLoading() {
        binding.pb1.visibility = View.INVISIBLE
        binding.pin.visibility = View.VISIBLE
        binding.resend.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.pb.visibility = View.VISIBLE
        binding.verify.isEnabled = false
        binding.verify.icon = null
        binding.verify.text = ""
    }

    private fun dismissLoading() {
        binding.pb.visibility = View.GONE
        binding.verify.isEnabled = true
        binding.verify.icon = ContextCompat.getDrawable(mContext, R.drawable.shield_check_24)
        binding.verify.text = "Verify"
    }

    private fun callbacks() {

       mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                dismissInitLoading()
                binding.pin.setText(credential.smsCode.toString())
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Snackbar.make(binding.root,"${p0.message}", Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }

            override fun onCodeSent(verificationId1: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId1, p1)
                verificationId = verificationId1
                mResendingToken = p1
                onCodeSent()
                startCountDownResend(mResendingToken!!)
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                Snackbar.make(binding.root,"Time out", Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

    }

    private fun startCountDownResend(resendingToken: PhoneAuthProvider.ForceResendingToken) {

        binding.resend.isEnabled = false

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                formatTimer(millisUntilFinished/1000)
            }

            override fun onFinish() {
                binding.resend.isEnabled = true
                binding.resend.text = "Resend"

                binding.resend.setOnClickListener {
                    resendVerificationCode(
                        "+91$phoneNumber", resendingToken
                    )
                    binding.resend.text = "Code sent"
                    binding.resend.isEnabled = false
                }
            }
        }.start()

    }

    private fun formatTimer(s: Long) {
        val seconds = s % 60
        val m = (s - seconds) / 60
        binding.resend.text = "Resend ${String.format("%02d",m)}:Resend ${String.format("%02d",m)}:"
    }

    private fun onCodeSent() {
        dismissInitLoading()

        binding.verify.setOnClickListener {

            if (binding.pin.text.toString().length != 6) {
                Snackbar.make(binding.root, "Invalid Verification Code", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading()

            verificationId?.let { it1 -> verifyPhoneNumberWithCode(it1, binding.pin.text.toString()) }

        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity((activity as AppCompatActivity))
            .setCallbacks(mCallbacks!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        mAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                dismissLoading()

                val args = Bundle()
                args.putString(PHONE, phoneNumber)
                args.putString(USER_NAME, arguments?.getString(USER_NAME) ?: "")
                findNavController().navigate(R.id.generatePassword, args)

            }
            .addOnFailureListener {
                Snackbar.make(binding.root, SOMETHING_WENT_WRONG, Snackbar.LENGTH_SHORT)
                    .show()
            }
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken
    ) {
        val options = mCallbacks?.let {
            PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity((activity as AppCompatActivity))
                .setForceResendingToken(token)
                .setCallbacks(it)
                .build()
        }
        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }


}