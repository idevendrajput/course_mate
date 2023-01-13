package com.dr.coursemate.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dr.coursemate.utils.AppConstant.COLLECTION_QUIZ
import com.dr.coursemate.utils.AppConstant.LEADER_BOARD
import com.dr.coursemate.utils.AppConstant.PROFILE_URL
import com.dr.coursemate.utils.AppConstant.USER_COLLECTION
import com.dr.coursemate.utils.AppConstant.USER_NAME
import com.dr.coursemate.databinding.ActivityScoreBoardBinding
import com.dr.coursemate.databinding.RowItemLeaderboardBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ScoreBoard : AppCompatActivity() {

    private lateinit var binding: ActivityScoreBoardBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var testId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBoardBinding.inflate(layoutInflater)

        Handler(Looper.getMainLooper()).post {
            loadData()
        }

        setContentView(binding.root)
        actions()

    }

    private fun actions() {

        binding.back.setOnClickListener { finish() }

        binding.retake.setOnClickListener {

        }

        binding.review.setOnClickListener {

        }

    }

    private fun loadData() {

        var allLeaders: ArrayList<ItemLeaders>

        val leader2 = ArrayList<ItemLeaders>()

        db.collection(COLLECTION_QUIZ)
            .document(testId)
            .get().addOnSuccessListener {
                try {

                    val leaderBoard = it?.get(LEADER_BOARD) ?: HashMap<String, Any>()

                    allLeaders = GsonBuilder().create().fromJson(JSONObject(leaderBoard as Map<String, Any>).toString(), object : TypeToken<ArrayList<ItemLeaders?>?>() {}.type)

                    try {
                        Collections.sort(allLeaders, Comparator<ItemLeaders?> { o1, o2 ->
                            (o2?.score ?: 0f).compareTo((o1?.score ?: 0f))
                        } as Comparator<ItemLeaders>)
                    } catch (e: Exception) {}

                    if (allLeaders.size > 3){
                        for (i in 3..allLeaders.lastIndex) {
                            leader2.add(allLeaders[i])
                        }
                    }

                    top3Leaders(allLeaders)
                    initLeaderBoard(leader2)

                } catch (e: Exception) {}
            }

    }

    private fun top3Leaders(leaders: ArrayList<ItemLeaders>) {

        for (i in 0..2) {
            val score = leaders[i].score
            val userId = leaders[i].userId
            if (i <= leaders.lastIndex) {
                setRankersProfile(i, score, userId)
            }
        }

    }

    private fun setRankersProfile(rank: Int, score: Float, userId: String) {

        db.collection(USER_COLLECTION)
            .document(userId)
            .get().addOnSuccessListener {
                try {
                    val name = it[USER_NAME].toString()
                    val profile = it[PROFILE_URL].toString()

                    when(rank) {
                        0-> {
                            binding.userNameRank1.text = name
                            binding.scoreRank1.text = score.toString()

                            Glide.with(this)
                                .load(profile).into(binding.profileRank1)
                        }
                        1-> {
                            binding.userNameRank1.text = name
                            binding.scoreRank1.text = score.toString()

                            Glide.with(this)
                                .load(profile).into(binding.profileRank1)
                        }
                        2-> {
                            binding.userNameRank1.text = name
                            binding.scoreRank1.text = score.toString()

                            Glide.with(this)
                                .load(profile).into(binding.profileRank1)
                        }
                    }

                } catch (e: Exception) { }
            }

    }

    private fun initLeaderBoard(leader2: ArrayList<ItemLeaders>) {

        val leader3 = ArrayList<ItemLeaders>()
        val adapter = LeadersAdapter(leader3)
        loadLeaders(0,9,leader2, leader3, adapter)
        binding.rvLeaders.adapter = adapter


    }

    private fun loadLeaders(startIndex: Int, endIndex: Int, leader2: ArrayList<ItemLeaders>, leader3: ArrayList<ItemLeaders>, adapter: LeadersAdapter) {

        for (i in startIndex..endIndex) {
            if (i < leader2.size) {
                leader3.add(leader2[i])
            }
        }

        if (endIndex <= leader2.lastIndex) {
            binding.loadMore.visibility = View.GONE
        }

        binding.loadMore.setOnClickListener {
            loadLeaders(endIndex+1, endIndex+10, leader2, leader3, adapter)
        }

    }

    private class ItemLeaders(
        val userId: String,
        val score: Float,
        val timestamp: Long
    )

    private inner class LeadersAdapter(val list: ArrayList<ItemLeaders>): RecyclerView.Adapter<LeadersAdapter.LeaderHolder>() {

        inner class LeaderHolder(itemView: View, val dBinding: RowItemLeaderboardBinding) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderHolder {
            val dBinding = RowItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LeaderHolder(dBinding.root, dBinding)
        }

        override fun onBindViewHolder(holder: LeaderHolder, position: Int) {

            holder.dBinding.score.text = list[position].score.toString()

            db.collection(USER_COLLECTION)
                .document(list[position].userId)
                .get().addOnSuccessListener {
                    try {
                        val name = it[USER_NAME].toString()
                        val profile = it[PROFILE_URL].toString()
                        holder.dBinding.name.text = name
                        Glide.with(holder.itemView.context)
                            .load(profile).into(holder.dBinding.profile)
                    } catch (e: Exception) { }
                }

            val rank = position + 3 // Because Top 3 is already filtered

            holder.dBinding.rank.text = rank.toString()

        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

}