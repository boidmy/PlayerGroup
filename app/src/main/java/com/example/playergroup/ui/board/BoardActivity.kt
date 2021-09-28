package com.example.playergroup.ui.board

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.playergroup.databinding.ActivityBoardBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.board.list.NoticeCategoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoardActivity : BaseActivity<ActivityBoardBinding>() {

    private val viewModel: BoardViewModel by viewModels()
    private val adapter = NoticeCategoryAdapter {

    }

    override fun getViewBinding() = ActivityBoardBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        binding.boardCateList.adapter = adapter
        viewModel.getCategoryList()
        observe()
    }

    fun observe() {
        viewModel.firebaseNoticeCategoryList.observe(this, {
            adapter.items = it
        })
    }
}