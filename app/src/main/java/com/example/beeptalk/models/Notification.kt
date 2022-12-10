package com.example.beeptalk.models

import com.google.firebase.Timestamp
import java.util.*

data class Notification(
    var id: String? = null,
    val username: String? = null,
    val desc: String? = "Started following you",
    val date: Date = Timestamp.now().toDate(),
)
