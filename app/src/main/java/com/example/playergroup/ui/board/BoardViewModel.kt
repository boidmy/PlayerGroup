package com.example.playergroup.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.NoticeBoardItem
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.two

class BoardViewModel : BaseViewModel() {

    private val _writeComplete: MutableLiveData<NoticeBoardItem> = MutableLiveData()
    private var _boardList: MutableLiveData<MutableList<NoticeBoardItem>> = MutableLiveData()
    private var _boardItem: MutableLiveData<NoticeBoardItem> = MutableLiveData()
    private val _boardReview: MutableLiveData<MutableList<NoticeBoardItem>> = MutableLiveData()
    private var _selectCategory: String? = null
    private var _selectDetail: String? = null

    val writeComplete: LiveData<NoticeBoardItem>
        get() = _writeComplete
    val boardList: LiveData<MutableList<NoticeBoardItem>>
        get() = _boardList
    val boardItem: LiveData<NoticeBoardItem>
        get() = _boardItem
    val boardReview: LiveData<MutableList<NoticeBoardItem>>
        get() = _boardReview
    val selectCategory: String?
        get() = _selectCategory
    val selectDetail: String?
        get() = _selectDetail

    fun addBoardList(item: NoticeBoardItem) {
        _boardList.value?.apply {
            add(0, item)
        }.run {
            _boardList.value = this
        }
    }

    fun insertBoard(item: NoticeBoardItem) {
        noticeRepository.insertBoard(item) {
            _writeComplete.value = it
        }
    }

    fun updateBoard(item: NoticeBoardItem) {
        noticeRepository.updateBoard(item) {
            _writeComplete.value = it
        }
    }

    fun getBoardList() {
        _selectCategory?.let {
            noticeRepository.getBoardList(it) { item ->
                _boardList.value = item
            }
        }
    }

    fun setSelectCategory(key: String) {
        _selectCategory = key
    }

    fun setSelectDetail(key: String) {
        _selectDetail = key
    }

    fun getBoardDetail() {
        (_selectCategory to _selectDetail).two { s, s2 ->
            noticeRepository.getBoardDetail(s, s2) {
                _boardItem.value = it
            }
        }
    }

    fun getBoardDetailReview() {
        (_selectCategory to _selectDetail).two { s, s2 ->
            noticeRepository.getBoardDetailReview(s, s2) {
                _boardReview.value = it
            }
        }
    }

    fun insertReview(edit: String, id: String = "") {
        (_selectCategory to _selectDetail).two { s, s2 ->
            noticeRepository.insertReview(
                cateKey = s,
                detailKey = s2,
                edit = edit,
                id = id
            ) {
                _boardReview.value?.apply {
                    add(0, it)
                }.run {
                    _boardReview.value = this
                }
            }
        }
    }
}