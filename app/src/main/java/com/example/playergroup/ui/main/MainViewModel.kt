package com.example.playergroup.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.MainDataSet
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.ViewTypeConst
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class MainViewModel: BaseViewModel() {

    private val _mainDataSetObserve: MutableLiveData<List<MainDataSet>> = MutableLiveData()
    val mainDataSet: LiveData<List<MainDataSet>>
        get() = _mainDataSetObserve

    fun getMainData() {
        Observable.fromCallable { createMainDataSet() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _mainDataSetObserve.value = it
            }, {
                Log.d("####", "Create MainDataSet Error > ${it.message}")
            })
            .addTo(compositeDisposable)
    }

    private fun createMainDataSet(): MutableList<MainDataSet> {
        // todo Refactoring..
        val moduls = mutableListOf<MainDataSet>(
            MainDataSet(viewType = ViewTypeConst.MAIN_MY_INFO),
            MainDataSet(viewType = ViewTypeConst.MAIN_CLUB_INFO),
            MainDataSet(viewType = ViewTypeConst.MAIN_CLUB_PICK_INFO),
            MainDataSet(viewType = ViewTypeConst.MAIN_PICK_LOCATION_INFO),
            MainDataSet(viewType = ViewTypeConst.MAIN_APP_COMMON_BOARD_INFO)
        )
        return moduls
    }
}