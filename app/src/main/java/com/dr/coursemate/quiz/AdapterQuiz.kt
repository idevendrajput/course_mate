package com.dr.coursemate.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dr.coursemate.databinding.RowItemQuizBinding

class AdapterQuiz: RecyclerView.Adapter<AdapterQuiz.QuizHolder>() {

    class QuizHolder(itemView: View, val dBinding: RowItemQuizBinding) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizHolder {
        val dBinding = RowItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizHolder(dBinding.root, dBinding)
    }

    override fun onBindViewHolder(holder: QuizHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}