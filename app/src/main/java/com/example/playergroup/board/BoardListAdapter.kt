package com.example.playergroup.board

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playergroup.data.BoardData

class BoardListAdapter: RecyclerView.Adapter<BoardItemViewHolder>() {
    var items: List<BoardData>? = null
        set(value) {
            value?.let {
                field = value
                notifyDataSetChanged()
            } ?: run {
                return
            }
        }
    override fun getItemCount(): Int = items?.size ?: 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardItemViewHolder = BoardItemViewHolder(parent)
    override fun onBindViewHolder(holder: BoardItemViewHolder, position: Int) {
        holder.onBindView(items?.getOrNull(position))
    }
}