package com.example.playergroup.ui.login

import com.example.playergroup.data.FirebaseResultCallback
import com.example.playergroup.ui.base.BaseRepository
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

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

    fun insertUserDocument(callback: (Boolean) -> Unit) {
        val data = firebaseAuth.currentUser
        val userEmail = data?.email
        if (userEmail.isNullOrEmpty()) {
            callback.invoke(false)
            return
        }

        val user = hashMapOf(
            "email" to userEmail.toString(),
            "name" to data.displayName.toString(),
            "img" to data.photoUrl.toString()
        )

        firebaseUser.document(userEmail).set(user)
            .addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    fun searchUserDocument(userEmail: String, callback: (Boolean) -> Unit) {
        firebaseUser.document(userEmail).get()
            .addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    /**
     * createEmail 오류코드 확인하여 적용 필요
     * https://firebase.google.com/docs/auth/admin/errors?hl=ko
     */
    fun createEmailUser(id: String, pw: String, callback: (FirebaseResultCallback) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(id, pw)
            .addOnCompleteListener {
                callback.invoke(FirebaseResultCallback(it.isSuccessful, if (it is FirebaseAuthException) it.errorCode else ""))
            }
    }

    fun sendEmailVerification(callback: (Boolean) -> Unit) {
        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }
}