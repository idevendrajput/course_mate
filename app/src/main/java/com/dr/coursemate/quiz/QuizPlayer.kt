package com.dr.coursemate.quiz

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.dr.coursemate.utils.AppConstant.DURATION
import com.dr.coursemate.utils.AppConstant.QUIZ
import com.dr.coursemate.databinding.ActivityQuizPlayerBinding
import com.google.android.material.button.MaterialButton
import kotlin.collections.ArrayList

class QuizPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityQuizPlayerBinding
    private lateinit var questionsList: ArrayList<QuizItems>
    private var answers = ArrayList<ItemAnswers>()
    private var position = 0
    private var duration = 0
    private var countDownTimer: CountDownTimer? = null

    override fun onStop() {
        countDownTimer?.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizPlayerBinding.inflate(layoutInflater)
        window.statusBarColor = Color.parseColor("#FF141A32")
        setContentView(binding.root)

        try {
            questionsList = intent.getSerializableExtra(QUIZ) as ArrayList<QuizItems>
            duration = intent.getIntExtra(DURATION, 0)
        } catch (e: Exception) {}

        if (questionsList.isNotEmpty())
            for (q in questionsList) answers.add(ItemAnswers(q.mapKey, -1))

        loadQuestions()
        countDownTimer()
        updateUi()
        actions()

    }

    private fun loadQuestions() {

        val item = questionsList[position]

        binding.question.text = item.question
        binding.optionA.text = item.optionA
        binding.optionB.text = item.optionB
        binding.optionC.text = item.optionC
        binding.optionD.text = item.optionD

    }

    private fun animateButtons() {

        binding.question.alpha = 0f
        for (v in binding.optionsContainer.children) {
            v.alpha = 0f
        }

        binding.question.animate().alpha(1f).duration = 500
        for (v in binding.optionsContainer.children) {
            v.animate().alpha(1f).duration = 500
        }

    }

    private fun actions() {

        binding.next.setOnClickListener {

            if (position == questionsList.lastIndex) {
                // Open Score Board
                return@setOnClickListener
            }

            position ++
            animateButtons()
            loadQuestions()
            updateUi()

        }

        for ((i,c) in binding.optionsContainer.children.withIndex()) {
            c.setOnClickListener {
                setSelection(i)
                selectAns(i)
            }
        }

    }

    private fun selectAns(selectedOption: Int) {
        for (a in answers) {
            if (a.questionId == questionsList[position].mapKey)
                a.selectedAns = selectedOption
        }
    }

    private fun setSelection(selectedButton: Int) {

        val selectionColor = "#FF018786"

        for ((i,c) in binding.optionsContainer.children.withIndex()) {
             val button = c as MaterialButton
             if (i == selectedButton) {
                 button.strokeColor = ColorStateList.valueOf(Color.parseColor(selectionColor))
                 button.iconTint = ColorStateList.valueOf(Color.parseColor(selectionColor))
             } else {
                 button.strokeColor = ColorStateList.valueOf(Color.WHITE)
                 button.iconTint = ColorStateList.valueOf(Color.WHITE)
             }
        }

    }

    private fun updateUi() {

        if (position == questionsList.lastIndex)
            binding.next.text = "Submit"
        else
            binding.next.text = "Next"

    }


    private fun countDownTimer() {

        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(duration*1000L,1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateProgress(duration, (millisUntilFinished/1000).toInt())
            }

            override fun onFinish() {
            }

        }.start()
    }

    private fun updateProgress(duration: Int, secondsUntilFinished: Int) {

        val seconds = String.format("%02d",secondsUntilFinished % 60)
        val minutes = String.format("%02d",(secondsUntilFinished - seconds.toInt()) / 60)

        binding.timerProgress.progress = ((secondsUntilFinished.toFloat() / duration.toFloat()) * 100).toInt()

        binding.timer.text = "$minutes:$seconds"

    }

}

class ItemAnswers(
    val questionId: String,
    var selectedAns: Int
)
