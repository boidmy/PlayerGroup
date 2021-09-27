package com.example.playergroup.ui.vote.voteCreate

import androidx.lifecycle.viewModelScope
import com.example.playergroup.data.repository.DataRepository
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoteCreateViewModel @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {

    fun insertVote(voteModel: VoteModel) {
        CoroutineScope(Dispatchers.IO).launch {
            dataRepository.insertVoteItem(voteModel)
        }
    }
}