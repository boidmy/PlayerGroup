package com.example.playergroup.api

import com.example.playergroup.data.NoticeBoardCategory
import com.example.playergroup.data.NoticeBoardItem
import com.example.playergroup.ui.base.BaseRepository
import com.example.playergroup.util.CalendarUtil
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query

class NoticeBoardRepository : BaseRepository() {

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
            "board"
        ).orderBy("time", Query.Direction.DESCENDING).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = it.result?.toObjects(NoticeBoardItem::class.java)
                callback(list)
            }
        }
    }

    /**
     * 게시판 글 등록
     */
    fun insertBoard(
        item: NoticeBoardItem,
        callback: (NoticeBoardItem) -> Unit
    ) {
        val collection = firebaseNoticeBoard.document(item.key).collection("board")
        val boardKey = collection.document().id //데이터를 저장하기전에 미리 난수인 key를 뽑아내서 저장한다
        item.apply {
            time = CalendarUtil.getDate()
            key = boardKey
            //timestamp = FieldValue.serverTimestamp().toString()
        }.run {
            collection.document(boardKey).set(this).addOnCompleteListener {
                callback(this)
            }
        }
    }

    fun updateBoard(item: NoticeBoardItem, callback: (NoticeBoardItem) -> Unit) {
        firebaseNoticeBoard.document(item.categoryKey).collection("board")
            .document(item.key).update(
                mapOf(
                    "title" to item.title,
                    "sub" to item.sub
                )
            ).addOnCompleteListener {
                callback(item)
            }
    }

    /**
     * 게시판 댓글 등록
     */
    fun insertReview(
        cateKey: String,
        detailKey: String,
        edit: String,
        id: String = "",
        callback: (NoticeBoardItem) -> Unit
    ) {
        val collection = firebaseNoticeBoard.document(cateKey).collection("board")
            .document(detailKey).collection("review")
        val boardKey = collection.document().id //데이터를 저장하기전에 미리 난수인 key를 뽑아내서 저장한다
        NoticeBoardItem(
            sub = edit,
            email = id,
            key = boardKey,
            time = CalendarUtil.getDate()
        ).run {
            collection.document(boardKey).set(this).addOnCompleteListener {
                callback(this)
            }
        }
    }

    /**
     * 게시판 글 상세
     */
    fun getBoardDetail(cateKey: String, detailKey: String, callback: (NoticeBoardItem?) -> Unit) {
        firebaseNoticeBoard.document(cateKey).collection("board")
            .document(detailKey).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val item = it.result?.toObject(NoticeBoardItem::class.java)
                    callback(item)
                }
            }
    }

    /**
     * 게시판 상세 댓글
     */
    fun getBoardDetailReview(
        cateKey: String,
        detailKey: String,
        callback: (MutableList<NoticeBoardItem>?) -> Unit
    ) {
        firebaseNoticeBoard.document(cateKey).collection("board")
            .document(detailKey).collection("review").orderBy("time", Query.Direction.DESCENDING)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val item = it.result?.toObjects(NoticeBoardItem::class.java)
                    callback(item)
                }
            }
    }
}