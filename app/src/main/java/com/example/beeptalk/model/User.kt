package com.example.beeptalk.model

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class User(
    var uid: String,
    var name: String,
    var username: String,
    var email: String,
    var profilePicture: String = ""
) {
    fun createHashMap(): MutableMap<String, String> {
        val user: MutableMap<String, String> = HashMap()
        user[USER_NAME_FIELD] = name
        user[USER_USERNAME_FIELD] = username
        user[USER_EMAIL_FIELD] = email
        user[USER_PROFILE_PICTURE_FIELD] = profilePicture

        return user
    }


}

const val USER_COLLECTION = "users"
const val USER_NAME_FIELD = "name"
const val USER_USERNAME_FIELD = "username"
const val USER_EMAIL_FIELD = "email"
const val USER_PROFILE_PICTURE_FIELD = "profile_picture"

fun saveUserToFireStore(
    name: String,
    username: String,
    email: String,
    documentId: String,
    context: Context
) {
    val user = User(documentId, name, username, email, "")

    FirebaseFirestore.getInstance().collection(USER_COLLECTION).document(documentId)
        .set(user.createHashMap())
        .addOnSuccessListener {
            Toast.makeText(context, "Account registered successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
}