package com.example.beeptalk.models

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

data class User(
    var uid: String,
    var name: String,
    var username: String,
    var email: String,
    var profilePicture: String = "",
    var bio: String = "",
    var following: ArrayList<String> = arrayListOf(),
    var followers: ArrayList<String> = arrayListOf(),
) {
}

const val USER_COLLECTION = "users"
const val USER_NAME_FIELD = "name"
const val USER_USERNAME_FIELD = "username"
const val USER_EMAIL_FIELD = "email"
const val USER_PROFILE_PICTURE_FIELD = "profilePicture"
const val USER_FOLLOWING_FIELD = "following"
const val USER_FOLLOWERS_FIELD = "followers"
const val USER_BIO_FIELD = "bio"

fun saveUserToFireStore(
    name: String,
    username: String,
    email: String,
    documentId: String,
    context: Context
) {
    val user = User(
        documentId,
        name,
        username,
        email,
        "https://firebasestorage.googleapis.com/v0/b/beeptalk-35de8.appspot.com/o/User%2FDefault%20Profile%20Picture%2Fcat_user.jpg?alt=media&token=c3aa7ba4-cd6c-44e5-8a8b-71b3dee98a8b"
    )

    FirebaseFirestore.getInstance().collection(USER_COLLECTION).document(documentId)
        .set(user)
        .addOnSuccessListener {
            Toast.makeText(context, "Account registered successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
}