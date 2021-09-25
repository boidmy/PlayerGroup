package com.example.playergroup.ui.vote.voteCreate

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playergroup.data.room.VoteDb
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoteCreateViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val voteDb = Room.databaseBuilder(context, VoteDb::class.java, "vote.db")
        .addCallback(object : RoomDatabase.Callback() {}).build()

    fun insertVote(data: VoteModel) {
        CoroutineScope(Dispatchers.IO).launch {
            voteDb.voteDao().insert(data)
        }
    }

    fun selectVote(): List<VoteModel> {
        return voteDb.voteDao().getVote()
    }
}