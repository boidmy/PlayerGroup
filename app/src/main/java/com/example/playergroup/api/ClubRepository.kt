package com.example.playergroup.api

import android.net.Uri
import android.util.Log
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.UserInfo
import com.example.playergroup.ui.base.BaseRepository
import com.example.playergroup.ui.dialog.calendar.BaseCalendar.Companion.DATE_FORMAT_YYYYMMDDHHMMSS
import com.example.playergroup.util.getToday
import com.google.firebase.firestore.FirebaseFirestore

class ClubRepository: BaseRepository() {
    /**
     * initCreateClub
     */
    fun insertInitCreateClub(key: String, clubName: String, clubImg: String, callback: (Boolean) -> Unit) {
        val clubData = hashMapOf(
            "clubAdmin" to firebaseAuth.currentUser?.email.toString(),
            "clubName" to clubName,
            "clubPrimaryKey" to key,
            "clubImg" to clubImg,
            "clubCreateDate" to getToday(DATE_FORMAT_YYYYMMDDHHMMSS).toString()
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
    fun getClubData(clubName: String, callback: (ClubInfo?) -> Unit) {
        firebaseClub.document(clubName).get()
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
     * whereIn 은 최대 10개까지만 갖고 온다? ( 참고 )
     */
    fun getClubList(primaryKeys: List<String>, callback: (List<ClubInfo>?) -> Unit) {
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