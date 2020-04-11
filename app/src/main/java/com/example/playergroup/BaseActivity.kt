package com.example.playergroup

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.playergroup.join_login.JoinLoginActivity
import com.example.playergroup.util.DialogCustom
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

open class BaseActivity : AppCompatActivity() {

    private val FinishIntervalTime: Long = 2000   //화면 종료 버튼 2초 Setting
    private var backPressedTime: Long = 0

    protected val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun onBackPressed() {
        val tempTime =
            System.currentTimeMillis() // 옛날부터 지금까지의 시간을 Milli단위로 저장해둔 함수. -> 엄청 큰 숫자가 저장됨.
        val intervalTime = tempTime - backPressedTime

        val isFinishApp = intervalTime in 0..FinishIntervalTime

        if (isFinishApp) {
            super.onBackPressed()
            finish()
        } else {
            backPressedTime = tempTime
        }
    }

    protected fun finishAlert(context: Context) {
        if (!this.isFinishing) {
            DialogCustom(context)
                .setMessage(R.string.dialog_alert_msg_network)
                .setConfirmBtnText(R.string.app_finish_btn)
                .setDialogCancelable(false)
                .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                    override fun onClick(dialogCustom: DialogCustom) {
                        dialogCustom.dismiss()
                        finish()
                    }
                })
                .show()
        }
    }

    protected fun showDialog(context: Context, msg: String): DialogCustom =
        DialogCustom(context)
            .setMessage(msg)
            .setConfirmBtnText(R.string.ok)
            .setDialogCancelable(false)
            .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                override fun onClick(dialogCustom: DialogCustom) {
                    dialogCustom.dismiss()
                }
            })

    protected fun goToMain(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    protected fun goToLogin(context: Context) {
        val intent = Intent(context, JoinLoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    protected fun setDelay(tag: String, delay: Long): Observable<Long> {
        return Observable
            .timer(delay, TimeUnit.MILLISECONDS)
            .doOnSubscribe { Log.i(tag, "Timer Rx 자원해제 완료") }
            .observeOn(AndroidSchedulers.mainThread())
    }


    // Network Check
    // https://ababqq.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%81%AC-%EC%83%81%ED%83%9C-%EC%B2%B4%ED%81%AC-getNetworkInfo-getActiveNetworkInfo
    protected fun getNetWorkState(): Boolean {
        var result = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }

    // 프로그스 바
    protected fun showProgress(view: View, isShow: Boolean) {
        view.visibility = if (isShow) View.VISIBLE else View.GONE
    }

}