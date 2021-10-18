package com.example.playergroup.ui.board.boardCreate

import android.os.Bundle
import androidx.activity.viewModels
import com.example.playergroup.data.INTENT_BUNDLE
import com.example.playergroup.data.INTENT_SERIALIZABLE
import com.example.playergroup.data.NoticeBoardItem
import com.example.playergroup.databinding.ActivityBoardCreateBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.board.BoardViewModel
import com.example.playergroup.ui.dialog.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.util.CategoryUtil
import com.example.playergroup.util.click

class BoardCreateActivity : BaseActivity<ActivityBoardCreateBinding>() {

    private val viewModel: BoardViewModel by viewModels()

    override fun getViewBinding() = ActivityBoardCreateBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initView()
        observe()
    }

    fun updateBoardView() {
        intent.getBundleExtra(INTENT_BUNDLE)?.let {
            val item: NoticeBoardItem = it.getSerializable(INTENT_SERIALIZABLE) as NoticeBoardItem
            binding.boardEditTitle.setText(item.title)
            binding.boardEditSub.setText(item.sub)
        }
    }

    fun initView() {
        setSelectCategory(getCateKey(configModule.categorySelectMode))
        with(binding) {
            categoryListText.setText(configModule.categorySelectMode)
            categoryListText click {
                setScrollerPicker(
                    ScrollSelectorBottomSheet.Companion.ScrollSelectorType.CATEGORY,
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

    fun observe() {
        viewModel.writeComplete.observe(this, {
            if (it) finish() //글등록에 성공하고 activityResult로 게시판 갱신어떻게 할지 협의필요
        })
    }

    private fun setScrollerPicker(
        type: ScrollSelectorBottomSheet.Companion.ScrollSelectorType,
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
}