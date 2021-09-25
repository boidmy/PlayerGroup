package com.example.playergroup.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.playergroup.data.room.VoteModel

@Dao
abstract class VoteDao : BaseDao<VoteModel> {
    @Query("SELECT * FROM vote")
    abstract fun getVote(): List<VoteModel>

    @Query("SELECT * FROM vote WHERE sequence = :seq")
    abstract fun selectVote(seq: Int): VoteModel

}