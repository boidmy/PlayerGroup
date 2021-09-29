package com.example.playergroup.ui.board.boardList

import android.os.Bundle
import androidx.activity.viewModels
import com.example.playergroup.databinding.ActivityBoardListBinding
import com.example.playergroup.ui.base.BaseActivity

class BoardListActivity : BaseActivity<ActivityBoardListBinding>() {

    private val viewModel: BoardListViewModel by viewModels()

    override fun getViewBinding() = ActivityBoardListBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {

        val key = intent.getStringExtra("key")
        key?.let {
            viewModel.getBoardList(it)
        }
    }
}