package com.dr.coursemate.notes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dr.coursemate.utils.AppConstant
import com.dr.coursemate.utils.AppConstant.CONFIRM
import com.dr.coursemate.utils.AppConstant.CONFIRM_DELETE
import com.dr.coursemate.utils.AppConstant.CONTENT
import com.dr.coursemate.utils.AppConstant.DATE_TIME
import com.dr.coursemate.utils.AppConstant.DECLINE
import com.dr.coursemate.utils.AppConstant.DEFAULT_DATE_FORMAT
import com.dr.coursemate.utils.AppConstant.DEFAULT_ORDER_FIELD
import com.dr.coursemate.utils.AppConstant.DELETE
import com.dr.coursemate.utils.AppConstant.DELETED_SUCCESSFULLY
import com.dr.coursemate.utils.AppConstant.DOCUMENT_ID
import com.dr.coursemate.utils.AppConstant.IMAGE_URL
import com.dr.coursemate.utils.AppConstant.LABEL
import com.dr.coursemate.utils.AppConstant.NOTES_COLLECTION
import com.dr.coursemate.utils.AppConstant.NO_LABEL
import com.dr.coursemate.utils.AppConstant.NO_RESULT_FOUND
import com.dr.coursemate.utils.AppConstant.THE_TITLE
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.utils.AppConstant.USER_COLLECTION
import com.dr.coursemate.R
import com.dr.coursemate.utils.Utils.Companion.mUid
import com.dr.coursemate.databinding.FragmentNotesBinding
import com.dr.coursemate.databinding.RowItemNotesBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Notes : Fragment() {

    private lateinit var binding : FragmentNotesBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var mContext : Context
    private lateinit var notesRef : CollectionReference
    private val list = ArrayList<ModelNotes>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(layoutInflater)

        notesRef = db.collection(USER_COLLECTION)
            .document(mUid(mContext)).collection(NOTES_COLLECTION)

        return binding.root
    }

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false)

        val adapter = AdapterNotes(list)

        notesRef
            .orderBy(DEFAULT_ORDER_FIELD,Query.Direction.DESCENDING)
            .addSnapshotListener { qs, _ ->
                if (qs != null) {
                    if (qs.size() == 0) {
                        binding.noData.visibility = View.VISIBLE
                        binding.pb.visibility = View.GONE
                    }
                    list.clear()
                    for (d in qs) {

                        try {
                            var label = NO_LABEL
                            if (d[LABEL] != null) {
                                label = d[LABEL].toString()
                            }
                            val dateTime = SimpleDateFormat(DEFAULT_DATE_FORMAT).format(d.getDate(
                                TIMESTAMP_FIELD) as Date)

                            list.add(ModelNotes(d[THE_TITLE].toString(),
                                d[IMAGE_URL].toString(),
                                label,
                                dateTime,
                                d[CONTENT].toString(),
                                d.id
                            ))
                            adapter.notifyDataSetChanged()
                            binding.pb.visibility = View.GONE
                        } catch (e : NullPointerException) { }

                    }
                }
            }

        binding.rv.adapter = adapter

        actions()

    }

    private fun actions() {

        visibilityControls()

        binding.search.addTextChangedListener {
            search(binding.search.text.toString().lowercase().replace(" ","").trim(),list)
        }

        binding.add.setOnClickListener {
            val args = Bundle()
            args.putString(DOCUMENT_ID, UUID.randomUUID().toString())
            args.putBoolean(AppConstant.IS_EDIT_MODE, false)
            findNavController().navigate(R.id.notesEditor, args)
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.home)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun visibilityControls() {
        binding.btnSearch.setOnClickListener {
            if (binding.search.visibility == View.VISIBLE) {
                binding.search.visibility = View.GONE
                binding.btnSearch.animate().rotation(360f).duration = 500
                binding.btnSearch.setImageResource(R.drawable.ic_round_search_24)
            } else {
                binding.search.visibility = View.VISIBLE
                binding.btnSearch.animate().rotation(0f).duration = 500
                binding.btnSearch.setImageResource(R.drawable.ic_round_keyboard_arrow_down_24)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun search(s : String, list: ArrayList<ModelNotes>) {

        if (s.isNotEmpty()) {

            binding.rv.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false)

            val result = ArrayList<ModelNotes>()
            val adapter = AdapterNotes(result)

            result.clear()

            for (r in list) {
                if (r.label.lowercase().replace(" ","").trim().contains(s) ||
                    r.title.lowercase().replace(" ","").trim().contains(s) ||
                    r.datetime.lowercase().replace(" ","").trim().contains(s)
                ) {
                    result.add(ModelNotes(r.title,
                        r.imageUrl,
                        r.label,
                        r.datetime,
                        r.content,
                        r.id
                    ))
                    adapter.notifyDataSetChanged()
                    binding.pb.visibility = View.GONE
                }
            }

            if (result.size == 0) {
                binding.noData.text = NO_RESULT_FOUND
                binding.noData.visibility = View.VISIBLE
            }

            binding.rv.adapter = adapter
        }

    }

    private inner class AdapterNotes(private val list: ArrayList<ModelNotes>) : RecyclerView.Adapter<AdapterNotes.MyViewHolder>() {

        inner class MyViewHolder(val binding: RowItemNotesBinding,itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val binding = RowItemNotesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return MyViewHolder(binding,binding.root)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.binding.title.text = list[position].title
            holder.binding.dateTime.text = list[position].datetime
            holder.binding.label.text = list[position].label
            Glide.with(holder.itemView.context)
                .load(list[position].imageUrl).into(holder.binding.imageView)

            holder.itemView.setOnLongClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle(DELETE)
                    .setMessage(CONFIRM_DELETE)
                    .setPositiveButton(CONFIRM) { _, _->
                        notesRef.document(list[position].id)
                            .delete()
                        Snackbar.make(binding.root, DELETED_SUCCESSFULLY, Snackbar.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(DECLINE,null)
                    .create().show()
                false
            }

            holder.itemView.setOnClickListener {
                val args = Bundle()
                args.putString(THE_TITLE, list[position].title)
                args.putString(IMAGE_URL, list[position].imageUrl)
                args.putString(CONTENT, list[position].content)
                args.putString(DATE_TIME, list[position].datetime)
                args.putString(DOCUMENT_ID, list[position].id)
                args.putString(LABEL, list[position].label)
                findNavController().navigate(R.id.notesViewer, args)
            }

        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

}



class ModelNotes(val title : String, val imageUrl : String, val label : String, val datetime : String, val content : String, val id : String)