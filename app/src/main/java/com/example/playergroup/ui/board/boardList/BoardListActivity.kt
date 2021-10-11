package com.example.playergroup.ui.board.boardList

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.example.playergroup.data.*
import com.example.playergroup.databinding.ActivityBoardListBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.board.BoardViewModel
import com.example.playergroup.ui.board.boardCreate.BoardCreateActivity
import com.example.playergroup.ui.board.boardList.list.BoardListAdapter
import com.example.playergroup.ui.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.ui.scrollselector.ScrollSelectorKeyValueBottomSheet
import com.example.playergroup.util.CategoryUtil
import com.example.playergroup.util.LandingRouter.move
import com.example.playergroup.util.click
import com.example.playergroup.util.observe

class BoardListActivity : BaseActivity<ActivityBoardListBinding>() {

    private val viewModel: BoardViewModel by viewModels()
    private val adapter = BoardListAdapter {
        itemClickEvent(it)
    }

    override fun getViewBinding() = ActivityBoardListBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initView()
        observe()
    }

    fun initView() {
        when (configModule.categorySelectMode) {
            CategoryUtil.RECRUIT_CATEGORY.value -> setSelectCategory(CategoryUtil.RECRUIT_CATEGORY.key)
            else -> setSelectCategory(CategoryUtil.FREE_CATEGORY.key)
        }

        with(binding) {
            boardRv.adapter = adapter
            categoryListText.setText(configModule.categorySelectMode)
            boardWrite click {
                val intent = Intent(this@BoardListActivity, BoardCreateActivity::class.java)
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
        ScrollSelectorKeyValueBottomSheet.newInstance(type) {
            binding.categoryListText.setText(it.first)
            setSelectCategory(it.second)
            configModule.categorySelectMode = it.first
        }.run {
            if (isVisible) return
            show(supportFragmentManager, tag)
        }
    }

    private fun renderBoardList(boardList: MutableList<NoticeBoardItem>?) {
        adapter.items = boardList.orEmpty() as MutableList<NoticeBoardItem>
    }

    private fun setSelectCategory(key: String) {
        viewModel.setSelectCategory(key)
        getBoardList()
    }

    private fun getBoardList() {
        viewModel.getBoardList()
    }

    private fun itemClickEvent(itemKey: String) {
        Bundle().apply {
            putString(INTENT_EXTRA_KEY_FIRST, viewModel.selectCategory)
            putString(INTENT_EXTRA_KEY_SECOND, itemKey)
        }.run {
            move(this@BoardListActivity, RouterEvent(type = Landing.BOARD_DETAIL, bundle = this))
        }
    }
}