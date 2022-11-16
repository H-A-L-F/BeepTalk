package com.example.beeptalk.model

data class User(
    var name: String,
    var username: String,
    var email: String,
    var password: String,
    var profilePicture: String
) {
    fun createHashMap(): MutableMap<String, String> {
        val user: MutableMap<String, String> = HashMap()
        user[USER_NAME_FIELD] = name
        user[USER_USERNAME_FIELD] = username
        user[USER_EMAIL_FIELD] = email
        user[USER_PASSWORD_FIELD] = password
        user[USER_PROFILE_PICTURE_FIELD] = profilePicture

        return user
    }
}

const val USER_COLLECTION = "users"
const val USER_NAME_FIELD = "name"
const val USER_USERNAME_FIELD = "username"
const val USER_EMAIL_FIELD = "email"
const val USER_PASSWORD_FIELD = "password"
const val USER_PROFILE_PICTURE_FIELD = "profile_picture"
