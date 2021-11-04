package com.example.playergroup.ui.board.boardList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.playergroup.data.*
import com.example.playergroup.databinding.ActivityBoardListBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.board.BoardViewModel
import com.example.playergroup.ui.board.boardCreate.BoardCreateActivity
import com.example.playergroup.ui.board.boardList.list.BoardListAdapter
import com.example.playergroup.ui.dialog.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.util.CategoryUtil
import com.example.playergroup.util.LandingRouter.move
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.click
import com.example.playergroup.util.observe

class BoardListActivity : BaseActivity<ActivityBoardListBinding>() {

    private val viewModel: BoardViewModel by viewModels()
    private val adapter = BoardListAdapter {
        itemClickEvent(it)
    }
    private val activityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val value = it.data?.getSerializableExtra(INTENT_SERIALIZABLE)
            viewModel.addBoardList(value as NoticeBoardItem)
            
        }
    }

    override fun getViewBinding() = ActivityBoardListBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initView()
        observe()
    }

    fun initView() {
        setSelectCategory(getCateKey(configModule.categorySelectMode))

        with(binding) {
            boardRv.adapter = adapter
            categoryList.text = configModule.categorySelectMode
            editButton click {
                move(this@BoardListActivity, RouterEvent(type = Landing.BOARD_WRITE_UPDATE, activityResult = activityForResult))
            }
            categoryList click {
                setScrollerPicker(
                    ViewTypeConst.SCROLLER_CATEGORY,
                    categoryList.text.toString()
                )
            }
        }
    }

    fun observe() {
        with(viewModel) {
            observe(boardList, ::renderBoardList)
        }
    }

    private fun getCateKey(name: String?): String {
        return when (name) {
            CategoryUtil.RECRUIT_CATEGORY.value -> CategoryUtil.RECRUIT_CATEGORY.key
            else -> CategoryUtil.FREE_CATEGORY.key
        }
    }

    private fun setScrollerPicker(
        type: ViewTypeConst,
        selectItem: String
    ) {
        ScrollSelectorBottomSheet.newInstance(type, selectItem) {
            binding.categoryList.text = it
            setSelectCategory(getCateKey(it))
            configModule.categorySelectMode = it
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
