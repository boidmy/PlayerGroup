package com.example.playergroup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.CompositeDisposable

open class BaseFragment: Fragment() {

    protected val compositeDisposable = CompositeDisposable()
    protected val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}