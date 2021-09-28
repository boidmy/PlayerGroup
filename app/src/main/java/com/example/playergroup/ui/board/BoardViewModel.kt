package com.example.playergroup.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.NoticeBoard
import com.example.playergroup.ui.base.BaseRepository
import com.example.playergroup.ui.base.BaseViewModel
import javax.inject.Inject

class BoardViewModel @Inject constructor() : BaseViewModel() {

    private val _firebaseNoticeCategoryList: MutableLiveData<MutableList<NoticeBoard>> = MutableLiveData()
    val firebaseNoticeCategoryList: LiveData<MutableList<NoticeBoard>>
        get() = _firebaseNoticeCategoryList

    fun getCategoryList() {
        noticeRepository.getCategoryList {
            _firebaseNoticeCategoryList.value = it
        }
    }
}