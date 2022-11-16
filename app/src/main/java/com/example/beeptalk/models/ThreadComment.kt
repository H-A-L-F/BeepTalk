package com.example.beeptalk.models

data class ThreadComment(
    var id: String = "",
    val threadId: String = "",
    val body : String = "",
    var upvote : Int = 0,
    var downvote: Int = 0
) {

    public fun getTotalVotes(): Int {
        return upvote - downvote
    }
}

