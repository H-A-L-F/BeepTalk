package com.example.beeptalk.models

data class ThreadComment(
    var id: String? = null,
    val threadId: String? = null,
    val body : String? = null,
    var upvote : Int = 0,
    var downvote: Int = 0
) {

    public fun getTotalVotes(): Int {
        return upvote - downvote
    }
}

