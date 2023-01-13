package com.dr.coursemate.auth

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
import com.dr.coursemate.utils.AppConstant.AUTH_STATUS
import com.dr.coursemate.utils.AppConstant.AUTH_STATUS_DONE
import com.dr.coursemate.utils.AppConstant.COUNTRY_CODE
import com.dr.coursemate.utils.AppConstant.EMAIL
import com.dr.coursemate.utils.AppConstant.ENTER_EMAIL_ERROR
import com.dr.coursemate.utils.AppConstant.ENTER_NAME_ERROR
import com.dr.coursemate.utils.AppConstant.ERROR_INVALID_EMAIL
import com.dr.coursemate.utils.AppConstant.GENDER
import com.dr.coursemate.utils.AppConstant.IMAGE_URL
import com.dr.coursemate.utils.AppConstant.PHONE
import com.dr.coursemate.utils.AppConstant.PROFILE_URL
import com.dr.coursemate.utils.AppConstant.SOMETHING_WENT_WRONG
import com.dr.coursemate.utils.AppConstant.USER_COLLECTION
import com.dr.coursemate.utils.AppConstant.USER_NAME
import com.dr.coursemate.utils.SharedPref
import com.dr.coursemate.utils.Utils.Companion.mUid
import com.dr.coursemate.databinding.FragmentInformationsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

class Information : Fragment() {

    lateinit var binding : FragmentInformationsBinding
    val db = FirebaseFirestore.getInstance()
    lateinit var mContext : Context
    private var imageUri: Uri? = null
    private var imageUrl = ""
    lateinit var downsizedImageBytes: ByteArray
    var gender = "Male"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInformationsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actions()

    }

    private fun actions() {

        binding.submit.setOnClickListener {

            if (binding.username.text.toString().trim().isEmpty()) {
                binding.username.error = ENTER_NAME_ERROR
                return@setOnClickListener
            }
            if (binding.email.text.toString().trim().isEmpty()) {
                binding.email.error = ENTER_EMAIL_ERROR
                return@setOnClickListener
            }
            if (!binding.email.text.toString().trim().contains("@")) {
                binding.email.error = ERROR_INVALID_EMAIL
                return@setOnClickListener
            }
            if (binding.female.isChecked) {
                gender = "Female"
            }

            showLoading()

            if (imageUri == null)
                pushData()
            else
                uploadImage()

        }

        binding.chooseProfile.setOnClickListener {
            getContent.launch("image/*")
        }

    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        binding.profile.setImageURI(uri)
    }

    private fun pushData() {

        val map = HashMap<String,Any>()
        map[USER_NAME] = binding.username.text.toString().trim()
        map[EMAIL] = binding.email.text.toString().trim()
        map[PHONE] = mUid(mContext)
        map[IMAGE_URL] = imageUrl
        map[GENDER] = gender
        map[COUNTRY_CODE] = SharedPref.getData(mContext, COUNTRY_CODE).toString()

        db.collection(USER_COLLECTION)
            .document(mUid(mContext))
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                // Save Data Offline
                SharedPref.setData(mContext, USER_NAME,binding.username.text.toString())
                SharedPref.setData(mContext, EMAIL,binding.email.text.toString())
                SharedPref.setData(mContext, GENDER,gender)
                SharedPref.setData(mContext, AUTH_STATUS, AUTH_STATUS_DONE)

            }.addOnFailureListener {
                // On Failure
                dismissLoading()
                Snackbar.make(binding.root, SOMETHING_WENT_WRONG,Snackbar.LENGTH_SHORT).show()
            }

    }

    private fun uploadImage() {

        val scaleDivider = 3

        try {

            val fullBitmap = MediaStore.Images.Media.getBitmap((activity as AppCompatActivity).contentResolver, imageUri)

            val scaleWidth = fullBitmap.width / scaleDivider
            val scaleHeight = fullBitmap.height / scaleDivider
            downsizedImageBytes = getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight)

            val reference = FirebaseStorage.getInstance().reference

            val file = imageUri
            val riversRef = reference.child("images/${file?.lastPathSegment}")
            val uploadTask = riversRef.putBytes(downsizedImageBytes)

            uploadTask.continueWithTask { task->

                if (!task.isSuccessful) {
                    throw task.exception!!
                }

                riversRef.downloadUrl.addOnCompleteListener { uploadTask ->
                    if (uploadTask.isSuccessful) {
                        imageUrl = uploadTask.result.toString()
                        pushData()
                        SharedPref.setData(mContext, PROFILE_URL, imageUrl)
                    } else {
                        dismissLoading()
                        Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                    }

                }
            }.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    dismissLoading()
                    Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                }
            }

        } catch (ioEx: IOException) {
            dismissLoading()
            Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading() {
        binding.pb.visibility = View.VISIBLE
        binding.submit.text = ""
        binding.submit.isEnabled = false
    }

    private fun dismissLoading() {
        binding.pb.visibility = View.GONE
        binding.submit.text = "Submit"
        binding.submit.isEnabled = true
    }

    @Throws(IOException::class)
    fun getDownsizedImageBytes(
        fullBitmap: Bitmap?,
        scaleWidth: Int,
        scaleHeight: Int
    ): ByteArray  {
        val scaledBitmap =
            Bitmap.createScaledBitmap(fullBitmap!!, scaleWidth, scaleHeight, true)

        // 2. Instantiate the downsized image content as a byte[]
        val baos = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

}