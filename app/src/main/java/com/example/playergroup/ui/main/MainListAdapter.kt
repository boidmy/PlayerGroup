package com.example.playergroup.ui.main

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.BaseDataSet
import com.example.playergroup.data.MainDataSet
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.ui.base.EmptyErrorViewHolder
import com.example.playergroup.ui.main.holder.*
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.diffUtilExtensions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class MainListAdapter(
    private val mCompositeDisposable: CompositeDisposable
): RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {
    var items: MutableList<MainDataSet>? = null
        set(value) {
            value?.let {
                calculate(value) {
                    field?.let {
                        it.clear()
                        it.addAll(value)
                    } ?: run {
                        field = value
                    }
                    it.dispatchUpdatesTo(this)
                }
            } ?: run {
                return
            }
        }

    private fun calculate(data: MutableList<MainDataSet>, callback: (DiffUtil.DiffResult) -> Unit) {
        Observable.fromCallable { diffUtilExtensions(
            oldList = items,
            newList = data,
            itemCompare = { o, n -> o?.viewType == n?.viewType },
            contentCompare = { o, n -> o == n }
        )}
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback.invoke(it)
            }, {
                Log.d("####", "calculate Error > ${it.message}")
            })
            .addTo(mCompositeDisposable)
    }

    override fun getItemCount(): Int = items?.size ?: 0
    override fun getItemViewType(position: Int): Int = items?.getOrNull(position)?.viewType?.ordinal ?: ViewTypeConst.EMPTY_ERROR.ordinal
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> =
        when(ViewTypeConst.values()[viewType]) {
            ViewTypeConst.MAIN_MY_INFO -> MainMyInfoViewHolder(parent)
            ViewTypeConst.MAIN_CLUB_INFO -> MainClubInfoViewHolder(parent)
            ViewTypeConst.MAIN_CLUB_PICK_INFO -> MainClubPickInfoViewHolder(parent)
            ViewTypeConst.MAIN_PICK_LOCATION_INFO -> MainPickLocationInfoViewHolder(parent)
            ViewTypeConst.MAIN_APP_COMMON_BOARD_INFO -> MainAppCommonBoardInfoViewHolder(parent)
            else -> EmptyErrorViewHolder(parent)
        } as BaseViewHolder<ViewBinding>

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        holder.onBindView(items?.getOrNull(position))
    }
}