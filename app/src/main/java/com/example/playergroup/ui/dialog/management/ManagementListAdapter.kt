package com.example.playergroup.ui.dialog.management

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.playergroup.data.Landing
import com.example.playergroup.data.ManagementDataSet
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ViewManagementClubEmptyBinding
import com.example.playergroup.databinding.ViewManagementClubItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.LandingRouter
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.click
import com.example.playergroup.util.viewBinding

class ManagementListAdapter(
    private val callback: () -> Unit
): RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {

    private var items: MutableList<ManagementDataSet>? = null
    fun submitList(data: MutableList<ManagementDataSet>?) {
        items = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items?.size ?: 0
    override fun getItemViewType(position: Int): Int = items?.getOrNull(position)?.viewType?.ordinal ?: ViewTypeConst.EMPTY_ERROR.ordinal
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> =
        when(ViewTypeConst.values()[viewType]) {
            ViewTypeConst.MANAGEMENT_ITEM -> ItemViewHolder(parent)
            else -> EmptyItemViewHolder(parent)
        } as BaseViewHolder<ViewBinding>
    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        holder.onBindView(items?.getOrNull(position))
    }

    private inner class EmptyItemViewHolder(parent: ViewGroup):
        BaseViewHolder<ViewManagementClubEmptyBinding>(
            parent.viewBinding(ViewManagementClubEmptyBinding::inflate)
        ) {
        override fun onBindView(data: Any?) {
            (data as? ManagementDataSet)?.let {
                binding.emptyContent.text = it.emptyTxt
                itemView click {
                    LandingRouter.move(itemView.context, RouterEvent(Landing.CREATE_CLUB))
                    callback.invoke()
                }
            }
        }
    }

    private inner class ItemViewHolder(parent: ViewGroup) :
        BaseViewHolder<ViewManagementClubItemBinding>(
            parent.viewBinding(ViewManagementClubItemBinding::inflate)
        ) {
        override fun onBindView(data: Any?) {
            (data as? ManagementDataSet)?.let { managemant ->
                with(binding) {
                    clubName.text = managemant.clubName ?: ""
                    Glide.with(itemView.context)
                        .load(managemant.clubImg)
                        .into(img)
                }

                itemView click {
                    LandingRouter.move(itemView.context, RouterEvent(type = Landing.CLUB_MAIN, primaryKey = managemant.clubPrimaryKey))
                    callback.invoke()
                }
            }
        }
    }
}