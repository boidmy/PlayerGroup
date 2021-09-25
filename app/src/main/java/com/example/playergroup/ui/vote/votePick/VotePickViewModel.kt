package com.example.playergroup.ui.vote.votePick

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playergroup.data.room.VoteDb
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.data.room.VoteReview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VotePickViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val voteDb = Room.databaseBuilder(context, VoteDb::class.java, "vote.db")
        .addCallback(object : RoomDatabase.Callback() {}).build()

    private val _voteItem: MutableLiveData<VoteModel> = MutableLiveData()
    val voteItem: LiveData<VoteModel>
        get() = _voteItem

    fun selectVote(key: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _voteItem.postValue(voteDb.voteDao().selectVote(key))
        }
    }

    fun updateVote(item: VoteModel, key: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val voteModel = voteDb.voteDao().selectVote(key) //리스트 우선 조회 후 카운트 시켜주기 위함 (투표 중간에 다른사람이 먼저 투표할 가능성)
            for ((index, voteItem) in item.voteData.withIndex()) {
                if (voteItem.checked) {
                    voteModel.voteData.getOrNull(index)?.let {
                        it.count++
                    }
                }
            }
            voteDb.voteDao().update(voteModel)
            _voteItem.postValue(voteModel)
        }
    }

    fun updateReview(review: String, key: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val voteModel = voteDb.voteDao().selectVote(key) //리스트 우선 조회 후 댓글 add
            val reviewList = ArrayList<VoteReview>(voteModel.review)
            reviewList.add(VoteReview(name = "고릴라", review = review))
            voteModel.review = reviewList
            voteDb.voteDao().update(voteModel)
            _voteItem.postValue(voteModel)
        }
    }
}