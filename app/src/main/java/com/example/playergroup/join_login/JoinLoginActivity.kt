package com.example.playergroup.join_login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.playergroup.BaseActivity
import com.example.playergroup.R
import com.example.playergroup.util.DialogCustom
import com.example.playergroup.util.hideKeyboard
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_joinlogin.*

/**
 * 파이어베이스 회원 관리 참고 : https://firebase.google.com/docs/auth/unity/manage-users?hl=ko#send_a_user_a_verification_email
 */

class JoinLoginActivity: BaseActivity() {

    private val TAG = this::class.java.simpleName

    private val mRxBus by lazy { JoinLoginRxBus.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joinlogin)

        vp_join_login_pager.run {
            adapter = JoinLoginAdapter(supportFragmentManager)
            currentItem = mRxBus.LOGINPAGE   // 초기 세팅은 로그인 페이지
        }

        compositeDisposable.add(mRxBus
            .listen_goTo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (currentFocus != null) {
                    hideKeyboard(currentFocus!!)
                }

                if (it == mRxBus.GOMAIN) {
                    goToMain(this)
                } else {
                    vp_join_login_pager.setCurrentItem(it, true)
                }
            })

        compositeDisposable.add(mRxBus
            .listen_snsLogin()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::signInSnsLogin))

        compositeDisposable.add(mRxBus
            .listen_loading()
            .subscribe{showProgress(group_ll_progress, it)})
    }

    override fun onBackPressed() {
        if (vp_join_login_pager.currentItem != mRxBus.LOGINPAGE)
            vp_join_login_pager.setCurrentItem(1, true)
        else
            super.onBackPressed()
    }

    private fun signInSnsLogin(snsLoginContent: Int) {
        mRxBus.publisher_loading(true)
        when (snsLoginContent) {
            mRxBus.GOOGLE -> {
                val mGoogleSignInClient = GoogleSignIn.getClient(this,
                    GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                )
                startActivityForResult(mGoogleSignInClient.signInIntent, mRxBus.GOOGLE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mRxBus.GOOGLE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                mRxBus.publisher_loading(false)
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error > $e")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        FirebaseAuth
            .getInstance()
            .signInWithCredential(credential)
            .addOnCompleteListener {
                mRxBus.publisher_loading(false)
                if (it.isSuccessful) {
                    FirebaseInstanceId
                        .getInstance()
                        .instanceId
                        .addOnSuccessListener { token ->
                            // 토큰값 과 계정정보를 가져온다.
                        }
                    goToMain(this)
                } else {
                    showDialog(this@JoinLoginActivity, getString(R.string.dialog_alert_msg_error)).show()
                }
            }
    }


}