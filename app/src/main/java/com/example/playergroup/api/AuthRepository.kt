package com.example.playergroup.api

import android.net.Uri
import android.util.Log
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.data.FirebaseResultCallback
import com.example.playergroup.data.UserInfo
import com.example.playergroup.ui.base.BaseRepository
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import io.reactivex.Single

class AuthRepository: BaseRepository() {

    fun createGetPrimaryKey() = firebaseUser.document().id

    /**
     * 구글 로그인 회원 가입
     */
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
    fun insertInitUserDocument(callback: (Boolean) -> Unit) {
        val data = firebaseAuth.currentUser
        val userEmail = data?.email
        if (userEmail.isNullOrEmpty()) {
            callback.invoke(false)
            return
        }

        val user = hashMapOf(
            "email" to userEmail.toString(),
            "userPrimaryKey" to createGetPrimaryKey()
        )

        firebaseUser.document(userEmail).set(user)
            .addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    /**
     * 클럽을 새로 만들었을 경우 Admin 으로 User 정보에 등록
     */
    fun upDateClubUserField(clubPrimaryKey: String, callback: (Boolean) -> Unit) {
        val data = firebaseAuth.currentUser
        val userEmail = data?.email
        if (userEmail.isNullOrEmpty()) {
            callback.invoke(false)
            return
        }

        firebaseDB.runTransaction {
            val userDB = firebaseUser.document(userEmail)
            val userInfo = it.get(userDB).toObject(UserInfo::class.java) ?: return@runTransaction
            userInfo.clubAdmin?.let {
                userInfo.clubAdmin?.add(clubPrimaryKey)
            } ?: run {
                userInfo.clubAdmin = mutableListOf(clubPrimaryKey)
            }
            it.set(userDB, userInfo)
        }
            .addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    /**
     * 회원 탈퇴 할 때 User 정보 삭제
     */
    fun setFirebaseUserInfoDelete(callback: (Boolean) -> Unit) {
        deleteUserDocument {
            deleteStorageImg {
                firebaseAuth.currentUser?.delete()
                    ?.addOnCompleteListener { callback.invoke(it.isSuccessful) }
            }
        }
    }

    private fun deleteUserDocument(callback: (Boolean) -> Unit) {
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
    fun insertInitUserDocument(userInfo: UserInfo, callback: (Boolean) -> Unit) {
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

    /**
     * 프로필 데이터가 저장되어 있는지 확인
     */
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
                PlayerGroupApplication.instance.userInfo = userInfo
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
     * 클럽 멤버는 admin(클럽모임장) 을 포함한 가입된 회원 을 조회하기 때문에 우선 admin 을 데리고 와야 한다..
     */
    fun getClubMemberData(primaryKey: String) =
        Single.create<List<UserInfo>> { emitter ->
            firebaseUser.whereArrayContains("clubAdmin", primaryKey).get()
                .addOnSuccessListener {
                    val adminUser = it.toObjects(UserInfo::class.java)
                    firebaseUser.whereArrayContains("clubInvolved", primaryKey).get()
                        .addOnSuccessListener {
                            val involvedMember = it.toObjects(UserInfo::class.java)
                            emitter.onSuccess(adminUser.plus(involvedMember))
                        }
                        .addOnFailureListener { emitter.onError(it) }
                }
                .addOnFailureListener { emitter.onError(it) }
        }

    fun getJoinProgressData(primaryKey: String) =
        Single.create<List<UserInfo>> { emitter ->
            firebaseUser.whereArrayContains("joinProgress", primaryKey).get()
                .addOnSuccessListener { emitter.onSuccess(it.toObjects(UserInfo::class.java)) }
                .addOnFailureListener { emitter.onError(it) }
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

    /**
     * 이메일 인증 보내기
     */
    fun sendEmailVerification(callback: (Boolean) -> Unit) {
        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    /**
     * Email 로 회원 가입
     */
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

    /**
     * 패스워드 재설정 메일 보내기
     */
    fun searchUserPassword(id: String, callback: (Boolean) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(id)
            .addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    /**
     * Storage 에 사진 저장 하기
     */
    fun upLoadStorageImg(uri: Uri?, callback: (Boolean) -> Unit) {
        if (uri == null) {
            callback.invoke(false)
        } else {
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
    }

    /**
     * Storage 에서 사진 URL 가져오기
     */
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

    /**
     * Storage 에 사진 삭제 ( 회원 탈퇴 할 때 사용 )
     */
    fun deleteStorageImg(callback: (Boolean) -> Unit) {
        firebaseStorageUserDB.child(firebaseAuth.currentUser?.email.toString()).delete()
            .addOnCompleteListener { callback.invoke(it.isSuccessful) }
    }

    /**
     * 이름 중복 확인용
     */
    fun getOverlapCheck(field: String, callback: (Boolean) -> Unit) {
        firebaseUser.whereEqualTo("name", field).get()
            .addOnCompleteListener {
                callback.invoke(it.result?.isEmpty ?: true)
            }
    }

}