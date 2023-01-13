package com.dr.coursemate.notes

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dr.coursemate.utils.AppConstant
import com.dr.coursemate.utils.AppConstant.ADD_LABEL
import com.dr.coursemate.utils.AppConstant.APP_LOGO_URL
import com.dr.coursemate.utils.AppConstant.CONTENT
import com.dr.coursemate.utils.AppConstant.DOCUMENT_ID
import com.dr.coursemate.utils.AppConstant.ERROR_ENTER_CONTENT
import com.dr.coursemate.utils.AppConstant.ERROR_ENTER_TITLE
import com.dr.coursemate.utils.AppConstant.IMAGE_URL
import com.dr.coursemate.utils.AppConstant.IS_EDIT_MODE
import com.dr.coursemate.utils.AppConstant.LABEL
import com.dr.coursemate.utils.AppConstant.NOTES_COLLECTION
import com.dr.coursemate.utils.AppConstant.NO_LABEL
import com.dr.coursemate.utils.AppConstant.SOMETHING_WENT_WRONG
import com.dr.coursemate.utils.AppConstant.THE_TITLE
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.R
import com.dr.coursemate.utils.Utils.Companion.mUid
import com.dr.coursemate.databinding.FragmentNotesEditorBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlin.collections.HashMap

class NotesEditor : Fragment() {

    private lateinit var binding : FragmentNotesEditorBinding
    private lateinit var mContext : Context
    private val db = FirebaseFirestore.getInstance()
    private var imageUri : Uri? = null
    private lateinit var imageUrl : String
    private lateinit var key : String
    private var isEditMode = false
    private lateinit var notesRef : CollectionReference

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesEditorBinding.inflate(layoutInflater)

        notesRef = db.collection(AppConstant.USER_COLLECTION)
            .document(mUid(mContext)).collection(NOTES_COLLECTION)

        key = arguments?.getString(DOCUMENT_ID).toString()
        isEditMode = arguments?.getBoolean(IS_EDIT_MODE) as Boolean

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.done.setOnClickListener {

            if (binding.title.text.toString().trim().isEmpty()) {
                binding.title.error = ERROR_ENTER_TITLE
                return@setOnClickListener
            }
            if (binding.content.text.toString().trim().isEmpty()) {
                binding.content.error = ERROR_ENTER_CONTENT
            }

            binding.done.visibility = View.GONE
            binding.pb.visibility = View.VISIBLE

            if (imageUri == null) {
                imageUrl = APP_LOGO_URL
                pushDataToDatabase()
            } else {
                uploadImage()
            }

        }

        if (isEditMode) {

            notesRef
                .document(key)
                .get().addOnSuccessListener {

                    val title = it[THE_TITLE].toString()
                    val imageUrl = it[IMAGE_URL].toString()
                    val label = if (it[LABEL] == null) NO_LABEL else it[LABEL].toString()
                    val content = it[CONTENT].toString()

                    binding.title.setText(title)
                    binding.label.text = label
                    binding.content.setText(content)
                    Glide.with(mContext)
                        .load(imageUrl).into(binding.imageView)
                }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.notes)
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.notes)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        binding.imageView.setImageURI(imageUri)
    }

    private fun uploadImage() {

        val reference = FirebaseStorage.getInstance().reference

        val file = imageUri
        val riversRef = reference.child("images/${file?.lastPathSegment}")
        val uploadTask = riversRef.putFile(file!!)

        uploadTask.continueWithTask { task->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            riversRef.downloadUrl.addOnCompleteListener { uploadTask ->
                if (uploadTask.isSuccessful) {
                    imageUrl = uploadTask.result.toString()
                    pushDataToDatabase()
                } else {
                    binding.pb.visibility = View.GONE
                    binding.done.visibility = View.VISIBLE
                    Snackbar.make(binding.root, SOMETHING_WENT_WRONG,Snackbar.LENGTH_SHORT).show()
                }
            }
        }.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                binding.pb.visibility = View.GONE
                binding.done.visibility = View.VISIBLE
                Snackbar.make(binding.root, SOMETHING_WENT_WRONG,Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun pushDataToDatabase() {

        val map = HashMap<String,Any>()
        map[THE_TITLE] = binding.title.text.toString().trim()
        map[CONTENT] = binding.content.text.toString().trim()
        if (!(binding.label.text.toString().trim() == NO_LABEL || binding.label.text.toString().trim() == ADD_LABEL)) {
            map[LABEL] =  binding.label.text.toString()
        }
        map[IMAGE_URL] = imageUrl
        if (!isEditMode) {
            map[TIMESTAMP_FIELD] = FieldValue.serverTimestamp()
        }

        notesRef
            .document(key).set(map, SetOptions.merge())
            .addOnSuccessListener {
               findNavController().navigate(R.id.notes)
            }
            .addOnFailureListener {
                binding.pb.visibility = View.GONE
                binding.done.visibility = View.VISIBLE
                Snackbar.make(binding.root, SOMETHING_WENT_WRONG,Snackbar.LENGTH_SHORT).show()
            }
    }

}