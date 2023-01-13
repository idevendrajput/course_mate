package com.dr.royaluation.auth

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.TelephonyManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dr.coursemate.databinding.CountryCodeDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginPage : Fragment() {

//    lateinit var binding : FragmentLoginPageBinding
//    private var mVerificationId: String? = null
//    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
//    private val db = FirebaseFirestore.getInstance()
//    private var mAuth = Firebase.auth
//    private var countDownTimer: CountDownTimer? = null
//    private lateinit var mContext: Context
//    private lateinit var phoneNumberDialog : BottomSheetDialog
//    private lateinit var verificationDialog : BottomSheetDialog
//    private lateinit var verificationBinding : VerifyOtpLayoutBinding
//    private lateinit var phoneNumber: String
//    private lateinit var countryCode : String
//    lateinit var countryCodeDialog : BottomSheetDialog
//    private lateinit var phoneNumberBinding : VerifyPhoneLayoutBinding
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
//        binding = FragmentLoginPageBinding.inflate(layoutInflater)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.forgetPassword.setOnClickListener {
//            phoneNumberDialog()
//        }
//        settingCountryCode()
//        actions()
//
//    }
//
//    private fun settingCountryCode() {
//
//        val tm = (activity as AppCompatActivity).getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        val countryCodeValue = tm.simCountryIso
//
//        val arrayList = resources.getStringArray(R.array.countryCodes).toList()
//        for (i in arrayList) {
//            if (i.lowercase().replace(" ","").contains(countryCodeValue)) {
//                binding.countryCode.text = "+${i}"
//                val value = i.replace("[^0-9]".toRegex(), "")
//                countryCode = "+".plus(value)
//                SharedPref.setData(mContext, AppConstaints.COUNTRY_CODE,"+".plus(value))
//            }
//        }
//    }
//
//
//    private fun countryCodePicker() {
//
//        countryCodeDialog = BottomSheetDialog(mContext)
//        val countryCodeDialogBinding = CountryCodeDialogBinding.inflate(countryCodeDialog.layoutInflater)
//        countryCodeDialog.setContentView(countryCodeDialogBinding.root)
//        countryCodeDialog.show()
//
//        countryCodeDialogBinding.rv.layoutManager = LinearLayoutManager(mContext,
//            LinearLayoutManager.VERTICAL,false)
//
//        val arrayList = resources.getStringArray(R.array.countryCodes).toList() as ArrayList<String>
//        val adapterCountryCode = AdapterCountryCode(arrayList)
//        adapterCountryCode.notifyDataSetChanged()
//
//        countryCodeDialogBinding.rv.adapter = adapterCountryCode
//
//        countryCodeDialogBinding.search.addTextChangedListener {
//
//            if (countryCodeDialogBinding.search.text.toString().trim().isNotEmpty()) {
//                countryCodeDialogBinding.rv.layoutManager = LinearLayoutManager(mContext,
//                    LinearLayoutManager.VERTICAL,false)
//
//                val results = ArrayList<String>()
//                val adapter = AdapterCountryCode(results)
//                for (r in arrayList) {
//                    if (r.lowercase().replace(" ","").contains(countryCodeDialogBinding.search.text.toString().trim().lowercase())) {
//                        results.add(r)
//                        adapter.notifyDataSetChanged()
//                    }
//                }
//                countryCodeDialogBinding.rv.adapter = adapter
//            }
//
//        }
//    }
//
//    private fun actions() {
//
//        binding.countryCode.addTextChangedListener {
//            countryCode = "+".plus(binding.countryCode.text.toString().replace("[^0-9]".toRegex(), ""))
//        }
//
//        binding.countryCode.setOnClickListener {
//            countryCodePicker()
//        }
//
//        binding.createNewAccount.setOnClickListener {
//            findNavController().navigate(R.id.informationPage)
//        }
//
//        binding.forgetPassword.setOnClickListener {
//            phoneNumberDialog()
//        }
//
//           binding.login.setOnClickListener {
//            if (binding.phoneNumber.text.toString().isEmpty()) {
//                binding.phoneNumber.error = ERROR_ENTER_PHONE
//                return@setOnClickListener
//            }
//            if (binding.password.text.toString().isEmpty()) {
//                binding.password.error = ERROR_ENTER_PASSWORD
//                return@setOnClickListener
//            }
//            showLoading(true)
//            db.collection(USER_COLLECTION)
//                .document(binding.phoneNumber.text.toString())
//                .get().addOnSuccessListener {
//                    if (it.exists()) {
//                        var password = "*#*#@!@#$)(*^&%^&*&*(%$%#$^&(*&^%^&*~%$#$#@#$~%$^~~&*(&!(*~(@)"
//                        if (it[PASSWORD] != null) {
//                            password = it[PASSWORD].toString().trim().lowercase()
//                        }
//                        if (binding.password.text.toString().trim().lowercase() == password) {
//
//                            try {
//
//                                SharedPref.setData(mContext, PHONE,binding.phoneNumber.text.toString().trim())
//
//                                var balance = 0.0f
//                                if (it[BALANCE] != null) {
//                                    balance = it[BALANCE].toString().toFloat()
//                                }
//                                SharedPref.setData(mContext, UPLINK_UID,it[UPLINK_UID].toString())
//                                setCurrentBalance(mContext, balance)
//                                setPassword(mContext, password)
//                                SharedPref.setData(mContext, AUTH_STATUS, AUTH_STATUS_DONE)
//                                setUserPremium(mContext, it[PREMIUM_PLAN] != null)
//                                setUserGold(mContext, it[GOLD_UPGRADE] != null)
//
//                                updateLoginInfo()
//
//                            } catch (e : Exception) {
//                            }
//
//                        } else {
//                            showLoading(false)
//                            Snackbar.make(binding.root, "Incorrect password", Snackbar.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        showLoading(false)
//                        Snackbar.make(binding.root, "User not exist", Snackbar.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
//
//    private fun startPhoneNumberVerification(phoneNumber: String) {
//        // [START start_phone_auth]
//        val options = PhoneAuthOptions.newBuilder(mAuth)
//            .setPhoneNumber(phoneNumber) // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity((activity as AppCompatActivity)) // Activity (for callback binding)
//            .setCallbacks(mCallbacks!!) // OnVerificationStateChangedCallbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//        // [END start_phone_auth]
//    }
//
//    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
//        val credential = PhoneAuthProvider.getCredential(verificationId, code)
//        signInWithPhoneAuthCredential(credential)
//    }
//
//    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//
//        mAuth.signInWithCredential(credential)
//            .addOnSuccessListener {
//                verificationDialog.dismiss()
//                SharedPref.setData(mContext,PHONE,phoneNumberBinding.phone.text.toString().trim())
//                val args = Bundle()
//                args.putString(PHONE, phoneNumberBinding.phone.text.toString().trim())
//                findNavController().navigate(R.id.forgetPassword, args)
//            }
//            .addOnFailureListener {
//                showLoadingVerificationDialog(false)
//                Snackbar.make(binding.root, SOMETHING_WENT_WRONG, Snackbar.LENGTH_SHORT)
//                    .show()
//            }
//    }
//
//    private fun resendVerificationCode(
//        phoneNumber: String,
//        token: PhoneAuthProvider.ForceResendingToken
//    ) {
//        val options = PhoneAuthOptions.newBuilder(mAuth)
//            .setPhoneNumber(phoneNumber) // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity((activity as AppCompatActivity)) // Activity (for callback binding)
//            .setCallbacks(mCallbacks!!) // OnVerificationStateChangedCallbacks
//            .setForceResendingToken(token) // ForceResendingToken from callbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }
//
//    private fun updateLoginInfo() {
//
//        try {
//            val pInfo: PackageInfo =
//                (activity as AppCompatActivity).packageManager.getPackageInfo((activity as AppCompatActivity).packageName, 0)
//            val version = pInfo.versionName
//
//            val map = HashMap<String, Any>()
//            map[APP_VERSION] = version
//            map[IP_ADDRESS] = try { getIpAddress() } catch (e : Exception) { "Not Available" }
//            map[LAST_TIME_UPDATE] = FieldValue.serverTimestamp()
//            map[COUNTRY_CODE] = countryCode
//            map[PASSWORD] = binding.password.text.toString().trim().lowercase()
//
//            db.collection(USER_COLLECTION)
//                .document(mUid(mContext))
//                .set(map, SetOptions.merge())
//                .addOnSuccessListener {
//                    retrieveLevels(mContext)
//                    updateConstraintData(mContext, activity as AppCompatActivity)
//                    findNavController().navigate(R.id.home)
//                }
//        } catch (e : Exception) {
//            Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
//        }
//
//    }
//
//    private fun phoneNumberDialog() {
//
//        phoneNumberDialog = BottomSheetDialog(mContext)
//        phoneNumberBinding = VerifyPhoneLayoutBinding.inflate(phoneNumberDialog.layoutInflater)
//        phoneNumberDialog.setContentView(phoneNumberBinding.root)
//        phoneNumberDialog.setCancelable(false)
//        phoneNumberDialog.show()
//
//        phoneNumberBinding.close.setOnClickListener {
//            phoneNumberDialog.dismiss()
//        }
//
//        phoneNumberBinding.phone.addTextChangedListener {
//            phoneNumber = countryCode.plus(phoneNumberBinding.phone.text.toString().ifEmpty { "0" })
//        }
//
//        phoneNumberBinding.getOTP.setOnClickListener {
//
//            if (phoneNumberBinding.phone.text.toString().trim().length < 8) {
//                phoneNumberBinding.phone.error = ERROR_INVALID_PHONE
//                return@setOnClickListener
//            }
//
//            if (phoneNumberBinding.phone.text.toString().trim().isEmpty()) {
//                phoneNumberBinding.phone.error = ERROR_ENTER_PHONE
//                returnTransition
//            }
//
//            showLoadingPhoneDialog(true)
//            phoneNumberBinding.phone.isFocusableInTouchMode = false
//            phoneNumberBinding.phone.isFocusable = false
//            startPhoneNumberVerification(countryCode.plus(phoneNumberBinding.phone.text.toString()))
//        }
//
//        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                phoneNumberDialog.dismiss()
//                verificationBinding.verificationCode.setText(credential.smsCode.toString())
//            }
//
//            override fun onVerificationFailed(p0: FirebaseException) {
//                Snackbar.make(phoneNumberBinding.root," ${p0.message}", Snackbar.LENGTH_SHORT).show()
//                showLoadingPhoneDialog(false)
//                phoneNumberBinding.phone.isFocusableInTouchMode = true
//                phoneNumberBinding.phone.isFocusable = true
//            }
//
//            override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
//                super.onCodeSent(verificationId, p1)
//                phoneNumberDialog.dismiss()
//                verificationDialog()
//
//                mVerificationId = verificationId
//
//                countDownTimer =  object : CountDownTimer(60000,1000) {
//                    override fun onFinish() {
//
//                        verificationBinding.verify.isEnabled = true
//                        verificationBinding.resend.text = "Resend"
//                        verificationBinding.resend.visibility = View.VISIBLE
//
//                        verificationBinding.verify.setOnClickListener {
//                            showLoadingVerificationDialog(true)
//                            resendVerificationCode(countryCode.plus(phoneNumberBinding.phone.text.toString()),p1)
//                        }
//
//                    }
//
//                    override fun onTick(millisUntilFinished: Long) {
//                        verificationBinding.resend.text = "Resend: ".plus(formatTimer(
//                            millisUntilFinished / 1000
//                        ))
//                    }
//                }.start()
//
//                verificationBinding.verify.setOnClickListener {
//
//                    if (verificationBinding.verificationCode.text.toString().trim().isEmpty()) {
//                        verificationBinding.verificationCode.error = ERROR_ENTER_VERIFICATION_CODE
//                        return@setOnClickListener
//                    }
//
//                    if (countDownTimer != null) {
//                        countDownTimer?.cancel()
//                    }
//                    showLoadingVerificationDialog(true)
//                    verificationBinding.resend.visibility = View.GONE
//
//                    verifyPhoneNumberWithCode(mVerificationId!!,verificationBinding.verificationCode.text.toString())
//                }
//            }
//
//            override fun onCodeAutoRetrievalTimeOut(p0: String) {
//                super.onCodeAutoRetrievalTimeOut(p0)
//                showLoadingPhoneDialog(false)
//                verificationBinding.verify.text = "Get OTP"
//                // Toast.makeText(mContext, SERVER_ERROR, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun verificationDialog() {
//
//        verificationDialog = BottomSheetDialog(mContext)
//        verificationBinding = VerifyOtpLayoutBinding.inflate(verificationDialog.layoutInflater)
//        verificationDialog.setContentView(verificationBinding.root)
//        verificationDialog.setCancelable(false)
//        verificationDialog.show()
//
//        verificationBinding.close.setOnClickListener {
//            verificationDialog.dismiss()
//        }
//    }
//
//    private fun showLoadingVerificationDialog(isShow: Boolean) {
//        if (isShow) {
//            verificationBinding.verify.text = ""
//            verificationBinding.verify.icon = null
//            verificationBinding.verify.isEnabled = false
//            verificationBinding.pb.visibility = View.VISIBLE
//        } else {
//            verificationBinding.verify.text = "Proceed Securely"
//            verificationBinding.verify.icon = ContextCompat.getDrawable(mContext,
//                R.drawable.shield_check_24
//            )
//            verificationBinding.verify.isEnabled = true
//            verificationBinding.pb.visibility = View.GONE
//        }
//    }
//
//    private fun showLoadingPhoneDialog(isShow: Boolean) {
//        if (isShow) {
//            phoneNumberBinding.getOTP.text = ""
//            phoneNumberBinding.getOTP.icon = null
//            phoneNumberBinding.getOTP.isEnabled = false
//            phoneNumberBinding.pb.visibility = View.VISIBLE
//        } else {
//            phoneNumberBinding.getOTP.text = "Get OTP"
//            phoneNumberBinding.getOTP.icon = ContextCompat.getDrawable(mContext,
//                R.drawable.shield_check_24
//            )
//            phoneNumberBinding.getOTP.isEnabled = true
//            phoneNumberBinding.pb.visibility = View.GONE
//        }
//    }
//
//    private fun showLoading(isShow : Boolean) {
//        if (isShow) {
//            binding.login.text = ""
//            binding.login.icon = null
//            binding.login.isEnabled = false
//            binding.pb.visibility = View.VISIBLE
//        } else {
//            binding.login.text = "Login Securely"
//            binding.login.icon = ContextCompat.getDrawable(mContext,
//                R.drawable.shield_check_24
//            )
//            binding.login.isEnabled = true
//            binding.pb.visibility = View.GONE
//        }
//    }
//
//    inner class AdapterCountryCode(private val list: ArrayList<String>) : RecyclerView.Adapter<AdapterCountryCode.Viewholder>() {
//
//        inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) { }
//
//        override fun onCreateViewHolder(
//            parent: ViewGroup,
//            viewType: Int
//        ): Viewholder {
//            return Viewholder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1,parent,false))
//        }
//
//        override fun onBindViewHolder(holder: Viewholder, position: Int) {
//            (holder.itemView.findViewById(android.R.id.text1) as TextView).text = list[position]
//            holder.itemView.setOnClickListener {
//                binding.countryCode.text = "+${list[position]}"
//                val value = list[position].replace("[^0-9]".toRegex(), "")
//                SharedPref.setData(mContext, AppConstaints.COUNTRY_CODE,"+".plus(value))
//                countryCodeDialog.dismiss()
//            }
//        }
//
//        override fun getItemCount(): Int {
//            return list.size
//        }
//
//    }

}