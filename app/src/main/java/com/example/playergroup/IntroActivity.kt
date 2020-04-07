package com.example.playergroup

import android.os.Bundle

class IntroActivity: BaseActivity() {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!getNetWorkState()) {   // 인터넷 연결 여부
            compositeDisposable.add(
                setDelay(TAG, 500)  // 원활한 애니메이션을 위해 딜레이 추가.
                    .map { this }
                    .subscribe(this::finishAlert)
            )
        } else {
            init()
        }
    }

    private fun init() {
        compositeDisposable.add(
            setDelay(TAG, 1000) // 현재는 데이터를 가져오는 통신 로직이 없어 딜레이로 대체
                .map { this }
                .subscribe(this::goToLogin)
        )
    }
}