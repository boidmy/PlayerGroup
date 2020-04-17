package com.example.playergroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import com.example.playergroup.data.UserInfo
import com.example.playergroup.fragments.MyInfoFragment
import com.example.playergroup.util.REQUEST_CODE_GALLERY
import com.example.playergroup.util.goTo
import kotlinx.android.synthetic.main.activity_joinlogin.*

class MainActivity : BaseActivity() {

    lateinit var main_container: View

    private val mRxBus by lazy { ActivityExchangeFragmentRxBus.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_container = findViewById(R.id.my_nav_host_fragment)

        mRxBus.publisher_loading(true)
        val userEmail = firebaseAuth.currentUser?.email?: ""
        firebaseDB
            .collection("users")
            .document(userEmail)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val userInfo = (it.result?.toObject(UserInfo::class.java))

                    MainApplication.getInstance().setUserInfo(userInfo)
                    if (userInfo?.height.isNullOrEmpty()) {
                        // 신체 정보를 입력하지 않았다는거 ( 처음 로그인한 사람이라는거 ) 그럼 개인정보 입력하는 페이지로 이동
                        main_container goTo R.id.action_mainFragment_to_myInfoFragment
                    } else {
                        // TODO 개인정보 입력되어 있는 사람이 여기 지나감.
                    }
                    mRxBus.publisher_loading(false)
                } else {
                    Log.d("####", "user 정보를 가져오는데 실패하였습니다.. >> "+ it.exception?.printStackTrace())
                    mRxBus.publisher_loading(false)
                }
            }

        compositeDisposable.add(mRxBus
            .listen_loading()
            .subscribe{showProgress(group_ll_progress, it)})

    }

    override fun onBackPressed() {
        if (main_container.findNavController().currentDestination?.id != R.id.mainFragment) {
            main_container.findNavController().popBackStack()
        } else
            super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_GALLERY -> {
                if (main_container.findNavController().currentDestination?.id != R.id.myInfoFragment) {
                    (main_container as MyInfoFragment).onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }
}
