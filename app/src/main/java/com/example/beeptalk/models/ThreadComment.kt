package com.example.beeptalk.models

data class ThreadComment(
    val body : String = "",
    var upvote : Int = 0,
    var downvote: Int = 0
) {

    public fun getTotalVotes(): Int {
        return upvote - downvote
    }
}

