package com.example.playergroup.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.ui.base.BaseViewModel
import com.google.firebase.auth.FirebaseUser

typealias LoadingProgress = ((Boolean) -> Unit)
class LoginViewModel: BaseViewModel() {
    private val authRepository by lazy { AuthRepository() }

    private val _firebaseResult: MutableLiveData<Boolean> = MutableLiveData()
    val firebaseResult: LiveData<Boolean>
        get() = _firebaseResult

    var loadingProgress: LoadingProgress? = null

    fun firebaseAuthWithGoogle(idToken: String) {
        authRepository.googleRegister(idToken) { firebaseUser ->
            firebaseUser?.let {
                val userEmail = it.email
                if (userEmail.isNullOrEmpty()) {
                    _firebaseResult.value = false
                } else {
                    authRepository.searchUserDocument(userEmail) { isSuccessful ->
                        if (isSuccessful) { // 기존 사용자
                            _firebaseResult.value = true
                        } else {    // 신규 사용자
                            authRepository.insertUserDocument(firebaseUser) {
                                _firebaseResult.value = it
                            }
                        }
                    }
                }
            } ?: run {
                _firebaseResult.value = false
            }
        }
    }

    fun searchUserDocument(user: FirebaseUser) {
        val userEmail = user.email
        if (userEmail.isNullOrEmpty()) {
            _firebaseResult.value = false
        } else {
            authRepository.searchUserDocument(userEmail) {

            }
        }
    }

    fun insertUserDocument(user: FirebaseUser) {
        authRepository.insertUserDocument(user) {

        }
    }
}

enum class LoginType(
    val value: Int
) {
    JOIN(0),
    LOGIN(1),
    LOGIN_INFO_LOST(2)
}