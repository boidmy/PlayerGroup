package com.example.playergroup.ui.login

import com.example.playergroup.ui.base.BaseRepository
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

    fun insertUserDocument(data: FirebaseUser, callback: (Boolean) -> Unit) {
        val userEmail = data.email
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
}