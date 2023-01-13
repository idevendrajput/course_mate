package com.dr.coursemate.auth

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dr.coursemate.utils.AppConstant.PHONE
import com.dr.coursemate.utils.AppConstant.USER_NAME
import com.dr.coursemate.R
import com.dr.coursemate.databinding.FragmentRegisterBinding

class Register : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actions()

    }

    private fun actions() {

        binding.register.setOnClickListener {

            if (binding.name.text.toString().trim().isNotEmpty()) {
                binding.name.error = "Enter name"
                return@setOnClickListener
            }

            if (binding.phoneNumber.text.toString().trim().isEmpty()) {
                binding.phoneNumber.error = "Enter phone number"
                return@setOnClickListener
            }
            if (binding.phoneNumber.text.toString().length != 10) {
                binding.phoneNumber.error = "Invalid phone number"
                return@setOnClickListener
            }

            val args = Bundle()
            args.putString(USER_NAME, binding.name.text.toString())
            args.putString(PHONE, binding.phoneNumber.text.toString())
            findNavController().navigate(R.id.verifyOtp, args)

        }

    }

}