package com.example.playergroup.api

import android.net.Uri
import android.util.Log
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.UserInfo
import com.example.playergroup.ui.base.BaseRepository

class ClubRepository: BaseRepository() {
    /**
     * initCreateClub
     */
    fun insertInitCreateClub(clubName: String, clubImg: Uri?, callback: (Boolean) -> Unit) {
        val clubData = hashMapOf(
            "clubAdmin" to firebaseAuth.currentUser?.email.toString(),
            "clubName" to clubName
        )
        if (clubImg != null) clubData["clubImg"] = clubName
        firebaseClub.document(clubName).set(clubData)
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
                val clubImg = clubInfo?.clubImg
                if (clubImg.isNullOrEmpty()) {
                    callback.invoke(clubInfo)
                } else {
                    firebaseStorageClubDB.child(clubImg).downloadUrl
                        .addOnCompleteListener {
                            clubInfo.clubImgFullUrl = it.result.toString()
                            callback.invoke(clubInfo)
                        }
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
}