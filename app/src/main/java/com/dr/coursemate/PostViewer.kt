package com.dr.coursemate

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dr.coursemate.utils.AppConstant.CONTENT
import com.dr.coursemate.utils.AppConstant.TITLE
import com.dr.coursemate.databinding.FragmentPostViewerBinding

class PostViewer : Fragment() {

    private lateinit var binding: FragmentPostViewerBinding
    private lateinit var mContext: Context
    private lateinit var title: String
    private lateinit var content: String

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostViewerBinding.inflate(layoutInflater)

        title = arguments?.getString(TITLE).toString()
        content = arguments?.getString(CONTENT).toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actions()
        updateUi()

    }

    private fun actions() {

        binding.back.setOnClickListener { findNavController().popBackStack() }

    }

    private fun updateUi() {

        binding.title.text = title

        binding.content.settings.javaScriptEnabled = true
        binding.content.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.content.loadDataWithBaseURL(null, "const val STYLE_FOR_WEB_VIEW = \"<style>    code {     /* max-width: 60rem; */    max-width: 100%;  color: #EAB908; \\tmax-height:250px;     display: block;          overflow-y: scroll; \\toverflow-x: scroll; } pre {     background-color: #041979;     box-shadow: 0 0 1.5rem rgb(50 50 93 / 10%), 0 0.5rem 1.5rem rgb(0 0 0 / 7%);     border-radius: .5rem;     padding: 1rem;     box-sizing: border-box; }    .code-style {         font-size: 20px;         line-height: 28px;         background-color: lightblue;         color: #ffffff;       }     </style> <style>img{display: inline; height: auto; max-width: 100%;font-weight: bold; }</style>" + content,
            "text/html", "UTF-8", null)

    }

}