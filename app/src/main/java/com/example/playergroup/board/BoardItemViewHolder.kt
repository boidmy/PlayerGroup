package com.example.playergroup.board

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playergroup.R
import com.example.playergroup.data.BoardData
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.view_board_item.view.*

class BoardItemViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.view_board_item, parent, false)
) {
    fun onBindView(data: BoardData?) {
        data?.let {
            with (itemView) {
                Glide.with(context)
                    .load(data.img)
                    .into(img)

                val userId = it.userId
                if (!userId.isNullOrEmpty()) {
                    FirebaseStorage.getInstance().reference.child(userId)
                        .downloadUrl
                        .addOnSuccessListener {
                            Glide.with(context)
                                .load(it)
                                .into(userImg)
                        }
                        .addOnFailureListener {
                            Log.d("####", "Error > "+it.message)
                        }
                }
                userNm.text = data.userNm
            }
        } ?: run {
            return
        }
    }
}