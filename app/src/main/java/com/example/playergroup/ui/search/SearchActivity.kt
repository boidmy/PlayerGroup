package com.example.playergroup.ui.search

import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playergroup.R
import com.example.playergroup.databinding.ActivitySearchBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.dialog.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.util.*

class SearchActivity: BaseActivity<ActivitySearchBinding>() {

    private val viewModel by viewModels<SearchViewModel>()
    private var isTwoItemMode: Boolean = false
    lateinit var currentActivityArea: String

    override fun getViewBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {

        isTwoItemMode = configModule.isTwoItemMode ?: false
        currentActivityArea = pgApplication.userInfo?.activityArea ?: ""

        initEtcView()
        initEditTextView()
        initRecyclerView()
        initViewModel()
    }

    private fun initEtcView() {

        with (binding) {
            tvSwap click {}
            moveToTop click { binding.rvSearch.scrollToPosition(0) }
            llLocation click {
                val newInstance = ScrollSelectorBottomSheet.newInstance(
                    type = ViewTypeConst.SCROLLER_ACTIVITY_AREA,
                    selectItem = currentActivityArea,
                    customTitle = "검색할 지역을 선택해 주세요."
                ) {
                    currentActivityArea = it
                    tvLocation.text = it
                    viewModel.getData(it, isTwoItemMode)
                }
                if (newInstance.isVisible) return@click
                newInstance.show(supportFragmentManager, newInstance.tag)
            }
            tvLocation.text = currentActivityArea

            ivListMode.apply {
                changeSearchIconUI(isTwoItemMode)
                click {
                    isTwoItemMode = !isTwoItemMode
                    viewModel.modeChange(isTwoItemMode)
                    configModule.isTwoItemMode = isTwoItemMode
                    changeSearchIconUI(isTwoItemMode)
                }
            }
        }
    }

    private infix fun ImageView.changeSearchIconUI(isTwoItemMode: Boolean) {
        setImageDrawable(
            ContextCompat.getDrawable(
                this@SearchActivity,
                if (isTwoItemMode) R.drawable.icon_two else R.drawable.icon_one
            )
        )
    }

    private infix fun RecyclerView.changeSearchListUI(isTwoItemMode: Boolean) {
        with(viewModel) {
            post {
                layoutManager = if (isTwoItemMode) {
                    GridLayoutManager(this@SearchActivity, 2)
                } else {
                    LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
                }
            }
        }
    }

    private fun initEditTextView() {
        binding.etsearch.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                hideKeyboard(binding.etsearch)
                val keyword = binding.etsearch.text?.trim().toString()
                if (keyword.isEmpty()) showDefDialog("검색어를 입력해주세요").show()
                else {
                    debugToast { "준비중" }
                    //todo 검색 은 어떻게 해야 할지 아직 못정함..
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun initViewModel() {
        viewModel.apply {
            viewModel.getCurrentSearchListData = { getSearchListAdapter()?.items?.map { it.copy() }?.toMutableList() }
            firebaseClubListData.observe(this@SearchActivity, Observer {
                getSearchListAdapter()?.submitList(it.first, it.second)
                val isEmpty = it.first?.indexOfFirst { it.viewType == ViewTypeConst.EMPTY_ERROR } == 0
                val count = if (isEmpty) 0 else (it.first?.size ?: 0)
                binding.tvCount.text = "$count 개"
            })
            getData(currentActivityArea, isTwoItemMode)
        }
    }

    private fun getSearchListAdapter() = binding.rvSearch.adapter as? SearchListAdapter

    private fun initRecyclerView() {
        binding.rvSearch.apply {
            layoutManager = if (isTwoItemMode) {
                GridLayoutManager(this@SearchActivity, 2)
            } else {
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            }
            adapter = SearchListAdapter().apply {
                registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        scrollToPosition(0)
                        showMoveTopBtn(false)
                    }
                })
            }
            if (itemDecorationCount == 0) addItemDecoration(object: RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)

                    val itemCount = state.itemCount
                    var itemPosition = parent.getChildAdapterPosition(view)
                    if (itemPosition == RecyclerView.NO_POSITION) { itemPosition = parent.getChildLayoutPosition(view) }

                    when (parent.adapter?.getItemViewType(itemPosition)) {
                        ViewTypeConst.SEARCH_ONE_TYPE.ordinal -> {
                            val firstItemMargin = 10.toPx
                            val lastItemMargin = 10.toPx
                            val itemMargin = 10.toPx
                            val leftRightMargin = 10.toPx

                            if (itemPosition == 0) {    // 첫 번째 아이템
                                outRect.set(leftRightMargin, firstItemMargin, leftRightMargin, (itemMargin / 2))
                            } else if (itemCount > 0 && itemPosition == itemCount - 1) {  // 마지막 아이템
                                outRect.set(leftRightMargin, (itemMargin / 2), leftRightMargin, lastItemMargin)
                            } else {  // 나머지
                                outRect.set(leftRightMargin, (itemMargin / 2), leftRightMargin, (itemMargin / 2))
                            }
                        }
                        ViewTypeConst.SEARCH_TWO_TYPE.ordinal -> {
                            val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 2    // spanCount
                            val column: Int = itemPosition % spanCount // item column
                            val spacing = 10.toPx

                            outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                            outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                            if (itemPosition < spanCount) { // top edge
                                outRect.top = spacing
                            }
                            outRect.bottom = spacing // item bottom
                        }
                    }
                }
            })

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val itemIdx =
                        (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()
                    showMoveTopBtn(!(itemIdx == 0 || itemIdx == null || adapter?.itemCount == 0))
                }
            })
            setOnTouchListener { v, event ->
                hideKeyboard(binding.etsearch)
                false
            }

            itemAnimator = setItemAnimatorDuration(150L)
        }
    }
    private fun showMoveTopBtn(isShow: Boolean) { if (isShow) binding.moveToTop.show() else binding.moveToTop.hide() }
}