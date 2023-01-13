package com.dr.coursemate.home_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dr.coursemate.utils.AppConstant.CONTENT
import com.dr.coursemate.utils.AppConstant.TITLE
import com.dr.coursemate.R
import com.dr.coursemate.utils.Utils
import com.dr.coursemate.databinding.RowItemNewsHorizontalHomeBinding
import com.dr.coursemate.room.WordPressEntity

class AdapterNewsHomePage(val list: ArrayList<WordPressEntity>, val navController: NavController): RecyclerView.Adapter<AdapterNewsHomePage.DataHolder>() {

    class DataHolder(itemView: View, val dBinding: RowItemNewsHorizontalHomeBinding) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val dBinding = RowItemNewsHorizontalHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataHolder(dBinding.root, dBinding)
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        holder.dBinding.imageView.load(list[position].imageUrl)
        holder.dBinding.title.text = list[position].title
        holder.dBinding.dateTime.text = Utils.getFormattedTime(list[position].timestamp)

        holder.itemView.setOnClickListener {
            val args = Bundle()
            args.putString(TITLE, list[position].title)
            args.putString(CONTENT, list[position].content)
            navController.navigate(R.id.postViewer, args)
        }

    }

    override fun getItemCount(): Int {
        return if (list.size < 10) list.size else 10
    }

}
