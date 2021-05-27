package com.example.playergroup.data

import com.google.gson.annotations.SerializedName

data class BoardData(
    @SerializedName("img") var img: String? = null,
    @SerializedName("video") var video: String? = null,
    @SerializedName("post") var post: String? = null,
    @SerializedName("test") var test: String? = null,
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("userNm") var userNm: String? = null
)