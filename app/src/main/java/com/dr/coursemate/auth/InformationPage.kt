package com.dr.coursemate.auth

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.telephony.TelephonyManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class InformationPage : Fragment() {
//
//    lateinit var binding : FragmentInformationPageBinding
//    private var mVerificationId: String? = null
//    private val db = FirebaseFirestore.getInstance()
//    private var countDownTimer: CountDownTimer? = null
//    private lateinit var verificationDialog : BottomSheetDialog
//    private lateinit var verificationBinding : VerifyOtpLayoutBinding
//    private lateinit var phoneNumber: String
//    lateinit var mContext : Context
//    private var imageUri: Uri? = null
//    private lateinit var imageUrl : String
//    private lateinit var uplinkUid : String
//    lateinit var downsizedImageBytes: ByteArray
//    lateinit var countryCodeDialog : BottomSheetDialog
//    private lateinit var countryCode : String
//    private lateinit var signUpBonusDialog : Dialog
//
//    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        imageUri = uri
//        binding.profile.setImageURI(uri)
//
//    }
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
//        // Inflate the layout for this fragment
//        binding = FragmentInformationPageBinding.inflate(layoutInflater)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        settingCountryCode()
//        // signUpBonusDialog()
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
//                SharedPref.setData(mContext,COUNTRY_CODE,"+".plus(value))
//                countryCode = "+".plus(value)
//            }
//        }
//    }
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
//    @Throws(IOException::class)
//    fun getDownsizedImageBytes(
//        fullBitmap: Bitmap?,
//        scaleWidth: Int,
//        scaleHeight: Int
//    ): ByteArray  {
//        val scaledBitmap =
//            Bitmap.createScaledBitmap(fullBitmap!!, scaleWidth, scaleHeight, true)
//
//        // 2. Instantiate the downsized image content as a byte[]
//        val baos = ByteArrayOutputStream()
//        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        return baos.toByteArray()
//    }
//
//    private fun actions() {
//
//        binding.countryCode.addTextChangedListener {
//            countryCode = "+".plus(binding.countryCode.text.toString().replace("[^0-9]".toRegex(), ""))
//        }
//
//        binding.chooseProfile.setOnClickListener {
//            Dexter.withContext(mContext)
//                .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(object : PermissionListener {
//                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
//                        getContent.launch("image/*")
//                    }
//                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
//                        Toast.makeText(mContext, "Permission Denied!", Toast.LENGTH_SHORT).show()
//                    }
//                    override fun onPermissionRationaleShouldBeShown(
//                        p0: PermissionRequest?,
//                        p1: PermissionToken?
//                    ) {}
//                }).check()
//        }
//
//        binding.proceed.setOnClickListener {
//
//            if (binding.username.text.toString().trim().isEmpty()) {
//                binding.username.error = ENTER_NAME_ERROR
//                return@setOnClickListener
//            }
//            if (binding.email.text.toString().trim().isEmpty()) {
//                binding.email.error = ENTER_EMAIL_ERROR
//                return@setOnClickListener
//            }
//            if (!binding.email.text.toString().trim().contains("@")){
//                binding.email.error = ERROR_INVALID_EMAIL
//                return@setOnClickListener
//            }
//             if (binding.female.isChecked){
//                 gender = FEMALE
//             }
//
//            if (binding.referCode.text.toString().trim().isEmpty()) {
//                binding.referCode.error = ERROR_INVALID_REFER_CODE
//                return@setOnClickListener
//            }
//
//            if (binding.phoneNumber.text.toString().trim().length < 8) {
//                binding.phoneNumber.error = AppConstaints.ERROR_INVALID_PHONE
//                return@setOnClickListener
//            }
//
//            if (binding.phoneNumber.text.toString().trim().isEmpty()) {
//                binding.phoneNumber.error = AppConstaints.ERROR_ENTER_PHONE
//                return@setOnClickListener
//            }
//
//            if (binding.password.text.toString().trim().isEmpty()) {
//                binding.password.error = ERROR_ENTER_PASSWORD
//                return@setOnClickListener
//            }
//
//            if (binding.password.text.toString().length < 6) {
//                binding.password.error = "Password must have atleast 6 character"
//                return@setOnClickListener
//            }
//
//            showLoading(true)
//
//            db.collection(USER_COLLECTION)
//                .document(binding.referCode.text.toString().trim())
//                .get().addOnSuccessListener {d->
//                    if (d.exists()) {
//                        uplinkUid = d.id
//                        setUplink(mContext, d.id, 1)
//                        db.collection(USER_COLLECTION)
//                            .document(binding.phoneNumber.text.toString().trim())
//                            .get().addOnSuccessListener {
//                                if (it.exists()) {
//                                    showLoading(false)
//                                    Snackbar.make(binding.root, "An account is already exist with this phone number", Snackbar.LENGTH_SHORT).show()
//                                    return@addOnSuccessListener
//                                }
//                                binding.phoneNumber.isFocusableInTouchMode = false
//                                binding.phoneNumber.isFocusable = false
//                                startPhoneNumberVerification(countryCode.plus(binding.phoneNumber.text.toString()))
//                            }
//                    } else {
//                        showLoading(false)
//                        binding.referCode.error = ERROR_INVALID_REFER_CODE
//                    }
//                }.addOnFailureListener {
//                    showLoading(false)
//                    Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
//                }
//
//        }
//
//        binding.countryCode.setOnClickListener {
//            countryCodePicker()
//        }
//
//        binding.phoneNumber.addTextChangedListener {
//            phoneNumber = countryCode.plus(binding.phoneNumber.text.toString().ifEmpty { "0" })
//        }
//
//    }
//
//    private fun uploadImage(referCode : Int) {
//
//        val scaleDivider = 3
//
//        try {
//
//            val fullBitmap = MediaStore.Images.Media.getBitmap((activity as AppCompatActivity).contentResolver, imageUri)
//
//            val scaleWidth = fullBitmap.width / scaleDivider
//            val scaleHeight = fullBitmap.height / scaleDivider
//            downsizedImageBytes = getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight)
//
//            val reference = FirebaseStorage.getInstance().reference
//
//            val file = imageUri
//            val riversRef = reference.child("images/${file?.lastPathSegment}")
//            val uploadTask = riversRef.putBytes(downsizedImageBytes)
//
//            uploadTask.continueWithTask { task->
//
//                if (!task.isSuccessful) {
//                    throw task.exception!!
//                }
//
//                riversRef.downloadUrl.addOnCompleteListener { uploadTask ->
//                    if (uploadTask.isSuccessful) {
//                        imageUrl = uploadTask.result.toString()
//                        putData(referCode)
//                        SharedPref.setData(mContext, PROFILE_URL, imageUrl)
//                    } else {
//                        showLoading(false)
//                        Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
//                    }
//
//                }
//            }.addOnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    showLoading(false)
//                    Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        } catch (ioEx: IOException) {
//            showLoading(false)
//            Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
//        // [START verify_with_code]
//        val credential = PhoneAuthProvider.getCredential(verificationId, code)
//        // [END verify_with_code]
//        signInWithPhoneAuthCredential(credential)
//    }
//
//    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//
//        mAuth.signInWithCredential(credential)
//            .addOnSuccessListener {
//
//                verificationDialog.dismiss()
//                showLoading(true)
//                SharedPref.setData(mContext,PHONE,binding.phoneNumber.text.toString())
//
//                showLoading(true)
//                if (imageUri == null) {
//                    imageUrl = APP_LOGO_URL
//                    putData(getRandomInt(100000,999999))
//                } else {
//                    uploadImage(getRandomInt(100000,999999))
//                }
//
//            }
//            .addOnFailureListener {
//                showLoadingVerificationDialog(false)
//                Snackbar.make(verificationBinding.root, SOMETHING_WENT_WRONG, Snackbar.LENGTH_SHORT)
//                    .show()
//            }
//
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
//
//    private fun putData(userCode : Int) {
//
//        showLoading(true)
//
//        db.collection(USER_COLLECTION)
//            .whereEqualTo(FIELD_USER_CODE,userCode)
//            .get().addOnSuccessListener {
//
//                if (it.size() == 0) {
//
//                    val pInfo: PackageInfo =
//                        (activity as AppCompatActivity).packageManager.getPackageInfo((activity as AppCompatActivity).packageName, 0)
//                    val version = pInfo.versionName
//
//                    val map = HashMap<String,Any>()
//                    map[USER_NAME] = binding.username.text.toString().trim()
//                    map[EMAIL] = binding.email.text.toString().trim()
//                    map[PHONE] = mUid(mContext)
//                    map[FIELD_USER_CODE] = userCode
//                    map[PROFILE_URL] = imageUrl
//                    map[GENDER] = gender
//                    map[COUNTRY_CODE] = countryCode
//                    map[BALANCE] = 70f
//                    map[UPLINK_UID] = uplinkUid
//                    map[PASSWORD] = binding.password.text.toString().trim()
//                    map[LAST_TIME_UPDATE] = FieldValue.serverTimestamp()
//                    map[APP_VERSION] = version
//                    map[IP_ADDRESS] = try {
//                        Utils.getIpAddress()
//                    } catch (e : Exception) { "Not Available" }
//                    map[TIMESTAMP_FIELD] = FieldValue.serverTimestamp()
//
//                    db.collection(USER_COLLECTION)
//                        .document(mUid(mContext))
//                        .set(map, SetOptions.merge())
//                        .addOnSuccessListener {
//                            SharedPref.setData(mContext, USER_NAME,binding.username.text.toString())
//                            SharedPref.setData(mContext, EMAIL,binding.email.text.toString())
//                            SharedPref.setData(mContext, AUTH_STATUS, AUTH_STATUS_DONE)
//                            SharedPref.setData(mContext, PROFILE_URL, imageUrl)
//                            setGender(mContext, gender)
//                            setCurrentBalance(mContext, 70f)
//                            findNavController().navigate(R.id.directionScreen)
//                        }.addOnFailureListener {
//                            // On Failure
//                            showLoading(false)
//                            Snackbar.make(binding.root, SOMETHING_WENT_WRONG, Snackbar.LENGTH_SHORT).show()
//                        }
//
//                    return@addOnSuccessListener
//                }
//                putData(getRandomInt(100000,999999))
//            }
//    }
//
//    private fun verificationDialog() {
//
//        verificationDialog = BottomSheetDialog(mContext)
//        verificationBinding = VerifyOtpLayoutBinding.inflate(verificationDialog.layoutInflater)
//        verificationDialog.setContentView(verificationBinding.root)
//        verificationDialog.show()
//
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
//    private fun showLoadingVerificationDialog(isShow: Boolean) {
//        if (isShow) {
//            verificationBinding.verify.text = ""
//            verificationBinding.verify.icon = null
//            verificationBinding.verify.isEnabled = false
//            verificationBinding.pb.visibility = View.VISIBLE
//        } else {
//            verificationBinding.verify.text = "Verify"
//            verificationBinding.verify.icon = ContextCompat.getDrawable(mContext,
//                R.drawable.shield_check_24
//            )
//            verificationBinding.verify.isEnabled = true
//            verificationBinding.pb.visibility = View.GONE
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
//                SharedPref.setData(mContext, COUNTRY_CODE,"+$value")
//                countryCodeDialog.dismiss()
//            }
//        }
//
//        override fun getItemCount(): Int {
//            return list.size
//        }
//
//    }
//
//    private fun signUpBonusDialog() {
//
//        signUpBonusDialog = Dialog(mContext)
//        signUpBonusDialog.setContentView(R.layout.sign_up_bonus_congrats_dialog)
//        signUpBonusDialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//        signUpBonusDialog.setCancelable(false)
//
//        val message = signUpBonusDialog.findViewById<TextView>(R.id.message)
//        val finish = signUpBonusDialog.findViewById<MaterialButton>(R.id.finish)
//        val anim = signUpBonusDialog.findViewById<LottieAnimationView>(R.id.anim)
//
//       try {
//           anim.setAnimationFromUrl("https://assets3.lottiefiles.com/private_files/lf30_9142zhsb.json")
//       } catch (e : Exception) { }
//
//        if (isIndianUser(activity as AppCompatActivity)) {
//            message.text = "70₹ Sign Up Bonus Added To Your Wallet"
//        } else {
//            message.text = "1$ Sign Up Bonus Added To Your Wallet"
//        }
//
//        MyNotification.SendNotificationToTopic("New team member \uD83C\uDF8A",
//            getUserName(mContext) + " Just Joined Your Team", getUplink(mContext, 1), activity as AppCompatActivity)
//
//        finish.setOnClickListener {
//            signUpBonusDialog.dismiss()
//            findNavController().navigate(R.id.directionScreen)
//        }
//
//    }


}