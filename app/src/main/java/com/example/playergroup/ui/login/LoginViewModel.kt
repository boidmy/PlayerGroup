package com.example.playergroup.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.getFirebaseExceptionCodeToString
import com.google.firebase.auth.FirebaseAuth

typealias LoadingProgress = ((Boolean) -> Unit)
typealias GoogleLogin = (() -> Unit)
typealias NavigationViewDismiss = (() -> Unit)
typealias PagerMoveCallback = ((Int) -> Unit)
class LoginViewModel: BaseViewModel() {
    private val authRepository by lazy { AuthRepository() }

    private val _firebaseResult: MutableLiveData<Boolean> = MutableLiveData()
    val firebaseResult: LiveData<Boolean>
        get() = _firebaseResult

    private val _firebaseError: MutableLiveData<String> = MutableLiveData()
    val firebaseError: LiveData<String>
        get() = _firebaseError

    private val _firebaseJoinResult: MutableLiveData<Boolean> = MutableLiveData()
    val firebaseJoinResult: LiveData<Boolean>
        get() = _firebaseJoinResult

    var loadingProgress: LoadingProgress? = null
    var googleLogin: GoogleLogin? = null
    var dismiss: NavigationViewDismiss? = null
    var pagerMoveCallback: PagerMoveCallback? = null

    fun firebaseAuthWithGoogle(idToken: String) {
        authRepository.googleRegister(idToken) { firebaseUser ->
            firebaseUser?.let {
                val userEmail = it.email
                if (userEmail.isNullOrEmpty()) {
                    _firebaseResult.value = false
                } else {
                    authRepository.isUserInfoEmpty { isSuccessful ->
                        if (isSuccessful) { // 신규 사용자
                            authRepository.insertUserDocument {
                                _firebaseResult.value = it
                            }
                        } else { // 기존 사용자
                            _firebaseResult.value = true
                        }
                    }
                }
            } ?: run {
                _firebaseResult.value = false
            }
        }
    }

    fun createEmailUserJoin(id: String, pw: String) {
        authRepository.createEmailUser(id, pw) { firebaseResultCallback ->
            if (firebaseResultCallback.isSuccess) {
                authRepository.sendEmailVerification { isEmailVerificationSuccessful ->
                    if (isEmailVerificationSuccessful) {
                        // 아직 메일인증 되지 않은 상태라고 판단 하고 로그아웃 처리 해버린다.
                        FirebaseAuth.getInstance().signOut()
                        _firebaseJoinResult.value = isEmailVerificationSuccessful
                    } else {
                        val message = getFirebaseExceptionCodeToString("")
                        _firebaseError.value = message
                    }
                }
            } else {
                val message = getFirebaseExceptionCodeToString(firebaseResultCallback.errorCode ?: "")
                _firebaseError.value = message
            }
        }
    }

    fun signInEmailLogin(id: String, pw: String) {
        authRepository.signInEmailLogin(id, pw) { firebaseResultCallback ->
            if (firebaseResultCallback.isSuccess) {
                authRepository.isUserInfoEmpty { isSuccessful ->
                    if (isSuccessful) { // 신규 사용자
                        authRepository.insertUserDocument {
                            _firebaseResult.value = it
                        }
                    } else { // 기존 사용자
                        _firebaseResult.value = true
                    }
                }
            } else {
                val message = getFirebaseExceptionCodeToString(firebaseResultCallback.errorCode ?: "")
                _firebaseError.value = message
            }
        }
    }
}

enum class LoginType(val value: Int) {
    JOIN(0),
    LOGIN(1),
    LOGIN_INFO_LOST(2)
}