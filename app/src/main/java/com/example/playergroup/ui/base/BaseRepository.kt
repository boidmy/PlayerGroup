package com.example.playergroup.ui.base

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.FirebaseStorage

abstract class BaseRepository {
    protected val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    protected val firebaseDB by lazy { FirebaseFirestore.getInstance() }
    protected val firebaseUser by lazy { firebaseDB.collection("users") }
    protected val firebaseClub by lazy { firebaseDB.collection("club") }
    protected val firebaseInit by lazy { firebaseDB.collection("init") }
    protected val firebaseStorageDB by lazy { FirebaseStorage.getInstance().reference }
    protected val firebaseStorageUserDB by lazy { firebaseStorageDB.child("userImg") }
    protected val firebaseStorageClubDB by lazy { firebaseStorageDB.child("clubImg") }
    protected val firebaseNoticeBoard by lazy { firebaseDB.collection("noticeBoard") }
}