package com.example.playergroup.data.repository

import android.net.Uri
import android.util.Log
import com.example.playergroup.data.FirebaseResultCallback
import com.example.playergroup.data.UserInfo
import com.example.playergroup.ui.base.BaseRepository
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest

class AuthRepository: BaseRepository() {

    fun googleRegister(idToken: String, callback: ((FirebaseUser?) -> Unit)) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.invoke(it.result?.user)
                } else {
                    callback.invoke(null)
                }
            }
    }

    /**
     * 회원가입 하면 기본으로 저장하는 User 정보
     */
    fun insertUserDocument(callback: (Boolean) -> Unit) {
        val data = firebaseAuth.currentUser
        val userEmail = data?.email
        if (userEmail.isNullOrEmpty()) {
            callback.invoke(false)
            return
        }

        val user = hashMapOf(
            "email" to userEmail.toString()
        )

        firebaseUser.document(userEmail).set(user)
            .addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    fun deleteUserDocument(callback: (Boolean) -> Unit) {
        val data = firebaseAuth.currentUser
        val userEmail = data?.email
        if (userEmail.isNullOrEmpty()) {
            callback.invoke(false)
            return
        }
        firebaseUser.document(userEmail).delete()
            .addOnCompleteListener { callback.invoke(it.isSuccessful) }
    }

    /**
     * 프로필 정보 저장
     */
    fun insertUserDocument(userInfo: UserInfo, callback: (Boolean) -> Unit) {
        val data = firebaseAuth.currentUser
        val userEmail = data?.email
        if (userEmail.isNullOrEmpty()) {
            callback.invoke(false)
            return
        }
        firebaseUser.document(userEmail).set(userInfo).addOnCompleteListener {
            callback.invoke(it.isSuccessful)
        }
    }

    fun isUserInfoEmpty(callback: (Boolean) -> Unit) {
        val data = firebaseAuth.currentUser
        val userEmail = data?.email
        if (userEmail.isNullOrEmpty()) {
            callback.invoke(false)
            return
        }
        firebaseUser.document(userEmail).get()
            .addOnCompleteListener {
                val userInfo = (it.result?.toObject(UserInfo::class.java))
                val isEmpty = userInfo?.isEmptyData() ?: true
                callback.invoke(isEmpty)
            }
    }

    /**
     * 내 프로필 정보 가져오기
     */

    fun getUserProfileData(email: String?, callback: (UserInfo?) -> Unit) {
        if (email.isNullOrEmpty()) {
            callback.invoke(null)
            return
        }
        firebaseUser.document(email).get()
            .addOnCompleteListener {
                val userInfo = (it.result?.toObject(UserInfo::class.java))
                callback.invoke(userInfo)
            }
    }

    /**
     * createEmail 오류코드 확인하여 적용 필요
     * https://firebase.google.com/docs/auth/admin/errors?hl=ko
     */
    fun createEmailUser(id: String, pw: String, callback: (FirebaseResultCallback) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(id, pw)
            .addOnCompleteListener {
                callback.invoke(FirebaseResultCallback(it.isSuccessful, (it.exception as? FirebaseAuthException)?.errorCode ?: ""))
            }
    }

    fun sendEmailVerification(callback: (Boolean) -> Unit) {
        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    fun signInEmailLogin(id: String, pw: String, callback: (FirebaseResultCallback) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(id, pw)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (firebaseAuth.currentUser?.isEmailVerified == false) {
                        firebaseAuth.signOut()
                        callback.invoke(FirebaseResultCallback(false, "ERROR_EMAIL_NOT_VERIFICATION"))
                    } else {
                        callback.invoke(FirebaseResultCallback(true))
                    }
                } else {
                    callback.invoke(FirebaseResultCallback(it.isSuccessful, (it.exception as? FirebaseAuthException)?.errorCode ?: ""))
                }
            }
    }

    fun searchUserPassword(id: String, callback: (Boolean) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(id)
            .addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun insertUserProfilePhoto(url: String, callback: (Boolean) -> Unit) {
        firebaseAuth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(url))
            .build()
        )?.addOnCompleteListener {
            callback.invoke(it.isSuccessful)
        }
    }

    fun upLoadStorageImg(uri: Uri, callback: (Boolean) -> Unit) {
        firebaseStorageUserDB.child(firebaseAuth.currentUser?.email.toString())
            .putFile(uri)
            .addOnProgressListener {
                val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
                Log.d("####", "UpLoading >> $progress")
            }
            .addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    fun getUserProfilePhoto(userEmail: String?, callback: (String?) -> Unit) {
        if (userEmail.isNullOrEmpty()) {
            callback.invoke(null)
            return
        }
        firebaseStorageUserDB.child(userEmail).downloadUrl
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.invoke(it.result.toString())
                } else {
                    callback.invoke(null)
                }
            }
    }

    fun deleteStorageImg(callback: (Boolean) -> Unit) {
        firebaseStorageUserDB.child(firebaseAuth.currentUser?.email.toString()).delete()
            .addOnCompleteListener { callback.invoke(it.isSuccessful) }
    }

    fun setFirebaseUserInfoDelete(callback: (Boolean) -> Unit) {
        deleteUserDocument {
            deleteStorageImg {
                firebaseAuth.currentUser?.delete()
                    ?.addOnCompleteListener { callback.invoke(it.isSuccessful) }
            }
        }
    }

}