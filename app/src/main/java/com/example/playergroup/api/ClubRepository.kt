package com.example.playergroup.api

import android.net.Uri
import android.util.Log
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.ui.base.BaseRepository
import com.example.playergroup.ui.dialog.calendar.BaseCalendar.Companion.DATE_FORMAT_YYYYMMDDHHMMSS
import com.example.playergroup.util.getToday
import com.google.firebase.firestore.FieldValue
import io.reactivex.Completable
import io.reactivex.Single

class ClubRepository: BaseRepository() {
    /**
     * initCreateClub
     */
    fun insertInitCreateClub(key: String, clubName: String, clubImg: String, location: String, callback: (Boolean) -> Unit) {
        val clubData = hashMapOf(
            "clubAdmin" to firebaseAuth.currentUser?.email.toString(),
            "clubName" to clubName,
            "clubPrimaryKey" to key,
            "clubImg" to clubImg,
            "clubCreateDate" to getToday(DATE_FORMAT_YYYYMMDDHHMMSS).toString(),
            "clubActivityArea" to location,
            "member" to mutableListOf<String>(),
            "joinProgress" to mutableListOf<String>()
        )
        firebaseClub.document(key).set(clubData)
            .addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    /**
     * Storage 에 클럽 이미지 넣기
     */
    fun upLoadClubImg(name: String, imgUri: Uri?, callback: (Boolean) -> Unit) {
        if (imgUri != null)
        firebaseStorageClubDB.child(name)
            .putFile(imgUri)
            .addOnProgressListener {
                val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
                Log.d("####", "UpLoading >> $progress")
            }
            .addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
        else callback.invoke(false)
    }

    /**
     * 클럽 유무
     */
    fun isClubEmpty(clubName: String, callback: (Boolean) -> Unit) {
        firebaseClub.document(clubName).get()
            .addOnCompleteListener {
                callback.invoke(it.result?.data == null)
            }
    }

    /**
     * 클럽 데이터 가져오기
     */
    fun getClubData(primaryKey: String, callback: (ClubInfo?) -> Unit) {
        firebaseClub.document(primaryKey).get()
            .addOnCompleteListener {
                val clubInfo = (it.result?.toObject(ClubInfo::class.java))
                callback.invoke(clubInfo)
                /*val clubImg = clubInfo?.clubImg
                if (clubImg.isNullOrEmpty()) {
                    callback.invoke(clubInfo)
                } else {
                    firebaseStorageClubDB.child(clubImg).downloadUrl
                        .addOnCompleteListener {
                            clubInfo.clubImgFullUrl = it.result.toString()
                            callback.invoke(clubInfo)
                        }
                }*/
            }
    }

    /**
     * Storage 에서 사진 URL 가져오기
     */
    fun getUserProfilePhoto(clubName: String?, callback: (String?) -> Unit) {
        if (clubName.isNullOrEmpty()) {
            callback.invoke(null)
            return
        }
        firebaseStorageClubDB.child(clubName).downloadUrl
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.invoke(it.result.toString())
                } else {
                    callback.invoke(null)
                }
            }
    }

    /**
     * 클럽 목록 가져오기
     */
    fun getClubList(callback: (List<ClubInfo>?) -> Unit) {
        firebaseClub.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = it.result?.toObjects(ClubInfo::class.java)
                callback.invoke(list)
            } else {
                callback.invoke(null)
            }
        }
    }

    /**
     * whereIn 의 비교는 최대 10개 까지만 허용 한다..
     * 결국 primaryKey 가 10개 까지만 검색할 수 있다 .. 참고
     */
    fun getClubList(primaryKeys: List<String>?, callback: (List<ClubInfo>?) -> Unit) {
        if (primaryKeys.isNullOrEmpty()) {
            callback.invoke(null)
        } else {
            firebaseClub.whereIn("clubPrimaryKey", primaryKeys).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val list = it.result?.toObjects(ClubInfo::class.java)
                    callback.invoke(list)
                } else {
                    callback.invoke(null)
                }
            }
        }
    }

    fun getClubList(clubActivityArea: String) =
        Single.create<List<ClubInfo>?> { emitter ->
            firebaseClub
                .whereEqualTo("clubActivityArea", clubActivityArea)
                .get()
                .addOnSuccessListener {
                    emitter.onSuccess(it.toObjects(ClubInfo::class.java))
                }.addOnFailureListener {
                    emitter.onError(it)
                }
        }

    /**
    // Atomically add a new region to the "regions" array field.
    washingtonRef.update("regions", FieldValue.arrayUnion("greater_virginia"))

    // Atomically remove a region from the "regions" array field.
    washingtonRef.update("regions", FieldValue.arrayRemove("east_coast"))
     */
    fun addClubJoinProgress(primaryKey: String, email: String) =
        Completable.create { emitter ->
            firebaseDB.runBatch {
                firebaseClub.document(primaryKey).update("joinProgress", FieldValue.arrayUnion(email))
                    .addOnSuccessListener {
                        firebaseUser.document(email).update("joinProgress", FieldValue.arrayUnion(primaryKey))
                            .addOnSuccessListener { emitter.onComplete() }
                            .addOnFailureListener {
                                Log.d("####", "[Join중..] User 에 저장하다가 실패 ${it.message}")
                                emitter.onError(it) }
                    }
                    .addOnFailureListener {
                        Log.d("####", "[Join중..] Club 에 저장하다가 실패 ${it.message}")
                        emitter.onError(it)
                    }
            }
        }

    fun removeClubJoinProgress(primaryKey: String, email: String) =
        Completable.create { emitter ->
            firebaseDB.runBatch {
                firebaseClub.document(primaryKey).update("joinProgress", FieldValue.arrayRemove(email))
                    .addOnSuccessListener {
                        firebaseUser.document(email).update("joinProgress", FieldValue.arrayRemove(primaryKey))
                            .addOnSuccessListener { emitter.onComplete() }
                            .addOnFailureListener {
                                Log.d("####", "[Remove중..] User 에 저장하다가 실패 ${it.message}")
                                emitter.onError(it)
                            }
                    }
                    .addOnFailureListener {
                        Log.d("####", "[Remove중..] Club 에 저장하다가 실패 ${it.message}")
                        emitter.onError(it)
                    }
            }
        }

    fun getMemberDataJoinResult(primaryKey: String, email: String, isState: Boolean) =
        Completable.create { emitter ->
            firebaseDB.runBatch {
                val clubDB = firebaseClub.document(primaryKey)
                it.update(clubDB, "joinProgress", FieldValue.arrayRemove(email))
                if (isState) it.update(clubDB, "member", FieldValue.arrayUnion(email))
                val userDB = firebaseUser.document(email)
                it.update(userDB, "joinProgress", FieldValue.arrayRemove(primaryKey))
                if (isState) it.update(userDB, "clubInvolved", FieldValue.arrayUnion(primaryKey))
            }
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }

    fun removeUserClubMember(primaryKey: String, email: String) =
        Completable.create { emitter ->
            firebaseDB.runBatch {
                val clubDB = firebaseClub.document(primaryKey)
                it.update(clubDB, "member", FieldValue.arrayRemove(email))
                val userDB = firebaseUser.document(email)
                it.update(userDB, "clubInvolved", FieldValue.arrayRemove(primaryKey))
            }
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }

    fun updateClubDescription(clubPrimaryKey: String?, description: String) {
        if (clubPrimaryKey.isNullOrEmpty()) return
        firebaseClub.document(clubPrimaryKey).update("clubDescription", description)
            .addOnSuccessListener { Log.d("####", "update to clubDescription success!!") }
            .addOnFailureListener { Log.d("####", "update to clubDescription Fail!! >> ${it.message}") }
    }
}