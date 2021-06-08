package com.example.playergroup.ui.base

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

abstract class BaseRepository {
    protected val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    protected val firebaseDB by lazy { FirebaseFirestore.getInstance() }
    protected val firebaseUser by lazy { firebaseDB.collection("users") }
    protected val firebaseBoardDB by lazy { firebaseDB.collection("board") }
}