package com.example.playergroup.ui.scrollselector

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playergroup.databinding.ViewSelectorTextItemBinding

class SliderListAdapter: RecyclerView.Adapter<SliderItemViewHolder>() {
    var items: MutableList<String>? = null
        set(value) {
            value?.let {
                field = it
                notifyDataSetChanged()
            } ?: run {
                return
            }
        }

    override fun getItemCount(): Int = items?.size ?: 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderItemViewHolder =
        SliderItemViewHolder(ViewSelectorTextItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: SliderItemViewHolder, position: Int) {
        holder.onBindView(items?.getOrNull(position))
    }
}

class SliderItemViewHolder(val binding: ViewSelectorTextItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun onBindView(data: String?) {
        binding.txt.text = data ?: ""
    }
}