package com.example.playergroup.ui.base

import androidx.lifecycle.ViewModel
import com.example.playergroup.api.AuthRepository
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel: ViewModel() {

    protected val compositeDisposable by lazy { CompositeDisposable() }
    protected val authRepository by lazy { AuthRepository() }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}