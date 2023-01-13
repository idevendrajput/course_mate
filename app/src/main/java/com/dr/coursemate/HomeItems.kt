package com.dr.coursemate

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dr.coursemate.databinding.FragmentHomeItemsBinding
import com.dr.coursemate.databinding.RowItemHomePageItemsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeItems : Fragment() {

    private lateinit var binding : FragmentHomeItemsBinding
    private val db = FirebaseFirestore.getInstance()
    lateinit var mContext : Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeItemsBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = ArrayList<ModelHomePageItems>()

        binding.rv.layoutManager = GridLayoutManager(mContext,2)

        val parent = arguments?.getString("tabName").toString()

        val adapter = AdapterHomePageItems(list)

        db.collection("Feed")
            .orderBy("timestamp",Query.Direction.DESCENDING)
            .whereArrayContains("parent",parent)
            .get().addOnSuccessListener { qs->
                try {
                   for (d in qs) {
                       list.add(ModelHomePageItems(d["title"].toString(),d["imageUrl"].toString(),d["price"].toString(), d.id))
                   }
                    adapter.notifyDataSetChanged()
                } catch (e : NullPointerException) { }

            }

        binding.rv.adapter = adapter

    }

}

class AdapterHomePageItems(val list: ArrayList<ModelHomePageItems>): RecyclerView.Adapter<AdapterHomePageItems.Viewholder>() {

    class Viewholder(itemView: View, val binding: RowItemHomePageItemsBinding) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Viewholder {
        val binding = RowItemHomePageItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Viewholder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
         holder.binding.title.text = list[position].title
         holder.binding.price.text = "₹${list[position].price}"
         Glide.with(holder.itemView.context)
             .load(list[position].imageUrl).into(holder.binding.imageView)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class ModelHomePageItems(val title : String, val imageUrl : String, val price : String, val id : String)