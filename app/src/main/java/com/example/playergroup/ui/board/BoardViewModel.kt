package com.example.playergroup.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.NoticeBoardCategory
import com.example.playergroup.ui.base.BaseViewModel

class BoardViewModel  constructor() : BaseViewModel() {

    private val _firebaseNoticeCategoryList: MutableLiveData<MutableList<NoticeBoardCategory>> = MutableLiveData()
    val firebaseNoticeCategoryList: LiveData<MutableList<NoticeBoardCategory>>
        get() = _firebaseNoticeCategoryList

    fun getCategoryList() {
        noticeRepository.getCategoryList {
            _firebaseNoticeCategoryList.value = it
        }
    }
}