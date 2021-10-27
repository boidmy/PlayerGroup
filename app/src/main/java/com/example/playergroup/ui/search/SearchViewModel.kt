package com.example.playergroup.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import com.example.playergroup.data.*
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.ViewTypeConst.SEARCH_ONE_TYPE
import com.example.playergroup.util.ViewTypeConst.SEARCH_TWO_TYPE
import com.example.playergroup.util.checkItemsAre
import com.example.playergroup.util.diffUtilResult
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

typealias GetCurrentSearchListData = (() -> MutableList<SearchDataSet>?)
class SearchViewModel: BaseViewModel() {

    private val _firebaseClubListData: MutableLiveData<Pair<MutableList<SearchDataSet>?, DiffUtil.DiffResult>> = MutableLiveData()
    val firebaseClubListData: LiveData<Pair<MutableList<SearchDataSet>?, DiffUtil.DiffResult>>
        get() = _firebaseClubListData

    var getCurrentSearchListData: GetCurrentSearchListData? = null

    fun getData(location: String, isTwoItemMode: Boolean) {
        clubRepository.getClubList(location)
            .subscribeOn(Schedulers.io())
            .map { createSearchDataSet(it, isTwoItemMode) }
            .map(::calculateDiffUtilResult)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _firebaseClubListData.value = it
            }, {
                Log.d("####", "Create SearchDataSet Error > ${it.message}")
            })
            .addTo(compositeDisposable)
    }

    private fun createSearchDataSet(data: List<ClubInfo>, isTwoItemMode: Boolean): MutableList<SearchDataSet>{
        val modules = mutableListOf<SearchDataSet>()
        data.forEach {
            modules.add(SearchDataSet(viewType = if (isTwoItemMode) SEARCH_TWO_TYPE else SEARCH_ONE_TYPE, data = it))
        }
        return modules
    }

    private fun calculateDiffUtilResult(data: MutableList<SearchDataSet>): Pair<MutableList<SearchDataSet>?, DiffUtil.DiffResult> {
        val getCurrentList = getCurrentSearchListData?.invoke()
        val result = diffUtilResult(
            oldList = getCurrentList,
            newList = data,
            itemCompare = { o, n -> (o?.viewType == n?.viewType || o?.data?.clubName == n?.data?.clubName) },
            contentCompare = { o, n -> o == n }
        )
        return Pair(data, result)
    }

    fun modeChange(isTwoItemMode: Boolean) {
        Single.fromCallable {
            val data = getCurrentSearchListData?.invoke()
            data?.map { it.viewType = if (isTwoItemMode) SEARCH_TWO_TYPE else SEARCH_ONE_TYPE }?.toMutableList()
            data?.checkItemsAre<SearchDataSet>()?.toMutableList()
        }
            .subscribeOn(Schedulers.computation())
            .map(::calculateDiffUtilResult)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _firebaseClubListData.value = it
            }, {
                Log.d("####", "modeChange SearchDataSet Error > ${it.message}")
            }).addTo(compositeDisposable)
    }
}