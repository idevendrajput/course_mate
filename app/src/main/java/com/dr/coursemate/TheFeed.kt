package com.dr.coursemate

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dr.coursemate.utils.AppConstant.COLLECTION_FEED
import com.dr.coursemate.utils.AppConstant.COMMENTS
import com.dr.coursemate.utils.AppConstant.DESCRIPTION
import com.dr.coursemate.utils.AppConstant.IMAGES
import com.dr.coursemate.utils.AppConstant.LIKE
import com.dr.coursemate.utils.AppConstant.PROFILE_URL
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.utils.AppConstant.USER_ID
import com.dr.coursemate.utils.AppConstant.USER_NAME
import com.dr.coursemate.utils.Utils.Companion.mUid
import com.dr.coursemate.databinding.FragmentTheFeedBinding
import com.dr.coursemate.databinding.RowItemFeedImagesBinding
import com.dr.coursemate.databinding.RowItemFeedPostBinding
import com.dr.coursemate.utils.AppConstant
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONObject

class TheFeed : Fragment() {

    private lateinit var binding: FragmentTheFeedBinding
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
        binding = FragmentTheFeedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

    }

    private fun actions() {

    }

    private fun updateUi() {

    }

    private fun loadData() {

        val list = ArrayList<ItemFeed>()

        val adapterFeed = AdapterFeed(list, mContext)

        db.collection(COLLECTION_FEED)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .limit(10)
            .get().addOnSuccessListener {

                binding.pb.visibility = View.GONE

                if (it.size() == 0) {
                    return@addOnSuccessListener
                }

                for (d in it) {

                    val userId = d?.getString(USER_ID) ?: ""

                    if (userId.isNotEmpty()) {

                        val caption = d?.getString(DESCRIPTION) ?: ""
                        val images = try {
                            d[IMAGES] as ArrayList<String>
                        } catch (e: Exception) {
                            ArrayList()
                        }
                        val commentsMap = try {
                            d[COMMENTS] as HashMap<String, Any>
                        } catch (e: Exception) {
                            HashMap()
                        }
                        val comments = extractComments(commentsMap)
                        val likes = try {
                            d[LIKE] as HashMap<String, Any>
                        } catch (e: Exception) {
                            HashMap()
                        }
                        val timestamp = d[TIMESTAMP_FIELD].toString().toLong()

                        list.add(
                            ItemFeed(
                                userId,
                                images = images,
                                comments = comments,
                                likes = extractLikes(likes),
                                caption = caption,
                                documentId = d.id,
                                timestamp = timestamp
                            )
                        )
                        adapterFeed.notifyItemInserted(list.lastIndex)
                    }
                }
            }

        binding.rvFeed.adapter = adapterFeed


    }

    private fun extractLikes(hashMap: HashMap<String, Any>) = run {

        val list = ArrayList<ItemLikes>()

        try {

            val jsonObject = JSONObject(hashMap as Map<String, Any>)
            val keys = jsonObject.keys()

            while (keys.hasNext()) {
                try {
                    val mapKey = keys.next()

                    val inObj = jsonObject.getJSONObject(mapKey)
                    val userId = inObj.getString(USER_ID)
                    val timestamp = inObj.get(TIMESTAMP_FIELD).toString().toLong()
                    list.add(
                        ItemLikes(
                            userId,
                            timestamp
                        )
                    )
                } catch (e: Exception) {}
            }

        } catch (e: Exception) { }


        list
    }

    private fun extractComments(hashMap: HashMap<String, Any>) = run {
        ArrayList<ItemComments>()
    }

}

class AdapterFeed(val list: ArrayList<ItemFeed>, mContext: Context): RecyclerView.Adapter<AdapterFeed.FeedHolder>() {

    val db = FirebaseFirestore.getInstance()
    val userId = mUid(mContext)

    class FeedHolder(itemView: View, val dBinding: RowItemFeedPostBinding) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHolder {
        val dBinding = RowItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedHolder(dBinding.root, dBinding)
    }

    override fun onBindViewHolder(holder: FeedHolder, position: Int) {

        holder.dBinding.caption.text = list[position].caption
        holder.dBinding.likeCount.text = list[position].likes.size.toString()

        val isLiked = list[position].likes.find { it.userId == userId } != null

        if (isLiked) {
            holder.dBinding.like.setImageResource(R.drawable.ic_baseline_thumb_up_24)
        } else {
            holder.dBinding.like.setImageResource(R.drawable.ic_outline_thumb_up_alt_24)
        }

        // get UserInfo

        Handler(Looper.getMainLooper())
            .post {

                holder.dBinding.viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                holder.dBinding.viewPager2.adapter = AdapterFeedImages(list[position].images)

                db.collection(AppConstant.USER_COLLECTION)
                    .document(list[position].userId)
                    .get().addOnSuccessListener {
                        try {
                            val name = it[USER_NAME].toString()
                            val profile = it[PROFILE_URL].toString()
                            holder.dBinding.username.text = name
                            holder.dBinding.profile.load(profile)
                        } catch (e: Exception) {
                        }
                    }
            }

        holder.dBinding.like.setOnClickListener {
            like(isLiked, position)
        }

    }

    private fun like(isLike: Boolean, position: Int) {

        if (isLike) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                list[position].likes.removeIf { it.userId == userId }
            }
        } else {
            list[position].likes.add(
                ItemLikes(
                    userId,
                    System.currentTimeMillis()
                )
            )
        }

        val likeMap = HashMap<String, Any>()

        for (i in list[position].likes) {
            val map = HashMap<String, Any>()
            map[USER_ID] = i.userId
            map[TIMESTAMP_FIELD] = i.timestamp
            likeMap[USER_ID] = map
        }

        val parentMap = HashMap<String, Any>()
        parentMap[LIKE] = likeMap

        CoroutineScope(IO)
            .launch {
                db.collection(COLLECTION_FEED)
                    .document(list[position].documentId)[parentMap] = SetOptions.merge()
            }

        notifyItemChanged(position)
    }


    private inner class AdapterFeedImages(val images: ArrayList<String>): RecyclerView.Adapter<AdapterFeedImages.ImageHolder>() {

        inner class ImageHolder(itemView: View, val dBinding: RowItemFeedImagesBinding) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
            val dBinding = RowItemFeedImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ImageHolder(dBinding.root, dBinding)
        }

        override fun onBindViewHolder(holder: ImageHolder, position: Int) {

            Glide.with(holder.itemView.context)
                .load(images[position])
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Toast.makeText(holder.itemView.context, "Failed to load image", Toast.LENGTH_SHORT).show()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.dBinding.pb.visibility = View.GONE
                        return false
                    }

                }).into(holder.dBinding.imageView)
            holder.dBinding.position.text = "${position+1}/${images.size}"

        }

        override fun getItemCount() = run {
            images.size
        }

    }

    override fun getItemCount() = run {
        list.size
    }


}

class ItemFeed(
    val userId: String,
    val caption: String,
    val images: ArrayList<String>,
    val comments: ArrayList<ItemComments>,
    val likes: ArrayList<ItemLikes>,
    val documentId: String,
    val timestamp: Long
)

class ItemComments(
    val userId: String,
    val comment: String,
    val likes: Int,
    val timestamp: Long
)

class ItemLikes(
    val userId: String,
    val timestamp: Long
)