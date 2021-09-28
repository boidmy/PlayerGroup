package com.example.playergroup.api

import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.NoticeBoard
import com.example.playergroup.ui.base.BaseRepository

class NoticeBoardRepository: BaseRepository() {

    /**
     * 게시판 카테고리 가져오기
     */
    fun getCategoryList(callback: (MutableList<NoticeBoard>?) -> Unit) {
        firebaseNoticeBoard.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = it.result?.toObjects(NoticeBoard::class.java)
                callback.invoke(list)
            } else {
                callback.invoke(null)
            }
        }
    }

}