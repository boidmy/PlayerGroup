package com.example.playergroup.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playergroup.data.room.dao.VoteDao

@Database(entities = [VoteModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class VoteDb: RoomDatabase() {
    abstract fun voteDao(): VoteDao
}