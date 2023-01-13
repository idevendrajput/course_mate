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
import com.dr.coursemate.utils.AppConstant
import com.dr.coursemate.utils.AppConstant.ANSWER
import com.dr.coursemate.utils.AppConstant.DATA_TYPE
import com.dr.coursemate.utils.AppConstant.DOCUMENT_ID
import com.dr.coursemate.utils.AppConstant.EXPLANATION
import com.dr.coursemate.utils.AppConstant.IMAGE_URL
import com.dr.coursemate.utils.AppConstant.OPTION_A
import com.dr.coursemate.utils.AppConstant.OPTION_B
import com.dr.coursemate.utils.AppConstant.OPTION_C
import com.dr.coursemate.utils.AppConstant.OPTION_D
import com.dr.coursemate.utils.AppConstant.PARENT
import com.dr.coursemate.utils.AppConstant.QUESTION
import com.dr.coursemate.utils.AppConstant.QUIZ
import com.dr.coursemate.utils.AppConstant.THE_TITLE
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.R
import com.dr.coursemate.utils.Utils.Companion.returnTimeAgo
import com.dr.coursemate.databinding.FragmentQuizChildBinding
import com.dr.coursemate.databinding.RowItemQuizBinding
import com.dr.coursemate.databinding.RowItemQuizCategoriesBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.json.JSONObject

class QuizChild : Fragment() {

    private lateinit var binding: FragmentQuizChildBinding
    private lateinit var mContext: Context
    private val db = FirebaseFirestore.getInstance()
    private lateinit var parentId: String
    private lateinit var title: String

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizChildBinding.inflate(layoutInflater)

        actions()
        updateUi()
        loadData()

        return binding.root
    }

    private fun actions() {

    }

    private fun updateUi() {

        binding.tb.title = title

    }

    private fun loadData() {

        binding.noData.visibility = View.GONE

        db.collection(AppConstant.COLLECTION_QUIZ)
            .whereEqualTo(PARENT, parentId)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .get().addOnSuccessListener {

                binding.pb.visibility = View.GONE

                if (it.size() == 0) {
                    binding.noData.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                val quizList = ArrayList<ItemQuiz>()
                val setsList = ArrayList<ItemQuiz>()

                for (d in it) {

                    if (d[DATA_TYPE].toString() == QUIZ) {
                        quizList.add(
                           ItemQuiz(
                                d[THE_TITLE].toString(),
                                d[IMAGE_URL].toString(),
                                returnTimeAgo(d.getDate(TIMESTAMP_FIELD)?.time ?: System.currentTimeMillis()),
                                accessQuizQuestions(d),
                                d.id
                          ))
                    } else {
                        setsList.add(
                            ItemQuiz(
                                d[THE_TITLE].toString(),
                                d[IMAGE_URL].toString(),
                                returnTimeAgo(d.getDate(TIMESTAMP_FIELD)?.time ?: System.currentTimeMillis()),
                                null,
                                d.id
                            ))
                    }
                }

                binding.rv.adapter = AdapterQuiz(quizList)
                binding.rvSets.adapter = AdapterQuizSet(setsList)

                if (quizList.isNotEmpty()) binding.txtQuiz.visibility = View.VISIBLE
                if (setsList.isNotEmpty()) binding.txtSets.visibility = View.VISIBLE

            }
    }

    private fun accessQuizQuestions(d: DocumentSnapshot) = run {

        val quizMap = d[QUIZ] as HashMap<String, Any>

        val quizObj = JSONObject(quizMap as Map<String, Any>)
        val keys: Iterator<String> = quizObj.keys()

        val list = ArrayList<QuizItems>()

        while (keys.hasNext()) {

            val mapKey = keys.next()

            val questionObj = quizObj.get(mapKey) as JSONObject

            val question = questionObj.get(QUESTION).toString()
            val optionA = questionObj.get(OPTION_A).toString()
            val optionB = questionObj.get(OPTION_B).toString()
            val optionC = questionObj.get(OPTION_C).toString()
            val optionD = questionObj.get(OPTION_D).toString()
            val explanation = questionObj.get(EXPLANATION).toString()
            val answer = questionObj.get(ANSWER).toString()
            val timestamp = questionObj.get(TIMESTAMP_FIELD).toString().toLong()

            list.add(
              QuizItems(
                  question, optionA, optionB, optionC, optionD, answer, explanation, timestamp, mapKey
              )
            )

        }

        list
    }

    private inner class AdapterQuiz(val list: ArrayList<ItemQuiz>): RecyclerView.Adapter<AdapterQuiz.QuizHolder>() {

        inner class QuizHolder(itemView: View, val dBinding: RowItemQuizBinding) : RecyclerView.ViewHolder(itemView) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizHolder {
            val dBinding = RowItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return QuizHolder(dBinding.root, dBinding)
        }

        override fun onBindViewHolder(holder: QuizHolder, position: Int) {

            holder.dBinding.title.text = list[position].title
            holder.dBinding.imageView.load(list[position].imagesUrl)
            holder.dBinding.dateTime.text = list[position].dateTime

            holder.dBinding.imageView.setOnClickListener {
                openQuizPlayer(position)
            }

            holder.dBinding.title.setOnClickListener {
                openQuizPlayer(position)
            }

            holder.dBinding.save.setOnClickListener {
                // Save in room and firestore
            }

            holder.dBinding.share.setOnClickListener {
                // Share the quiz here......
            }

        }

        private fun openQuizPlayer(position: Int) {
            val args = Bundle()
            args.putString(THE_TITLE, list[position].title)
            args.putString(DOCUMENT_ID, list[position].id)
            args.putSerializable(QUIZ, list[position].quizItems)
            findNavController().navigate(R.id.quizPlayer, args)
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    private inner class AdapterQuizSet(val list: ArrayList<ItemQuiz>): RecyclerView.Adapter<AdapterQuizSet.QuizHolder>() {

        inner class QuizHolder(itemView: View, val dBinding: RowItemQuizCategoriesBinding) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizHolder {
            val dBinding = RowItemQuizCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return QuizHolder(dBinding.root, dBinding)
        }

        override fun onBindViewHolder(holder: QuizHolder, position: Int) {

            holder.dBinding.title.text = list[position].title
            holder.dBinding.image.load(list[position].imagesUrl)

            holder.itemView.setOnClickListener {
                openNewTab(position)
            }

        }

        private fun openNewTab(position: Int) {
            val args = Bundle()
            args.putString(THE_TITLE, list[position].title)
            args.putString(PARENT, list[position].id)
            findNavController().navigate(R.id.quizChild, args)
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

}


