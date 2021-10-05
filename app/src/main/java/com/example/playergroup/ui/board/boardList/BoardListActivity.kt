package com.example.playergroup.ui.board.boardList

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.example.playergroup.data.NoticeBoardItem
import com.example.playergroup.databinding.ActivityBoardListBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.board.BoardViewModel
import com.example.playergroup.ui.board.boardCreate.BoardCreateActivity
import com.example.playergroup.ui.board.boardList.list.BoardListAdapter
import com.example.playergroup.ui.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.ui.scrollselector.ScrollSelectorKeyValueBottomSheet
import com.example.playergroup.util.CategoryUtil
import com.example.playergroup.util.click
import com.example.playergroup.util.observe

class BoardListActivity : BaseActivity<ActivityBoardListBinding>() {

    private val viewModel: BoardViewModel by viewModels()
    private val adapter = BoardListAdapter()

    override fun getViewBinding() = ActivityBoardListBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initView()
        observe()
    }

    fun initView() {
        with(binding) {
            //var key = "0o2JJc0Zyethb84KT8HR" //키 어떻게 처리할지
            val key = when (configModule.categorySelectMode) {
                CategoryUtil.RECRUIT_CATEGORY.value -> CategoryUtil.RECRUIT_CATEGORY.key
                else -> CategoryUtil.FREE_CATEGORY.key
            }
            key.let {
                viewModel.getBoardList(it)
            }
            boardRv.adapter = adapter

            boardWrite click {
                val intent = Intent(this@BoardListActivity, BoardCreateActivity::class.java)
                intent.putExtra("key", key)
                startActivity(intent) //액티비티리절트 받아서 처리할지 확인후 처리
            }
            categoryListText click {
                setScrollerPicker(ScrollSelectorBottomSheet.Companion.ScrollSelectorType.CATEGORY)
            }
        }
    }

    fun observe() {
        with(viewModel) {
            observe(boardList, ::renderBoardList)
        }
    }

    private fun setScrollerPicker(type: ScrollSelectorBottomSheet.Companion.ScrollSelectorType) {
        val newInstance = ScrollSelectorKeyValueBottomSheet.newInstance(type) {
            binding.categoryListText.setText(it.first)
            viewModel.getBoardList(it.second)
            configModule.categorySelectMode = it.first
        }
        if (newInstance.isVisible) return
        newInstance.show(supportFragmentManager, newInstance.tag)
    }

    private fun renderBoardList(boardList: MutableList<NoticeBoardItem>?) {
        adapter.items = boardList.orEmpty() as MutableList<NoticeBoardItem>
    }
}