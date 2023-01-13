package com.dr.coursemate.booksandnotes

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dr.coursemate.R
import com.dr.coursemate.databinding.FragmentBooksAndNotesBinding
import com.google.android.material.tabs.TabLayoutMediator
import javax.annotation.meta.When

class BooksAndNotes : Fragment() {

    private lateinit var binding: FragmentBooksAndNotesBinding
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksAndNotesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper())
            .post {
                loadTabs()
            }

        actions()

    }

    private fun actions() {
        binding.back.setOnClickListener { findNavController().popBackStack() }
    }

    private fun loadTabs() {

        val tabs = arrayOf("Notes", "Books")

        binding.viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager2.adapter = activity?.supportFragmentManager?.let { AdapterViewPager(it) }

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager2
        ) { tab, position ->
            tab.text = tabs[position]
        }.attach()

    }

    private inner class AdapterViewPager(fragmentManager: FragmentManager) : FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
           return when(position) {
               0-> NotesPage()
               1-> BooksPages()
               else-> NotesPage()
           }
        }

    }

}