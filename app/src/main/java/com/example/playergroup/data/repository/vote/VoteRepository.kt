package com.example.playergroup.data.repository.vote

import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.data.room.VoteDb
import com.example.playergroup.data.room.VoteModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class VoteRepository {

    private val voteDb: VoteDb = Room.databaseBuilder(
        PlayerGroupApplication.instance.applicationContext,
        VoteDb::class.java,
        "vote.db"
    )
        .addCallback(object : RoomDatabase.Callback() {}).build()

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