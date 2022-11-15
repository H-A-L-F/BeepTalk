package com.example.beeptalk.parcel

import android.os.Parcelable
import com.google.firebase.Timestamp
import java.util.*
import kotlinx.parcelize.Parcelize

@Parcelize
class ThreadID(
    var id: String,
    val body : String,
    val stitch : String?,
    var upvote : Int,
    var downvote : Int,
    val createdAt : Date
): Parcelable