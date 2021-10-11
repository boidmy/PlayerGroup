package com.example.playergroup.ui.board.boardCreate

import android.os.Bundle
import androidx.activity.viewModels
import com.example.playergroup.databinding.ActivityBoardCreateBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.board.BoardViewModel
import com.example.playergroup.ui.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.ui.scrollselector.ScrollSelectorKeyValueBottomSheet
import com.example.playergroup.util.click

class BoardCreateActivity : BaseActivity<ActivityBoardCreateBinding>() {

    private val viewModel: BoardViewModel by viewModels()

    override fun getViewBinding() = ActivityBoardCreateBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initView()
        observe()
    }

    fun initView() {
        with(binding) {
            categoryListText click {
                setScrollerPicker(ScrollSelectorBottomSheet.Companion.ScrollSelectorType.CATEGORY)
            }

            btnNoticeBoard click {
                viewModel.selectCategory?.let { key ->
                    viewModel.insertBoard(
                        key = key,
                        title = boardEditTitle.text.toString(),
                        sub = boardEditSub.text.toString(),
                        id = ""
                    )
                }
            }
        }
    }

    fun observe() {
        viewModel.writeComplete.observe(this, {
            if (it) finish() //글등록에 성공하고 activityResult로 게시판 갱신어떻게 할지 협의필요
        })
    }

    private fun setScrollerPicker(type: ScrollSelectorBottomSheet.Companion.ScrollSelectorType) {
        ScrollSelectorKeyValueBottomSheet.newInstance(type) {
            binding.categoryListText.setText(it.first)
            viewModel.setSelectCategory(it.second)
        }.run {
            if (isVisible) return
            show(supportFragmentManager, tag)
        }
    }
}