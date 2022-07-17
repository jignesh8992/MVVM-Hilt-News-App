package com.jdroid.newsapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.jdroid.newsapp.R
import com.jdroid.newsapp.databinding.ActivityNewsBinding
import com.jdroid.newsapp.ui.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {


    lateinit var mBinding: ActivityNewsBinding
    val viewModel: NewsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        mBinding.bottomNavigationView.setupWithNavController(navController)


    }
}