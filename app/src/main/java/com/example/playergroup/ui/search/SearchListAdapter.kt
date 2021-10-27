package com.example.playergroup.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.BaseDataSet
import com.example.playergroup.data.MainDataSet
import com.example.playergroup.data.SearchDataSet
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.ui.base.EmptyErrorViewHolder
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.diffUtilResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchListAdapter: RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {
    var items: MutableList<SearchDataSet>? = null
    fun submitList(newList: MutableList<SearchDataSet>?, diffResult: DiffUtil.DiffResult) {
        if (newList.isNullOrEmpty()) return
        items?.let {
            it.clear()
            it.addAll(newList)
        } ?: run {
            items = newList
        }
        diffResult.dispatchUpdatesTo(this)
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
        holder.onBindView(items?.getOrNull(position)?.data)
    }
}