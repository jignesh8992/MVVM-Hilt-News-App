package com.jdroid.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.jdroid.newsapp.databinding.FragmentArticleBinding
import com.jdroid.newsapp.ui.NewsActivity
import com.jdroid.newsapp.ui.viewmodel.NewsViewModel

class ArticleFragment : Fragment() {

    lateinit var mBinding: FragmentArticleBinding
    lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentArticleBinding.inflate(inflater, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        mBinding.webView.apply {
            webChromeClient = WebChromeClient()
            loadUrl(args.article.url)
        }

        mBinding.fab.setOnClickListener {
            viewModel.upsertNews(args.article)
            Snackbar.make(view, "Article Saver Successfully", Snackbar.LENGTH_SHORT).show()
        }

    }
}