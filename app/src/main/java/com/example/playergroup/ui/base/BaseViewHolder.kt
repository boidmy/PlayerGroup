package com.example.playergroup.ui.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.databinding.ViewEmptyErrorBinding
import com.example.playergroup.util.viewBinding

open class BaseViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    open fun onBindView(data: Any?) {}
}

class EmptyErrorViewHolder(parent: ViewGroup): BaseViewHolder<ViewEmptyErrorBinding>(parent.viewBinding(ViewEmptyErrorBinding::inflate))