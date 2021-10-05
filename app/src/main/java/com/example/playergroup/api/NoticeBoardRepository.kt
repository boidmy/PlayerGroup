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

    /**
     * 게시판 글 등록
     */
    fun insertBoard(key: String, title: String, sub: String, id: String, callback: (Boolean) -> Unit) {
        val collection = firebaseNoticeBoard.document(key).collection("board")
        val boardKey  = collection.document().id //데이터를 저장하기전에 미리 난수인 key를 뽑아내서 저장한다
        val boardData = NoticeBoardItem(title, sub, id, boardKey)
        collection.document(boardKey).set(boardData).addOnCompleteListener {
            callback(it.isSuccessful)
        }
    }
}