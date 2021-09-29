package com.example.playergroup.api

import com.example.playergroup.data.NoticeBoardCategory
import com.example.playergroup.data.NoticeBoardItem
import com.example.playergroup.data.NoticeBoardList
import com.example.playergroup.ui.base.BaseRepository

class NoticeBoardRepository: BaseRepository() {

    /**
     * 게시판 카테고리 가져오기
     */
    fun getCategoryList(callback: (MutableList<NoticeBoardCategory>?) -> Unit) {
        firebaseNoticeBoard.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = it.result?.toObjects(NoticeBoardCategory::class.java)
                callback.invoke(list)
            } else {
                callback.invoke(null)
            }
        }
    }

    /**
     * 게시판 리스트 가져오기
     */
    fun getBoardList(key: String, callback: (MutableList<NoticeBoardItem>?) -> Unit) {
        firebaseNoticeBoard.document(key).collection(
                "board").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = it.result?.toObjects(NoticeBoardItem::class.java)
                callback(list)
            }
        }
    }

}