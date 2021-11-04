package com.example.playergroup.ui.vote.votePick

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playergroup.data.repository.DataRepository
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.data.room.VoteReview
import com.example.playergroup.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class VotePickViewModel : BaseViewModel() {
    private val dataRepository: DataRepository = DataRepository()

    private val _voteItem: MutableLiveData<VoteModel> = MutableLiveData()
    val voteItem: LiveData<VoteModel>
        get() = _voteItem

    fun selectVote(key: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _voteItem.postValue(dataRepository.requestVoteItem(key))
        }
    }

    fun updateVote(item: VoteModel, key: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val voteModel = dataRepository.requestVoteItem(key) //리스트 우선 조회 후 카운트 시켜주기 위함 (투표 중간에 다른사람이 먼저 투표할 가능성)
            for ((index, voteItem) in item.voteData.withIndex()) {
                if (voteItem.checked) {
                    voteModel.voteData.getOrNull(index)?.let {
                        it.count++
                    }
                }
            }
            dataRepository.updateVoteItem(voteModel)
            _voteItem.postValue(voteModel)
        }
    }

    fun updateReview(review: String, key: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val voteModel = dataRepository.requestVoteItem(key) //리스트 우선 조회 후 댓글 add
            val reviewList = ArrayList<VoteReview>(voteModel.review)
            reviewList.add(VoteReview(name = "고릴라", review = review))
            voteModel.review = reviewList
            dataRepository.updateVoteItem(voteModel)
            _voteItem.postValue(voteModel)
        }
    }
}