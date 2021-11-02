package com.example.playergroup.ui.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.R
import com.example.playergroup.custom.DialogCustom
import com.example.playergroup.ui.main.MainActivity
import com.example.playergroup.util.ConfigModule
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity<B: ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: B
    val connectivityManager by lazy { getSystemService(ConnectivityManager::class.java) }
    val pgApplication by lazy { PlayerGroupApplication.instance }

    abstract fun getViewBinding(): B
    abstract fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?)
    open fun onReload() {}

    private val FinishIntervalTime: Long = 2000   //화면 종료 버튼 2초 Setting
    private var backPressedTime: Long = 0

    protected val compositeDisposable by lazy { CompositeDisposable() }
    protected val configModule by lazy { ConfigModule(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        onCreateBindingWithSetContentView(savedInstanceState)
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

        if (this is MainActivity) {
            if (isFinishApp) {
                finish()
            } else {
                backPressedTime = tempTime
            }
        } else {
            super.onBackPressed()
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

    fun isWIFIConnected(): Boolean {
        var result = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                result = true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                result = true
            }
        }
        return result
    }

    fun isVisitor(primaryKey: String?): Boolean {
        val involve = pgApplication.userInfo?.clubInvolved ?: mutableListOf()
        val admin = pgApplication.userInfo?.clubAdmin ?: mutableListOf()
        val listJoined = involve.union(admin)
        return listJoined.firstOrNull { it == primaryKey }.isNullOrEmpty()
    }

    fun isJoining(primaryKey: String?): Boolean {
        val joinProgress = pgApplication.userInfo?.joinProgress ?: mutableListOf()
        val joinPrimaryKey = joinProgress.firstOrNull { it == primaryKey }
        return !joinPrimaryKey.isNullOrEmpty()
    }

    fun isAdmin(primaryKey: String?): Boolean {
        val admin = pgApplication.userInfo?.clubAdmin ?: mutableListOf()
        return !admin.firstOrNull { it == primaryKey }.isNullOrEmpty()
    }

    override fun onResume() {
        super.onResume()
    }
}