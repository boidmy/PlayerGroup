package com.example.playergroup.ui.vote

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playergroup.data.room.VoteDb
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoteViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val _voteList: MutableLiveData<List<VoteModel>> = MutableLiveData()
    val voteList: LiveData<List<VoteModel>>
        get() = _voteList

    private val voteDb = Room.databaseBuilder(context, VoteDb::class.java, "vote.db")
        .addCallback(object : RoomDatabase.Callback() {}).build()

    fun selectVote() {
        CoroutineScope(Dispatchers.IO).launch {
            _voteList.postValue(voteDb.voteDao().getVote())
        }
    }
}