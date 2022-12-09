package com.example.beeptalk.models

data class ThreadComment(
    var id: String? = null,
    val threadId: String? = null,
    val body : String? = null,
    val replyTo: String = "Default",
    var upvote : ArrayList<String> = arrayListOf<String>(),
    var downvote: ArrayList<String> = arrayListOf<String>(),
    var upDownFlag : Int = 0
) {

    public fun getTotalVotes(): Int {
        return upvote.size - downvote.size
    }
}

