package com.example.playergroup.join_login

import android.os.Bundle
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.example.playergroup.BaseActivity
import com.example.playergroup.R
import com.example.playergroup.util.hideKeyboard
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_joinlogin.*

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
                    Toast.makeText(this, "강제로 메인으로 이동합니다.", Toast.LENGTH_LONG).show()
                } else {
                    vp_join_login_pager.setCurrentItem(it, true)
                }
            })
    }

    override fun onBackPressed() {
        if (vp_join_login_pager.currentItem != mRxBus.LOGINPAGE)
            vp_join_login_pager.setCurrentItem(1, true)
        else
            super.onBackPressed()
    }
}