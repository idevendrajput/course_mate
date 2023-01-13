package com.dr.coursemate.quiz

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dr.coursemate.utils.AppConstant.COLLECTION_QUIZ
import com.dr.coursemate.utils.AppConstant.HOME
import com.dr.coursemate.utils.AppConstant.IMAGE_URL
import com.dr.coursemate.utils.AppConstant.PARENT
import com.dr.coursemate.utils.AppConstant.THE_TITLE
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.databinding.FragmentQuizBinding
import com.dr.coursemate.databinding.RowItemQuizCategoriesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.collections.ArrayList

class Quiz : Fragment() {

    private lateinit var binding: FragmentQuizBinding
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
        binding = FragmentQuizBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actions()

        updateUi()

    }

    private fun actions() {

        binding.tb.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun updateUi() {

        binding.noData.visibility = View.GONE

        val list = ArrayList<ItemQuizCategories>()

        db.collection(COLLECTION_QUIZ)
            .whereEqualTo(PARENT, HOME)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .get().addOnSuccessListener {

                if (it.size() == 0) {
                    binding.noData.visibility = View.VISIBLE
                }

                for (d in it) {
                    list.add(
                        ItemQuizCategories(
                            d[THE_TITLE].toString(),
                            d[IMAGE_URL].toString(),
                            d.id
                        )
                    )
                }

                binding.rv.adapter = AdapterQuizCategories(list)

            }

    }

    private inner class AdapterQuizCategories(val list: ArrayList<ItemQuizCategories>): RecyclerView.Adapter<AdapterQuizCategories.QuizHolder>() {

        inner class QuizHolder(itemView: View, val dBinding: RowItemQuizCategoriesBinding) : RecyclerView.ViewHolder(itemView) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizHolder {
            val dBinding = RowItemQuizCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return QuizHolder(dBinding.root, dBinding)
        }

        override fun onBindViewHolder(holder: QuizHolder, position: Int) {

            holder.dBinding.title.text = list[position].title
            holder.dBinding.image.load(list[position].imagesUrl)

        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

    
    private inner class ItemQuizCategories(val title: String, val imagesUrl: String, val id: String)

}