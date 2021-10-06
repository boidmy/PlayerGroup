package com.example.playergroup.ui.main

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.MainDataSet
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.ui.base.EmptyErrorViewHolder
import com.example.playergroup.ui.main.holder.*
import com.example.playergroup.util.ViewTypeConst

class MainListAdapter: RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {
    var items: List<MainDataSet>? = null
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