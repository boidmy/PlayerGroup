package com.example.playergroup.data

data class UserInfo(
    var email: String? = null,  // 이메일
    var name: String? = null,   // 이름
    var height: String? = null, // 키
    var weight: String? = null, // 몸무게
    var position: String? = null, // 포지션
    var age: String? = null,    // 생년월일
    var sex: String? = null,   // 성별
    var img: String? = null,    // 프로필 이미지
    var comment: String? = null,    // 하고싶은 말

    var clubAdmin: MutableList<String>? = null,   // 내가 만든 동호회 이름 ( 프라이머리 키 )
    var clubInvolved: MutableList<String>? = null // 가입된 동호회 이름 ( 프라이머리 키 )
) {
    fun isEmptyData() = (
        age.isNullOrEmpty() ||
        height.isNullOrEmpty() ||
        name.isNullOrEmpty() ||
        weight.isNullOrEmpty() ||
        position.isNullOrEmpty() ||
        sex.isNullOrEmpty() ||
        img.isNullOrEmpty())
}