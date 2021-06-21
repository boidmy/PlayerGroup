package com.example.playergroup.data

data class LoginResultCallback (
    var isSuccess: Boolean,
    var landingType: Landing = Landing.MAIN
)