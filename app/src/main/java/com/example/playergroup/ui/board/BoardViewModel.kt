package com.example.playergroup.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.NoticeBoardItem
import com.example.playergroup.ui.base.BaseViewModel

class BoardViewModel : BaseViewModel() {

    private val _writeComplete: MutableLiveData<Boolean> = MutableLiveData()
    private var _boardList: MutableLiveData<MutableList<NoticeBoardItem>> = MutableLiveData()
    var selectCategory: String? = null

    val writeComplete: LiveData<Boolean>
        get() = _writeComplete
    val boardList: MutableLiveData<MutableList<NoticeBoardItem>>
        get() = _boardList

    fun addBoardItem(key: String, title: String, sub: String, id: String) {
        noticeRepository.insertBoard(key = key, title = title, sub = sub, id = id) {
            _writeComplete.value = it
        }
    }

    fun getBoardList(key: String) {
        noticeRepository.getBoardList(key) {
            _boardList.value = it
        }
    }
}