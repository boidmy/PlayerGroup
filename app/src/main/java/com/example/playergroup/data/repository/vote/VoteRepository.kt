package com.example.playergroup.data.repository.vote

import com.example.playergroup.data.room.VoteDb
import com.example.playergroup.data.room.VoteModel
import javax.inject.Inject

class VoteRepository @Inject constructor(private val voteDb: VoteDb){

    suspend fun insertVoteItem(voteModel: VoteModel) {
        voteDb.voteDao().insert(voteModel)
    }

    suspend fun selectVoteList(): List<VoteModel> {
        return voteDb.voteDao().getVote()
    }

    suspend fun selectVoteItem(key: Int): VoteModel {
        return voteDb.voteDao().selectVote(key)
    }

    suspend fun updateVoteItem(item: VoteModel) {
        voteDb.voteDao().update(item)
    }
}