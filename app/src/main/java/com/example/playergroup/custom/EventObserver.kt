package com.example.playergroup.custom

import androidx.lifecycle.Observer

class EventObserver<T>(private val onEventUnhandledContent: (T?) -> Unit): Observer<Event<T>> {
    /**
     * nullable 하지 않아
     * null 이 들어오면 Event 를 처리할 수 없다.
     */
    /*override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent.invoke(value)
        }
    }*/

    override fun onChanged(event: Event<T>?) {
        event ?: return
        if (event.content == null && !event.hasBeenHandled) {
            event.getContentIfNotHandled()
            onEventUnhandledContent.invoke(null)
            return
        }

        event.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent.invoke(value)
        }
    }
}