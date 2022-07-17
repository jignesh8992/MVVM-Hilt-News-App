package com.jdroid.newsapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jdroid.newsapp.R
import com.jdroid.newsapp.ui.adapter.NewsAdapter
import com.jdroid.newsapp.databinding.FragmentSearchNewsBinding
import com.jdroid.newsapp.ui.NewsActivity
import com.jdroid.newsapp.ui.viewmodel.NewsViewModel
import com.jdroid.newsapp.utilities.Constants
import com.jdroid.newsapp.utilities.Constants.QUERY_DELAY
import com.jdroid.newsapp.utilities.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    lateinit var mBinding: FragmentSearchNewsBinding
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    private val TAG = javaClass.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        newsAdapter = NewsAdapter()
        mBinding.rvSearchNews.apply {
            adapter = newsAdapter
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }


        var job: Job? = null

        mBinding.etSearch.addTextChangedListener { text: Editable? ->
            job?.cancel()
            job = MainScope().launch {
                delay(QUERY_DELAY)
                text?.let {
                    if (text.toString().isNotEmpty()) {
                        viewModel.searchNews(text.toString())
                    }
                }
            }
        }

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)
        }


        viewModel.searchNews.observe(viewLifecycleOwner) { responseSearchNews ->

            when (responseSearchNews) {
                is Resource.Loading -> {
                    showProgress()
                }

                is Resource.Success -> {
                    hideProgress()
                    responseSearchNews.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                        val totalPage = it.totalResults!! / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchPageNumber == totalPage
                        if (isLastPage) {
                            mBinding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }

                }
                is Resource.Error -> {
                    hideProgress()
                    responseSearchNews.message?.let {
                        Log.i(TAG, "OnError: $it")
                        Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
                    }

                }
            }

        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.searchNews(mBinding.etSearch.text.toString())
                isScrolling = false
            }
        }
    }


    private fun hideProgress() {
        mBinding.paginationProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgress() {
        mBinding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }
}