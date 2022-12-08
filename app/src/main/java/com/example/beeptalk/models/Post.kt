package com.example.beeptalk.models

import java.io.Serializable

data class Post(
    var id: String,
    var videoUrl: String,
    var title: String,
    var desc: String,
    var likes: ArrayList<String>,
    var comments: ArrayList<String>,
) {
    fun createHashMap(): HashMap<String, Serializable> {
        return hashMapOf(
            "videoUrl" to videoUrl,
            "title" to title,
            "desc" to desc,
            "likes" to likes,
            "comments" to comments,
        )
    }

}