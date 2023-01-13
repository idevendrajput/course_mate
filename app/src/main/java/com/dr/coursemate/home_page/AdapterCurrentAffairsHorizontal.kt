package com.dr.coursemate.home_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dr.coursemate.utils.AppConstant
import com.dr.coursemate.R
import com.dr.coursemate.utils.Utils.Companion.getFormattedTime
import com.dr.coursemate.databinding.RowItemCurrentAffairsBinding
import com.dr.coursemate.room.WordPressEntity

class AdapterCurrentAffairsHorizontal(val list: ArrayList<WordPressEntity>, val navController: NavController): RecyclerView.Adapter<AdapterCurrentAffairsHorizontal.DataHolder>() {

    class DataHolder(itemView: View, val dBinding: RowItemCurrentAffairsBinding) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val dBinding = RowItemCurrentAffairsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataHolder(dBinding.root, dBinding)
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {

        holder.dBinding.imageView.load(list[position].imageUrl)
        holder.dBinding.title.text = list[position].title
        holder.dBinding.dateTime.text = getFormattedTime(list[position].timestamp)

        holder.itemView.setOnClickListener {
            val args = Bundle()
            args.putString(AppConstant.TITLE, list[position].title)
            args.putString(AppConstant.CONTENT, list[position].content)
            navController.navigate(R.id.postViewer, args)
        }
    }

    override fun getItemCount(): Int {
         return if (list.size < 10) list.size else 10
    }

}
