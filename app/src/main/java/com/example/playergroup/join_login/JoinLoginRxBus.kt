package com.example.playergroup.join_login

import android.content.Intent
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.subjects.PublishSubject

/**
 * Singleton RxBus
 * 뭔가 Interface 를 대체 할 수 있는 느낌 ?
 * https://androidwave.com/fragment-communication-using-rxjava/
 * Kotlin Singleton Example >> https://bonoogi.postype.com/post/3591846
 */

class JoinLoginRxBus {

    companion object {
        @Volatile
        private var instance: JoinLoginRxBus? = null

        @JvmStatic
        fun getInstance(): JoinLoginRxBus =
            instance ?: synchronized(this) {
                instance ?: JoinLoginRxBus().also {
                    instance = it
                }
            }
    }

    val JOINPAGE = 0    // 회원 가입 페이지
    val LOGINPAGE = 1   // 로그인 페이지
    val SEARCHMEMBERINFO = 2    // 아이디 / 비밀번호 찾기 페이지
    //TODO TEST Ver
    val GOMAIN = 3 // 홈으로 이동

    //SNS Login Content
    val GOOGLE = 4  // Google Login

    private val goToPagePublisher: PublishSubject<Int> = PublishSubject.create()
    fun publisher_goTo(page: Int) { goToPagePublisher.onNext(page) }
    fun listen_goTo(): Observable<Int> = goToPagePublisher

    private val idpw_publisher: PublishSubject<IDPW> = PublishSubject.create()
    fun publisher_idpw(id: String, pw: String) { idpw_publisher.onNext(IDPW(id, pw)) }
    fun listen_idpw(): Observable<IDPW> = idpw_publisher
    data class IDPW(val id: String, val pw: String)

    // *************** SNS 로그인 ******************
    private val snsLogin: PublishSubject<Int> = PublishSubject.create()
    fun publisher_snsLogin(snsLoginContent: Int) { snsLogin.onNext(snsLoginContent) }
    fun listen_snsLogin() : Observable<Int> = snsLogin
}