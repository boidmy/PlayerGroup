package com.example.playergroup.ui.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.playergroup.R
import com.example.playergroup.util.DialogCustom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

abstract class BaseActivity<B: ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: B
    val connectivityManager by lazy { getSystemService(ConnectivityManager::class.java) }

    abstract fun getViewBinding(): B
    abstract fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?)
    abstract fun onNetworkStateLost(network: Network?)

    private val FinishIntervalTime: Long = 2000   //화면 종료 버튼 2초 Setting
    private var backPressedTime: Long = 0

    protected val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    protected val firebaseDB by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        onCreateBindingWithSetContentView(savedInstanceState)
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

    private val networkCallback = object: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            // 인터넷이 연결되면 호출됨.
        }
        override fun onLost(network: Network) {
            super.onLost(network)
            // 인터넷이 끊어지면 호출됨.
            onNetworkStateLost(network)
        }
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun terminateNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onResume() {
        super.onResume()
        registerNetworkCallback()
    }

    override fun onStop() {
        super.onStop()
        terminateNetworkCallback()
    }

    // 프로그스 바
    protected fun showProgress(view: View, isShow: Boolean) {
        view.visibility = if (isShow) View.VISIBLE else View.GONE
    }

}