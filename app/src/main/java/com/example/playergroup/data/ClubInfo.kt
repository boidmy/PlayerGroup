package com.example.playergroup.data

data class ClubInfo (
    var clubAdmin: String? = null,
    var clubImg: String? = null,
    var clubImgFullUrl: String? = null, // Storage 에서 다운받아온 Real Path
    var clubName: String? = null
)