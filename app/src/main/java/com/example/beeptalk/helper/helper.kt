package com.example.beeptalk.helper

fun generateOTP(): String {
    var OTP : String = ""
    for(i in 1..6) {
        val rnds = (0..9).random()
        OTP += rnds
    }

    return OTP
}