package com.example.playergroup.ui.board.boardCreate

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.playergroup.data.INTENT_BUNDLE
import com.example.playergroup.data.INTENT_SERIALIZABLE
import com.example.playergroup.data.NoticeBoardItem
import com.example.playergroup.databinding.ActivityBoardCreateBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.board.BoardViewModel
import com.example.playergroup.ui.board.boardList.BoardListActivity
import com.example.playergroup.ui.dialog.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.util.CategoryUtil
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.click

class BoardCreateActivity : BaseActivity<ActivityBoardCreateBinding>() {

    private val viewModel: BoardViewModel by viewModels()

    override fun getViewBinding() = ActivityBoardCreateBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initView()
        observe()
    }

    fun initView() {
        if (intent.getBundleExtra(INTENT_BUNDLE)?.getSerializable(INTENT_SERIALIZABLE) != null) {
            updateBoardView()
        } else {
            insertBoardView()
        }
    }

    fun observe() {
        viewModel.writeComplete.observe(this, {
            Intent(this, BoardListActivity::class.java).apply {
                putExtra(INTENT_SERIALIZABLE, it)
            }.run {
                setResult(RESULT_OK, this)
                finish()
            }
        })
    }

    private fun setScrollerPicker(
        type: ViewTypeConst,
        selectItem: String
    ) {
        ScrollSelectorBottomSheet.newInstance(type, selectItem) {
            binding.categoryListText.setText(it)
            setSelectCategory(getCateKey(it))
        }.run {
            if (isVisible) return
            show(supportFragmentManager, tag)
        }
    }

    private fun setSelectCategory(key: String) {
        viewModel.setSelectCategory(key)
    }

    private fun getCateKey(name: String?): String {
        return when (name) {
            CategoryUtil.RECRUIT_CATEGORY.value -> CategoryUtil.RECRUIT_CATEGORY.key
            else -> CategoryUtil.FREE_CATEGORY.key
        }
    }

    //일반적인 글등록
    private fun insertBoardView() {
        setSelectCategory(getCateKey(configModule.categorySelectMode))
        with(binding) {
            categoryListText.setText(configModule.categorySelectMode)
            categoryListText click {
                setScrollerPicker(
                    ViewTypeConst.SCROLLER_CATEGORY,
                    categoryListText.text.toString()
                )
            }

            btnNoticeBoard click {
                viewModel.selectCategory?.let { key ->
                    viewModel.insertBoard(NoticeBoardItem(
                        key = key,
                        title = boardEditTitle.text.toString(),
                        sub = boardEditSub.text.toString(),
                        email = pgApplication.userInfo?.email ?: "",
                        name = pgApplication.userInfo?.name ?: ""
                    ))
                }
            }
        }
    }

    //내가작성한글 수정
    private fun updateBoardView() {
        intent.getBundleExtra(INTENT_BUNDLE)?.let {
            val item: NoticeBoardItem = it.getSerializable(INTENT_SERIALIZABLE) as NoticeBoardItem
            with(binding) {
                categoryList.isVisible = false
                boardEditTitle.setText(item.title)
                boardEditSub.setText(item.sub)
                btnNoticeBoard click {
                    item.title = boardEditTitle.text.toString()
                    item.sub = boardEditSub.text.toString()
                    viewModel.updateBoard(item)
                }
            }
        }
    }
}