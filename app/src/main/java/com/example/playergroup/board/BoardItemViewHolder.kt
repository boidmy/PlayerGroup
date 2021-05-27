package com.example.playergroup.board

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
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

                val videoId = it.video
                if (!videoId.isNullOrEmpty()) {
                    video.visibility = View.VISIBLE
                    img.visibility = View.GONE
                    FirebaseStorage.getInstance().reference.child(videoId)
                        .downloadUrl
                        .addOnSuccessListener {
                            val mediaController = MediaController(itemView.context)
                            mediaController.setAnchorView(video)

                            video.setMediaController(mediaController)
                            video.setVideoURI(it)
                            video.requestFocus()
                            video.start()
                        }
                        .addOnFailureListener { Log.d("####", "Error > "+it.message) }
                }


                val boardId = it.img
                if (!boardId.isNullOrEmpty()) {
                    video.visibility = View.GONE
                    img.visibility = View.VISIBLE
                    FirebaseStorage.getInstance().reference.child(boardId)
                        .downloadUrl
                        .addOnSuccessListener {
                            Glide.with(context)
                                .load(it)
                                .into(img)
                        }
                        .addOnFailureListener { Log.d("####", "Error > "+it.message) }
                }

                val userId = it.userId
                if (!userId.isNullOrEmpty()) {
                    FirebaseStorage.getInstance().reference.child(userId)
                        .downloadUrl
                        .addOnSuccessListener {
                            Glide.with(context)
                                .load(it)
                                .into(userImg)
                        }
                        .addOnFailureListener { Log.d("####", "Error > "+it.message) }
                }
                userNm.text = data.userNm
            }
        } ?: run {
            return
        }
    }
}