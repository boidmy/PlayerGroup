package com.example.playergroup.ui.board.boardDetail

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.playergroup.R
import com.example.playergroup.data.*
import com.example.playergroup.databinding.ActivityBoardDetailBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.board.BoardViewModel
import com.example.playergroup.ui.board.boardDetail.list.BoardDetailReviewAdapter
import com.example.playergroup.util.*
import com.example.playergroup.util.LandingRouter.move

class BoardDetailActivity : BaseActivity<ActivityBoardDetailBinding>() {

    private val viewModel: BoardViewModel by viewModels()
    private val boardDetailReviewAdapter = BoardDetailReviewAdapter()

    private val activityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            (it.data?.getSerializableExtra(INTENT_SERIALIZABLE) as NoticeBoardItem).run {
                binding.boardTitle.text = title
                binding.boardSub.text = sub
            }
        }
    }

    override fun getViewBinding() = ActivityBoardDetailBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initView()
        observe()
    }

    private fun initView() {
        intent.getBundleExtra(INTENT_BUNDLE)?.let {
            (it.getString(INTENT_EXTRA_KEY_FIRST) to it.getString(INTENT_EXTRA_KEY_SECOND)).two { cateKey, itemKey ->
                viewModel.setSelectCategory(cateKey)
                viewModel.setSelectDetail(itemKey)
                viewModel.getBoardDetail()
                viewModel.getBoardDetailReview()
            }
        }

        with(binding) {
            boardReview.adapter = boardDetailReviewAdapter
            (ResourcesCompat.getDrawable(
                reviewSend.resources,
                R.drawable.edge_round_send,
                null
            ) as GradientDrawable).run {
                reviewSend.initTextWatcher(this)
                boardReviewEdit.addTextChangedListener(reviewSend.textWatcher(this))
            }
            reviewSend click {
                insertReview()
            }
        }
    }

    private fun itemUpdate(item: NoticeBoardItem) {
        item.categoryKey = viewModel.selectCategory ?: ""
        Bundle().apply {
            putSerializable(INTENT_SERIALIZABLE, item)
        }.run {
            move(
                this@BoardDetailActivity,
                RouterEvent(type = Landing.BOARD_WRITE_UPDATE, bundle = this, activityResult = activityForResult)
            )
        }
    }

    private fun observe() {
        with(viewModel) {
            observe(boardItem, ::renderBoardDetail)
            observe(boardReview, ::renderBoardDetailReview)
        }
    }

    private fun renderBoardDetail(item: NoticeBoardItem?) {
        with(binding) {
            item?.let {
                boardTitle.text = it.title
                boardSub.text = it.sub
                boardEditTime.text = CalendarUtil.getDateFormat(it.time)
                boardUpdate.isVisible = pgApplication.userInfo?.email ?: "" == item.email
                boardUpdate click {
                    itemUpdate(item)
                }
            }
        }
    }

    private fun renderBoardDetailReview(item: MutableList<NoticeBoardItem>?) {
        item?.let {
            boardDetailReviewAdapter.items = it
        }
    }

    private fun insertReview() {
        viewModel.insertReview(binding.boardReviewEdit.text.toString())
    }
}