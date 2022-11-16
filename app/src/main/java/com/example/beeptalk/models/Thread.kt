package com.example.beeptalk.models

import com.google.firebase.Timestamp
import java.util.*

data class Thread(
    var id: String? = null,
    val body : String? = null,
    val stitch : String? = null,
    var upvote : Int = 0,
    var downvote : Int = 0,
    val createdAt : Date = Timestamp.now().toDate()
) {

    public fun getTotalVotes(): Int {
        return upvote - downvote
    }
}
