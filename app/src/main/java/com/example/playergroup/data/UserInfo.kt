package com.example.playergroup.data

data class UserInfo(
    var email: String? = null,  // 이메일
    var name: String? = null,   // 이름
    var height: String? = null, // 키
    var weight: String? = null, // 몸무게
    var position: String? = null, // 포지션
    var age: String? = null,    // 생년월일
    var addr: String? = null,   // 거주지역
    var img: String? = null,    // 프로필 이미지
    var hope: String? = null    // 하고싶은 말
)