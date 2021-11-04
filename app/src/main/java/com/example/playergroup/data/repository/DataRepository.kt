package com.example.playergroup.data.repository

import com.example.playergroup.data.repository.vote.VoteRepository
import com.example.playergroup.data.room.VoteModel
import javax.inject.Inject

class DataRepository : DataRepositorySource {

    private val voteRepository: VoteRepository = VoteRepository()

    suspend fun insertVoteItem(voteModel: VoteModel) {
        voteRepository.insertVoteItem(voteModel)
    }

    suspend fun requestVoteList(): List<VoteModel> {
        return voteRepository.selectVoteList()
    }

    suspend fun requestVoteItem(key: Int): VoteModel {
        return voteRepository.selectVoteItem(key)
    }

    suspend fun updateVoteItem(voteModel: VoteModel) {
        voteRepository.updateVoteItem(voteModel)
    }

}