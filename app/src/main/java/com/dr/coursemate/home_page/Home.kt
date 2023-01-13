package com.dr.coursemate.home_page

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.dr.coursemate.utils.AppConstant.ACTION_URL
import com.dr.coursemate.utils.AppConstant.COLLECTION_SLIDER
import com.dr.coursemate.utils.AppConstant.IMAGE_URL
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.R
import com.dr.coursemate.databinding.FragmentHomeBinding
import com.dr.coursemate.room.MyDatabase
import com.dr.coursemate.room.WordPressEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Home : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable : Runnable
    private lateinit var room: MyDatabase
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onDestroy() {
        try {
            sliderHandler.removeCallbacks(sliderRunnable)
        } catch (e: Exception) {}
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        room = MyDatabase.getDatabase(mContext)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actions()
        currentAffairs()
        slider()
    }

    private fun actions() {

        binding.notePad.setOnClickListener {
            findNavController().navigate(R.id.notes)
        }

        binding.quiz.setOnClickListener {
            findNavController().navigate(R.id.quizPlayer)
        }

        binding.notes.setOnClickListener {
            findNavController().navigate(R.id.booksAndNotes)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun slider()  {

        val sliderItems = ArrayList<SliderItems>()
        val adapter = SliderAdapter(sliderItems)

        for (i in 0..5) {
            sliderItems.add(SliderItems("",""))
            adapter.notifyItemInserted(adapter.itemCount - 1)
        }

             try {
                    binding.slider.adapter = adapter
                    binding.slider.clipToPadding = false
                    binding.slider.clipChildren = false
                    binding.slider.offscreenPageLimit = 3
                    binding.slider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

                    sliderRunnable = Runnable {
                        if (binding.slider.currentItem == sliderItems.size-1) {
                            binding.slider.setCurrentItem(0,true)
                            return@Runnable
                        }
                        binding.slider.currentItem = binding.slider.currentItem + 1
                    }

                    binding.slider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            sliderHandler.removeCallbacks(sliderRunnable)
                            sliderHandler.postDelayed(sliderRunnable, 2000)
                        }
                    })
                } catch (e : Exception) { }

        db.collection(COLLECTION_SLIDER)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .get().addOnSuccessListener {
                sliderItems.clear()
                adapter.notifyDataSetChanged()
                for (d in it) {
                    try {
                        val actionUrl = if (d[ACTION_URL] != null) d[ACTION_URL].toString() else ""
                        sliderItems.add(SliderItems(d[IMAGE_URL].toString(), actionUrl))
                        adapter.notifyItemInserted(adapter.itemCount - 1)
                    } catch (e : Exception) {}
                }
            }
    }

    private fun currentAffairs() {

        val list = room.wpDao().getCurrentAffairs() as ArrayList<WordPressEntity>
        binding.rvCurrentAffairs.adapter = AdapterCurrentAffairsHorizontal(list, findNavController())

        news()
        binding.rvFeed.adapter = AdapterFeed()

    }

    private fun news() {
        val list = room.wpDao().getNews() as ArrayList<WordPressEntity>
        binding.rvNews.adapter = AdapterNewsHomePage(list, findNavController())

    }

   inner class SliderAdapter(private val sliderItems: ArrayList<SliderItems>) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
            return SliderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_slider, parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
            holder.setImage(sliderItems[position])
            if (position == sliderItems.size - 2) {
                binding.slider.post(runnable)
            }
        }

        override fun getItemCount(): Int {
            return sliderItems.size
        }

        inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.imageView)
            fun setImage(sliderItems: SliderItems) {
                if (sliderItems.imageUrl.isNotEmpty()) {
                    imageView.load(sliderItems.imageUrl)
                }
            }

        }

        private val runnable = Runnable {
            sliderItems.addAll(sliderItems)
            notifyDataSetChanged()
        }
    }

}

class SliderItems(val imageUrl: String, val actionUrl: String)
