package com.example.playergroup.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.R
import com.example.playergroup.data.Landing
import com.example.playergroup.data.LoginResultCallback
import com.example.playergroup.data.repository.AuthRepository
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.*
import com.google.firebase.auth.FirebaseAuth

typealias LoadingProgress = ((Boolean) -> Unit)
typealias GoogleLogin = (() -> Unit)
typealias NavigationViewDismiss = (() -> Unit)
typealias PagerMoveCallback = ((Int) -> Unit)
class LoginViewModel: BaseViewModel() {
    private val authRepository by lazy { AuthRepository() }

    private val _firebaseResult: MutableLiveData<LoginResultCallback> = MutableLiveData()
    val firebaseResult: LiveData<LoginResultCallback>
        get() = _firebaseResult

    private val _firebaseError: MutableLiveData<Any> = MutableLiveData()
    val firebaseError: LiveData<Any>
        get() = _firebaseError

    private val _firebaseJoinResult: MutableLiveData<Boolean> = MutableLiveData()
    val firebaseJoinResult: LiveData<Boolean>
        get() = _firebaseJoinResult

    private val _firebaseUserPasswordResult: MutableLiveData<Boolean> = MutableLiveData()
    val firebaseUserPasswordResult: LiveData<Boolean>
        get() = _firebaseUserPasswordResult

    var loadingProgress: LoadingProgress? = null
    var googleLogin: GoogleLogin? = null
    var dismiss: NavigationViewDismiss? = null
    var pagerMoveCallback: PagerMoveCallback? = null

    fun firebaseAuthWithGoogle(idToken: String) {
        authRepository.googleRegister(idToken) { firebaseUser ->
            firebaseUser?.let {
                val userEmail = it.email
                if (userEmail.isNullOrEmpty()) {
                    _firebaseResult.value = LoginResultCallback(false)
                } else {
                    authRepository.isUserInfoEmpty { isSuccessful ->
                        if (isSuccessful) { // 신규 사용자
                            authRepository.insertUserDocument {
                                _firebaseResult.value = LoginResultCallback(it, Landing.MY_PAGE)
                            }
                        } else { // 기존 사용자
                            _firebaseResult.value = LoginResultCallback(true)
                        }
                    }
                }
            } ?: run {
                _firebaseResult.value = LoginResultCallback(false)
            }
        }
    }

    fun createEmailUserJoin(id: String, pw: String) {
        if (isEditTextEmpty(id, pw)) {
            _firebaseError.value = R.string.input_empty_error
        } else if (!isEmailPattern(id)) {
            _firebaseError.value = R.string.email_error_info
        } else if (!isPWDPattern(pw)) {
            _firebaseError.value = R.string.input_pw_error
        } else {
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
    }

    fun signInEmailLogin(id: String, pw: String) {
        if (isEditTextEmpty(id, pw)) {
            _firebaseError.value = R.string.input_empty_error
        } else if (!isEmailPattern(id)) {
            _firebaseError.value = R.string.email_error_info
        } else {
            authRepository.signInEmailLogin(id, pw) { firebaseResultCallback ->
                if (firebaseResultCallback.isSuccess) {
                    authRepository.isUserInfoEmpty { isSuccessful ->
                        if (isSuccessful) { // 신규 사용자
                            authRepository.insertUserDocument {
                                _firebaseResult.value = LoginResultCallback(it, Landing.MY_PAGE)
                            }
                        } else { // 기존 사용자
                            _firebaseResult.value = LoginResultCallback(true)
                        }
                    }
                } else {
                    val message = getFirebaseExceptionCodeToString(firebaseResultCallback.errorCode ?: "")
                    _firebaseError.value = message
                }
            }
        }
    }

    fun searchUserPassword(id: String) {
        if (isEditTextEmpty(id)) {
            _firebaseError.value = R.string.input_search_email_empty_error
        } else if (!isEmailPattern(id)) {
            _firebaseError.value = R.string.email_error_info
        } else {
            authRepository.searchUserPassword(id) {
                _firebaseUserPasswordResult.value = it
            }
        }
    }
}

enum class LoginType(val value: Int) {
    JOIN(0),
    LOGIN(1),
    LOGIN_INFO_LOST(2)
}