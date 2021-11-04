package com.example.playergroup.ui.vote.voteCreate

import com.example.playergroup.data.repository.DataRepository
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoteCreateViewModel : BaseViewModel() {
    private val dataRepository: DataRepository = DataRepository()

    fun insertVote(voteModel: VoteModel) {
        CoroutineScope(Dispatchers.IO).launch {
            dataRepository.insertVoteItem(voteModel)
        }
    }
}