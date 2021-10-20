package com.example.playergroup.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import com.example.playergroup.data.MainDataSet
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.diffUtilResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

typealias GetCurrentMainListData = (() -> MutableList<MainDataSet>?)
class MainViewModel: BaseViewModel() {

    private val _mainDataSetObserve: MutableLiveData<Pair<MutableList<MainDataSet>?, DiffUtil.DiffResult>> = MutableLiveData()
    val mainDataSet: LiveData<Pair<MutableList<MainDataSet>?, DiffUtil.DiffResult>>
        get() = _mainDataSetObserve

    var getCurrentMainListData: GetCurrentMainListData? = null

    // todo Refactoring..
    fun getMainData(saveList: MutableList<ViewTypeConst>) {
        Observable.fromCallable { createMainDataSet(saveList) }
            .subscribeOn(Schedulers.computation())
            .map(::calculateDiffUtilResult)
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

    private fun calculateDiffUtilResult(data: MutableList<MainDataSet>): Pair<MutableList<MainDataSet>?, DiffUtil.DiffResult> {
        val getCurrentList = getCurrentMainListData?.invoke()
        val result = diffUtilResult(
            oldList = getCurrentList,
            newList = data,
            itemCompare = { o, n -> o?.viewType == n?.viewType },
            contentCompare = { o, n -> o == n }
        )
        return Pair(data, result)
    }
}