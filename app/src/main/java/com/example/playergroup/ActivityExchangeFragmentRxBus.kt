package com.example.playergroup

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * MainActivity 와 FragmentNavigation 간 RxBus
 * 주로.. 프로그레스바..
 */
class ActivityExchangeFragmentRxBus {

    companion object {
        @Volatile
        private var instance: ActivityExchangeFragmentRxBus? = null

        @JvmStatic
        fun getInstance(): ActivityExchangeFragmentRxBus =
            instance ?: synchronized(this) {
                instance ?: ActivityExchangeFragmentRxBus().also {
                    instance = it
                }
            }
    }

    // loadingbar
    private val loadingProgressBar: PublishSubject<Boolean> = PublishSubject.create()
    fun publisher_loading(isShow: Boolean) {loadingProgressBar.onNext(isShow)}
    fun listen_loading(): Observable<Boolean> = loadingProgressBar
}