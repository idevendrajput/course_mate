package com.dr.coursemate.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dr.coursemate.utils.AppConstant
import com.dr.coursemate.utils.AppConstant.ATTEMPTED
import com.dr.coursemate.utils.AppConstant.CORRECT_ANS
import com.dr.coursemate.utils.AppConstant.INCORRECT_ANS
import com.dr.coursemate.utils.AppConstant.MAX_SCORE
import com.dr.coursemate.utils.AppConstant.NOT_ATTEMPTED
import com.dr.coursemate.utils.AppConstant.PERCENTAGE
import com.dr.coursemate.utils.AppConstant.SCORE
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.utils.AppConstant.TOTAL_QUESTIONS
import com.dr.coursemate.utils.Utils.Companion.mUid
import com.dr.coursemate.databinding.ActivityReviewBinding
import com.dr.coursemate.databinding.RowItemReviewQuestionsBinding
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Review : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private lateinit var testId: String
    private val db = FirebaseFirestore.getInstance()
    private lateinit var questions: ArrayList<QuizItems>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            questions = intent.getSerializableExtra(AppConstant.QUIZ) as ArrayList<QuizItems>
        } catch (e: Exception) {}

        actions()

        Handler(Looper.getMainLooper())
            .post {
                loadData()
                loadQuestions()
            }

    }

    private fun actions() {

        binding.back.setOnClickListener { finish() }

    }

    private fun loadData() {

        db.collection(AppConstant.COLLECTION_QUIZ)
            .document(testId)
            .get().addOnSuccessListener {
                try {

                    val leaderBoard = it?.get(AppConstant.LEADER_BOARD) ?: HashMap<String, Any>()

                    val leaderObj = JSONObject(leaderBoard as Map<String,Any>)

                    val myObj = leaderObj.get(mUid(this)) as JSONObject

                    val score = myObj.get(SCORE).toString()
                    val attemptedQuestions = myObj.get(ATTEMPTED).toString()
                    val notAttempted = myObj.get(NOT_ATTEMPTED).toString()
                    val totalQuestions = myObj.get(TOTAL_QUESTIONS).toString()
                    val correctAns = myObj.get(CORRECT_ANS).toString()
                    val incorrectAns = myObj.get(INCORRECT_ANS).toString()
                    val percentage = myObj.get(PERCENTAGE).toString()
                    val maxScore = myObj.get(MAX_SCORE).toString()
                    val timestamp = myObj.get(TIMESTAMP_FIELD).toString().toLong()

                    binding.score.text = score
                    binding.attempted.text  = attemptedQuestions
                    binding.notAttempted.text = notAttempted
                    binding.totalQuestions.text = totalQuestions
                    binding.correct.text = correctAns
                    binding.incorrect.text = incorrectAns
                    binding.percentage.text = percentage
                    binding.maxMarks.text = maxScore
                    binding.time.text = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(timestamp)
                    )

                } catch (e: Exception) {}
            }
    }

    private fun loadQuestions() {

        binding.rvQuestions.adapter = AdapterReview(questions)

    }

    private inner class AdapterReview(val list: ArrayList<QuizItems>): RecyclerView.Adapter<AdapterReview.QuestionHolder>() {

        inner class QuestionHolder(itemView: View, val dBinding: RowItemReviewQuestionsBinding) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = run {
            val dBinding = RowItemReviewQuestionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            QuestionHolder(dBinding.root, dBinding)
        }

        override fun onBindViewHolder(holder: QuestionHolder, position: Int) {

            val qn = position + 1
            holder.dBinding.questionNo.text = qn.toString()
            holder.dBinding.question.text = list[position].question
            holder.dBinding.optionA.text = list[position].optionA
            holder.dBinding.optionB.text = list[position].optionB
            holder.dBinding.optionC.text = list[position].optionC
            holder.dBinding.optionD.text = list[position].optionD
            holder.dBinding.explanation.text = list[position].explanation

            holder.dBinding.arrow.setOnClickListener {
                if (holder.dBinding.optionsContainer.visibility == View.VISIBLE) {
                    holder.dBinding.arrow.animate().rotation(0f).duration = 400
                    holder.dBinding.optionsContainer.visibility = View.GONE
                } else {
                    holder.dBinding.arrow.animate().rotation(180f).duration = 400
                    holder.dBinding.optionsContainer.visibility = View.VISIBLE
                }
            }

            if (list[position].explanation.trim().isNotEmpty())
                holder.dBinding.explanation.visibility = View.VISIBLE
            else
                holder.dBinding.explanation.visibility = View.GONE

        }

        override fun getItemCount() = run {
            list.size
        }


    }

}