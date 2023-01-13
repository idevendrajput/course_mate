package com.dr.coursemate.home_page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dr.coursemate.databinding.RowItemFeedPostBinding

class AdapterFeed : RecyclerView.Adapter<AdapterFeed.DataHolder>() {

    class DataHolder(itemView: View, val dBinding: RowItemFeedPostBinding) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val dBinding = RowItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataHolder(dBinding.root, dBinding)
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }

}