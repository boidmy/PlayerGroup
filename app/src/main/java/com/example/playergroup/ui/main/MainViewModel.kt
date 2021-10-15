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

    private val _mainDataSetObserve: MutableLiveData<MutableList<MainDataSet>> = MutableLiveData()
    val mainDataSet: LiveData<MutableList<MainDataSet>>
        get() = _mainDataSetObserve

    // todo Refactoring..
    fun getMainData(saveList: MutableList<ViewTypeConst>) {
        Observable.fromCallable { createMainDataSet(saveList) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _mainDataSetObserve.value = it
            }, {
                Log.d("####", "Create MainDataSet Error > ${it.message}")
            })
            .addTo(compositeDisposable)
    }

    private fun createMainDataSet(saveList: MutableList<ViewTypeConst>): MutableList<MainDataSet> {
        // todo Refactoring..
        val modules = mutableListOf<MainDataSet>(
            MainDataSet(viewType = ViewTypeConst.MAIN_MY_INFO)
        )
        saveList.forEach {
            modules.add(MainDataSet(viewType = it))
        }
        return modules
    }
}