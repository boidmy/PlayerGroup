package com.example.playergroup.ui.club.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.ClubMemberDataSet
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.ui.base.EmptyErrorViewHolder
import com.example.playergroup.ui.club.holder.ClubMemberExpandItemViewHolder
import com.example.playergroup.ui.club.holder.ClubMemberInviteUserViewHolder
import com.example.playergroup.ui.club.holder.ClubMemberItemViewHolder
import com.example.playergroup.ui.club.holder.ClubTitleTextViewHolder
import com.example.playergroup.ui.main.holder.MainClubInfoViewHolder
import com.example.playergroup.ui.main.holder.MainMyInfoViewHolder
import com.example.playergroup.util.ViewTypeConst

class ClubMemberListAdapter: RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {

    var items: MutableList<ClubMemberDataSet>? = null

    fun submitList(newList: MutableList<ClubMemberDataSet>?, diffResult: DiffUtil.DiffResult) {
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
            ViewTypeConst.CLUB_MEMBER_USER -> ClubMemberItemViewHolder(parent)
            ViewTypeConst.CLUB_MEMBER_EXPAND -> ClubMemberExpandItemViewHolder(parent)
            ViewTypeConst.CLUB_TITLE_TEXT -> ClubTitleTextViewHolder(parent)
            else -> EmptyErrorViewHolder(parent)
        } as BaseViewHolder<ViewBinding>

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        holder.onBindView(items?.getOrNull(position))
    }

}