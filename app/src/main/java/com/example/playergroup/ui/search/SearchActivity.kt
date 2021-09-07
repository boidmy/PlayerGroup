package com.example.playergroup.ui.search

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import com.example.playergroup.databinding.ActivitySearchBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.util.click
import com.example.playergroup.util.hideKeyboard

class SearchActivity: BaseActivity<ActivitySearchBinding>() {

    private val viewModel by viewModels<SearchViewModel>()

    override fun getViewBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        binding.ivBack click { onBackPressed() }
        initEditTextView()
        initRecyclerView()
        initViewModel()
    }

    private fun initEditTextView() {
        binding.etsearch.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                //mainViewModel.requestMovieData(binding.userInputEditText.text.toString())
                //binding.progressBar isShow true
                hideKeyboard(binding.etsearch)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun initViewModel() {

    }

    private fun initRecyclerView() {
        binding.rvSearch.apply {

            setOnTouchListener { v, event ->
                hideKeyboard(binding.etsearch)
                false
            }
        }
    }
}