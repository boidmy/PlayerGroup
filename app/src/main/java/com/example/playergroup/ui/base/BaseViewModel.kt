package com.example.playergroup.ui.base

import androidx.lifecycle.ViewModel
import com.example.playergroup.api.AuthRepository
import com.example.playergroup.api.ClubRepository
import com.example.playergroup.api.InitRepository
import com.example.playergroup.api.NoticeBoardRepository
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel: ViewModel() {

    protected val compositeDisposable by lazy { CompositeDisposable() }
    protected val authRepository by lazy { AuthRepository() }
    protected val clubRepository by lazy { ClubRepository() }
    protected val initRepository by lazy { InitRepository() }
    protected val noticeRepository by lazy { NoticeBoardRepository() }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}