package com.example.playergroup.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.BaseDataSet
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.ui.base.EmptyErrorViewHolder
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.diffUtilResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchListAdapter(
    private val mCompositeDisposable: CompositeDisposable
): RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {
    var items: MutableList<BaseDataSet>? = mutableListOf()
        set(value) {
            value?.let {
                calculate(value) {
                    field?.clear()
                    field?.addAll(value)
                    it.dispatchUpdatesTo(this)
                }
            } ?: run {
                return
            }
        }

    private fun calculate(value: MutableList<BaseDataSet>?, callback: (DiffUtil.DiffResult) -> Unit) {
        mCompositeDisposable.add(
            Observable.fromCallable { diffUtilResult(
                oldList = items,
                newList = value,
                itemCompare = { o, n -> o?.viewType == n?.viewType },
                contentCompare = { o, n -> o == n }
            ) }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    callback.invoke(it)
                }
        )
    }

    override fun getItemCount(): Int = items?.size ?: 0
    override fun getItemViewType(position: Int): Int = items?.getOrNull(position)?.viewType?.ordinal ?: ViewTypeConst.EMPTY_ERROR.ordinal
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> =
        when(ViewTypeConst.values()[viewType]) {
            ViewTypeConst.SEARCH_ONE_TYPE -> SearchOneItemViewHolder(parent)
            ViewTypeConst.SEARCH_TWO_TYPE -> SearchTwoItemViewHolder(parent)
            else -> EmptyErrorViewHolder(parent)
        } as BaseViewHolder<ViewBinding>

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        holder.onBindView(items?.getOrNull(position))
    }
}