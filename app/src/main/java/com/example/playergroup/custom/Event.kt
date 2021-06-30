package com.example.playergroup.custom

open class Event<out T>(val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {    // 이벤트가 이미 처리되었다면?
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * 이벤트의 처리 여부에 상관 없이 값을 반환.
     */
    fun peekContent(): T = content
}