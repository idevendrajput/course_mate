package com.dr.royaluation.auth

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

class ForgetPassword : Fragment() {

//    private lateinit var mContext : Context
//    private lateinit var binding : FragmentForgetPasswordBinding
//    private lateinit var phoneNumber : String
//    private val db = FirebaseFirestore.getInstance()
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mContext = context
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentForgetPasswordBinding.inflate(layoutInflater)
//        phoneNumber = arguments?.getString(PHONE).toString()
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//        binding.proceed.setOnClickListener {
//
//            if (binding.password.text.toString().trim().isEmpty()) {
//                binding.password.error = "Please enter password"
//                return@setOnClickListener
//            }
//
//            if (binding.password.text.toString().length < 6) {
//                binding.password.error = "Password must have atleast 6 character"
//                return@setOnClickListener
//            }
//
//            if (binding.password2.text.toString().trim().isEmpty()) {
//                binding.password2.error = "Please Re-Enter password"
//                return@setOnClickListener
//            }
//
//            if (binding.password2.text.toString().trim().lowercase() != binding.password.text.toString().trim().lowercase()) {
//                Snackbar.make(binding.root, "Password is not same", Snackbar.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//
//            showLoading(true)
//
//            db.collection(USER_COLLECTION)
//                .document(phoneNumber)
//                .get().addOnSuccessListener { d->
//                    try {
//                        var balance = 0.0f
//                        if (d[BALANCE] != null) {
//                            balance = d[AppConstaints.BALANCE].toString().toFloat()
//                        }
//                        setData(mContext, UPLINK_UID,d[UPLINK_UID].toString())
//                        setCurrentBalance(mContext, balance)
//                        setEmail(mContext, d[EMAIL].toString())
//                        setPhone(mContext, d[PHONE].toString())
//                        setProfileUrl(mContext, d[PROFILE_URL].toString())
//                        setPassword(mContext, binding.password.text.toString().trim().lowercase())
//                        updateLoginInfo()
//                    } catch (e : Exception) {
//                        showLoading(false)
//                        Snackbar.make(binding.root, SOMETHING_WENT_WRONG, Snackbar.LENGTH_SHORT).show()
//                    }
//                }.addOnFailureListener {
//                    showLoading(false)
//                    Snackbar.make(binding.root, SOMETHING_WENT_WRONG, Snackbar.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//    private fun showLoading(isShow : Boolean) {
//        if (isShow) {
//            binding.proceed.text = ""
//            binding.proceed.icon = null
//            binding.proceed.isEnabled = false
//            binding.pb.visibility = View.VISIBLE
//        } else {
//            binding.proceed.text = "Proceed Securely"
//            binding.proceed.icon = ContextCompat.getDrawable(mContext,
//                R.drawable.shield_check_24
//            )
//            binding.proceed.isEnabled = true
//            binding.pb.visibility = View.GONE
//        }
//    }
//
//
//    private fun updateLoginInfo() {
//
//        val pInfo: PackageInfo =
//            (activity as AppCompatActivity).packageManager.getPackageInfo((activity as AppCompatActivity).packageName, 0)
//        val version = pInfo.versionName
//
//        val map = HashMap<String, Any>()
//        map[AppConstaints.APP_VERSION] = version
//        map[AppConstaints.IP_ADDRESS] = try {
//            Utils.getIpAddress()
//        } catch (e : Exception) { "Not Available" }
//        map[AppConstaints.LAST_TIME_UPDATE] = FieldValue.serverTimestamp()
//        map[PASSWORD] = binding.password.text.toString().trim().lowercase()
//
//        db.collection(USER_COLLECTION)
//            .document(phoneNumber)
//            .set(map, SetOptions.merge())
//            .addOnSuccessListener {
//                setData(mContext,
//                    AppConstaints.AUTH_STATUS,
//                    AppConstaints.AUTH_STATUS_DONE
//                )
//                retrieveLevels(mContext)
//                updateConstraintData(mContext, activity as AppCompatActivity)
//                findNavController().navigate(R.id.home)
//            }
//    }
}