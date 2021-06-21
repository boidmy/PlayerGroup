package com.example.playergroup.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.playergroup.databinding.ViewLoadingProgressBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class LoadingProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val loadingProgressBar: PublishSubject<Boolean> = PublishSubject.create()
    fun publisherLoading(isShow: Boolean) {
        loadingProgressBar.onNext(isShow)
    }

    private val binding: ViewLoadingProgressBinding by lazy {
        ViewLoadingProgressBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private val mCompositeDisposable by lazy { CompositeDisposable() }

    init {
        visibility = View.GONE
        binding
        setOnTouchListener { _, _ -> return@setOnTouchListener true }
        mCompositeDisposable.add(
            loadingProgressBar
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showProgress)
        )
    }

    private fun showProgress(isShow: Boolean) {
        visibility = if (isShow) View.VISIBLE else View.GONE
        binding.root.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mCompositeDisposable.clear()
    }

}