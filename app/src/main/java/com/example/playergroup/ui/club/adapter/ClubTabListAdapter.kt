package com.example.playergroup.ui.club.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.example.playergroup.R
import com.example.playergroup.data.ClubTabInfo
import com.example.playergroup.databinding.ViewClubTabItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.click
import com.example.playergroup.util.getSpannedColorText
import com.example.playergroup.util.viewBinding

class ClubTabListAdapter(
    private val clickCallback: ((ViewTypeConst) -> Unit)
) : ListAdapter<ClubTabInfo, BaseViewHolder<ViewBinding>>(
    object : DiffUtil.ItemCallback<ClubTabInfo>() {
        override fun areItemsTheSame(oldItem: ClubTabInfo, newItem: ClubTabInfo): Boolean = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: ClubTabInfo, newItem: ClubTabInfo): Boolean = oldItem == newItem
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> =
        ClubTabItemViewHolder(parent) as BaseViewHolder<ViewBinding>
    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        holder.onBindView(currentList[position])
    }

    private fun select(index: Int) {
        val data = currentList.map { it.copy() }.toMutableList()

        val selectedItem = data.indexOfFirst { it.isSelected } ?: -1  // true 된 아이템을 찾는다.
        if (selectedItem == -1) {
            data.getOrNull(index)?.isSelected = true // 없으면 그냥 선택한 아이템 Select
        } else {
            if (selectedItem == index) {
                // 같은걸 눌렀을 때는 ... 아무것도 안함.
            } else {
                data.getOrNull(selectedItem)?.isSelected = false   // 같은게 아닐 경우 서로 바꿈.
                data.getOrNull(index)?.isSelected = true
                clickCallback.invoke(data[index].tabType)
            }
        }
        submitList(data)
    }

    private inner class ClubTabItemViewHolder(parent: ViewGroup) :
        BaseViewHolder<ViewClubTabItemBinding>(parent.viewBinding(ViewClubTabItemBinding::inflate)) {
        override fun onBindView(data: Any?) {
            (data as? ClubTabInfo)?.let { clubTabInfo ->
                binding.name.text = clubTabInfo.name
                binding.name.text = getSpannedColorText(
                    origin = clubTabInfo.name,
                    changed = clubTabInfo.name,
                    color =
                    if (clubTabInfo.isSelected)
                        itemView.context.getColor(R.color.colorPrimaryText)
                    else
                        itemView.context.getColor(R.color.colorSecondaryText)
                    ,
                    bold = clubTabInfo.isSelected )
                binding.checkedView.visibility = if (clubTabInfo.isNewFeed) View.VISIBLE else View.GONE
                itemView click {
                    select(adapterPosition)
                }
            }
        }
    }
}