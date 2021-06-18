package com.example.playergroup.ui.base

import androidx.lifecycle.ViewModel
import com.example.playergroup.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel: ViewModel() {

    protected val compositeDisposable by lazy { CompositeDisposable() }
    protected val authRepository by lazy { AuthRepository() }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}