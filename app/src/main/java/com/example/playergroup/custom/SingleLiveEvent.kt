package com.example.playergroup.custom

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 문제점
 * 내부에서 여러번 호출되는 것을 막고 있기 때문에 오직 하나의 옵저빙만 가능하다.
 * 여러개의 옵저빙을 등록한다 해도 하나만 호출됨.
 */

class SingleLiveEvent<T>: MutableLiveData<T>() {

    // setValue 로 새로운 이벤트를 받으면 true 로 바뀌고 그 이벤트가 실행되면 false 로 돌아감.
    private val isPending = AtomicBoolean(false)

    /**
     * 1. 값이 변경되면 false 였던 isPending 이 true 로 바뀌고, Observer 가 호출 됨.
     */
    @MainThread
    override fun setValue(value: T?) {
        isPending.set(true)
        super.setValue(value)
    }

    /**
     * 2. 내부에 등록된 Observer 는 isPending 이 true 인지 확인후, true 일 경우 다시 false 로 돌려 놓은 후에 이벤트가 호출되었다고 알림.
     */
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer {
            if (isPending.compareAndSet(true, false)) observer.onChanged(it)
        })
    }

    /**
     * T가 Void 일 경우 호출을 편하게 하기 위한 함수.
     */
    @MainThread
    fun call() {
        value = null
    }
}