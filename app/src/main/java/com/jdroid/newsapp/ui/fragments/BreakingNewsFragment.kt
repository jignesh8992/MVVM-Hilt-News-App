package com.jdroid.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jdroid.newsapp.R
import com.jdroid.newsapp.databinding.FragmentBreakingNewsBinding
import com.jdroid.newsapp.ui.NewsActivity
import com.jdroid.newsapp.ui.adapter.NewsAdapter
import com.jdroid.newsapp.ui.viewmodel.NewsViewModel
import com.jdroid.newsapp.utilities.Constants.QUERY_PAGE_SIZE
import com.jdroid.newsapp.utilities.Resource


class BreakingNewsFragment : Fragment() {

    lateinit var mBinding: FragmentBreakingNewsBinding
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private val TAG = javaClass.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        viewModel.getBreakingNews("in")

        newsAdapter = NewsAdapter()

        mBinding.rvBreakingNews.apply {
            adapter = newsAdapter
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)
        }

        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgress()
                }
                is Resource.Success -> {
                    hideProgress()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPage = it.totalResults!! / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPageNumber == totalPage
                        if (isLastPage) {
                            mBinding.rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }

                }
                is Resource.Error -> {
                    hideProgress()
                    response.message?.let {
                        Log.i(TAG, "OnError: $it")
                        Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resource.NoNetworkConnectivity -> {
                    Log.i(TAG, "NoNetworkConnectivity")
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getBreakingNews("in")
                isScrolling = false
            }
        }
    }

    private fun hideProgress() {
        mBinding.paginationProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgress() {
        isLoading = true
        mBinding.paginationProgressBar.visibility = View.VISIBLE
    }
}