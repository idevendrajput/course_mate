package com.dr.royaluation.auth

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

class Edit_Profile : Fragment() {

//    lateinit var binding : FragmentEditProfileBinding
//    private val db = FirebaseFirestore.getInstance()
//    lateinit var mContext : Context
//    lateinit var downsizedImageBytes: ByteArray
//    private var imageUri: Uri? = null
//    private lateinit var imageUrl : String
//
//    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        imageUri = uri
//        binding.profile.setImageURI(uri)
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
//        binding = FragmentEditProfileBinding.inflate(layoutInflater)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        actions()
//        updateUi()
//
//    }
//
//    private fun actions() {
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
//        binding.finish.setOnClickListener {
//
//            if (binding.username.text.toString().trim().isEmpty()) {
//                binding.username.error = AppConstaints.ENTER_NAME_ERROR
//                return@setOnClickListener
//            }
//            if (binding.email.text.toString().trim().isEmpty()) {
//                binding.email.error = AppConstaints.ENTER_EMAIL_ERROR
//                return@setOnClickListener
//            }
//            if (!binding.email.text.toString().trim().contains("@")){
//                binding.email.error = AppConstaints.ERROR_INVALID_EMAIL
//                return@setOnClickListener
//            }
//            showLoading(true)
//            if (imageUri == null) {
//                putData()
//            } else {
//                uploadImage()
//            }
//        }
//
//    }
//
//    private fun uploadImage() {
//
//        val reference = FirebaseStorage.getInstance().reference
//
//        val imageUri: Uri = imageUri!!
//        val fullBitmap = MediaStore.Images.Media.getBitmap((activity as AppCompatActivity).contentResolver, imageUri)
//
//        val scaleWidth = fullBitmap.width / 3
//        val scaleHeight = fullBitmap.height / 3
//        downsizedImageBytes = getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight)
//
//        val file = imageUri
//        val riversRef = reference.child("images/${file.lastPathSegment}")
//        val uploadTask = riversRef.putBytes(downsizedImageBytes)
//
//        uploadTask.continueWithTask { task->
//
//            if (!task.isSuccessful) {
//                throw task.exception!!
//            }
//
//            riversRef.downloadUrl.addOnCompleteListener { uploadTask ->
//                if (uploadTask.isSuccessful) {
//                    imageUrl = uploadTask.result.toString()
//                    putData()
//                } else {
//                    showLoading(false)
//                    Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
//                }
//
//            }
//        }.addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                showLoading(false)
//                Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun updateUi() {
//        binding.username.setText(getUserName(mContext))
//        binding.email.setText(getEmail(mContext))
//        Picasso.get()
//            .load(getProfileUrl(mContext))
//            .into(binding.profile)
//        if (getGender(mContext) == MALE) {
//            binding.male.isChecked = true
//        } else {
//            binding.male.isChecked = true
//        }
//    }
//
//    private fun putData() {
//
//        showLoading(true)
//
//        var gender = MALE
//        if (binding.female.isChecked) {
//            gender = FEMALE
//        }
//
//        val map = HashMap<String,Any>()
//        map[AppConstaints.USER_NAME] = binding.username.text.toString().trim()
//        map[AppConstaints.EMAIL] = binding.email.text.toString().trim()
//        map[GENDER] = gender
//        if (imageUri != null) {
//            map[PROFILE_URL] = imageUrl
//            SharedPref.setData(mContext, PROFILE_URL, imageUrl)
//        }
//
//        db.collection(AppConstaints.USER_COLLECTION)
//            .document(Utils.mUid(mContext))
//            .set(map, SetOptions.merge())
//            .addOnSuccessListener {
//                SharedPref.setData(mContext, AppConstaints.USER_NAME,binding.username.text.toString())
//                SharedPref.setData(mContext, AppConstaints.EMAIL,binding.email.text.toString())
//                SharedPref.setData(mContext,
//                    AppConstaints.AUTH_STATUS,
//                    AppConstaints.AUTH_STATUS_DONE
//                )
//                showLoading(false)
//                findNavController().popBackStack()
//            }.addOnFailureListener {
//                // On Failure
//                showLoading(false)
//                Snackbar.make(binding.root,
//                    AppConstaints.SOMETHING_WENT_WRONG, Snackbar.LENGTH_SHORT).show()
//            }
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
//    private fun showLoading(isShow : Boolean) {
//        if (isShow) {
//            binding.loading.visibility = View.VISIBLE
//            binding.finish.visibility = View.GONE
//        } else {
//            binding.loading.visibility = View.GONE
//            binding.finish.visibility = View.VISIBLE
//        }
//    }

}