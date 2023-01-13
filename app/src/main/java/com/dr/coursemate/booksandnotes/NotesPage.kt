package com.dr.coursemate.booksandnotes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dr.coursemate.utils.AppConstant.COLLECTION_BOOKS_NOTES
import com.dr.coursemate.utils.AppConstant.DESCRIPTION
import com.dr.coursemate.utils.AppConstant.DOCUMENT_ID
import com.dr.coursemate.utils.AppConstant.IMAGES
import com.dr.coursemate.utils.AppConstant.PRICE
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.utils.AppConstant.TITLE
import com.dr.coursemate.R
import com.dr.coursemate.databinding.FragmentNotesPageBinding
import com.dr.coursemate.databinding.RowItemBooksNotesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotesPage : Fragment() {

    private lateinit var binding: FragmentNotesPageBinding
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
        binding = FragmentNotesPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUi()

    }

    private fun updateUi() {

        val list = ArrayList<NotesItems>()

        binding.noData.visibility = View.GONE

        val adapter = AdapterNotes(list)

        db.collection(COLLECTION_BOOKS_NOTES)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .get().addOnSuccessListener {

                binding.pb.visibility = View.GONE

                if (it.size() == 0) {
                    binding.noData.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                for (d in it) {
                    try {

                        val title = d?.getString(TITLE) ?: ""
                        val price = d?.get(PRICE) ?: ""
                        val description = d?.getString(DESCRIPTION) ?: ""
                        val imageUrl = d?.get(IMAGES) ?: ArrayList<String>()

                        list.add(
                            NotesItems(
                                title, imageUrl as ArrayList<String>, price.toString().toInt(), description, d.id
                            )
                        )

                        adapter.notifyItemInserted(list.lastIndex)
                    } catch (e: Exception) {}
                }

                if (list.isEmpty()) {
                    binding.noData.visibility = View.VISIBLE
                }

            }

        binding.rv.adapter = adapter

    }

    
    private inner class NotesItems(
        val title: String,
        val images: ArrayList<String>,
        val price: Int,
        val description: String,
        val id: String
    )

    private inner class AdapterNotes(val list: ArrayList<NotesItems>): RecyclerView.Adapter<AdapterNotes.BookHolder>() {

        inner class BookHolder(itemView: View, val dBinding: RowItemBooksNotesBinding) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
             val dBinding = RowItemBooksNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BookHolder(dBinding.root, dBinding)
        }

        override fun onBindViewHolder(holder: BookHolder, position: Int) {

            holder.dBinding.title.text = list[position].title

            if (list[position].images.isNotEmpty()) {
                holder.dBinding.imageView.load(list[position].images[0])
            }

            holder.dBinding.price.text = if (list[position].price == 0) "Free" else "Price: ${list[position].price} "

            holder.itemView.setOnClickListener {
                val args = Bundle()
                args.putString(DOCUMENT_ID, list[position].id)
                args.putString(DESCRIPTION, list[position].description)
                args.putInt(PRICE, list[position].price)
                args.putString(TITLE, list[position].title)
                args.putSerializable(IMAGES, list[position].images)
                findNavController().navigate(R.id.notesInfo, args)
            }

        }

        override fun getItemCount(): Int {
             return list.size
        }

    }

}