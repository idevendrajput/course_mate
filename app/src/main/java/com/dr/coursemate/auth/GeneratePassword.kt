package com.dr.coursemate.auth

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.dr.coursemate.utils.AppConstant.PASSWORD
import com.dr.coursemate.utils.AppConstant.PHONE
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.utils.AppConstant.USER_COLLECTION
import com.dr.coursemate.utils.AppConstant.USER_NAME
import com.dr.coursemate.R
import com.dr.coursemate.databinding.FragmentGeneratePasswordBinding
import com.dr.coursemate.utils.Utils
import com.dr.coursemate.utils.Utils.Companion.isAuthDone
import com.dr.coursemate.utils.Utils.Companion.setAuthDone
import com.dr.coursemate.utils.Utils.Companion.setPhone
import com.dr.coursemate.utils.Utils.Companion.setUserName
import com.google.firebase.firestore.FirebaseFirestore

class GeneratePassword : Fragment() {

    private lateinit var binding: FragmentGeneratePasswordBinding
    private lateinit var mContext: Context
    private val db = FirebaseFirestore.getInstance()

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGeneratePasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actions()
    }

    private fun actions() {

        binding.finish.setOnClickListener {

            val password = binding.password.text.toString().trim()
            val confirmPass = binding.confirmPassword.text.toString().trim()

            if (password.isEmpty()) {
                binding.password.error = "Enter Password"
                return@setOnClickListener
            }

            if (confirmPass.isEmpty()) {
                binding.confirmPassword.error = "Re-enter password"
                return@setOnClickListener
            }

            if (password != confirmPass) {
                binding.confirmPassword.error = "Password not matching"
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.password.error = "Password must have have at least 6 characters"
                return@setOnClickListener
            }

            finish()

        }

    }

    private fun finish() {

        val username = arguments?.getString(USER_NAME) ?: ""
        val phone = arguments?.getString(PHONE) ?: ""
        val password = binding.password.text.toString()

        val map = HashMap<String, Any>()
        map[PHONE] = phone
        map[PASSWORD] = password
        map[USER_NAME] = username
        map[TIMESTAMP_FIELD] = System.currentTimeMillis()

        showLoading()

        db.collection(USER_COLLECTION)
            .document(phone)
            .get().addOnSuccessListener {

                setPhone(mContext, phone)
                setUserName(mContext, username)
                setAuthDone(mContext)

                Handler(Looper.getMainLooper())
                    .postDelayed({
                        dismissLoading()
                        findNavController().navigate(R.id.home)
                    }, 150)

            }

    }

    private fun showLoading() {
        binding.pb.visibility = View.VISIBLE
        binding.finish.isEnabled = false
        binding.finish.icon = null
        binding.finish.text = ""
    }

    private fun dismissLoading() {
        binding.pb.visibility = View.GONE
        binding.finish.isEnabled = true
        binding.finish.icon = ContextCompat.getDrawable(mContext, R.drawable.shield_check_24)
        binding.finish.text = "Finish"
    }

}