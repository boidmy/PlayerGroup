package com.example.playergroup.ui.vote

import androidx.lifecycle.*
import com.example.playergroup.data.repository.DataRepository
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoteViewModel @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {

    private val _voteList: MutableLiveData<List<VoteModel>> = MutableLiveData()

    val voteList: LiveData<List<VoteModel>>
        get() = _voteList

    fun selectVote() {
        viewModelScope.launch {
            _voteList.postValue(dataRepository.requestVoteList())
        }
    }
}