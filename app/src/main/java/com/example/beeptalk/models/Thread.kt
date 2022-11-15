package com.example.beeptalk.models

import com.google.firebase.Timestamp
import java.util.*

data class Thread(val body : String, val stitch : String? = null, val upvote : Int = 0, val downvote : Int = 0, val createdAt : Date = Timestamp.now().toDate()) {

    public fun getTotalVotes(): Int {
        return upvote - downvote
    }
}
