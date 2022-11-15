package com.example.beeptalk.model

data class User(var name: String, var username: String, var email: String, var password: String, var profilePicture: String)

public const val USER_COLLECTION = "users"
public const val USER_NAME_COLUMN = "name"
public const val USER_USERNAME_COLUMN = "username"
public const val USER_EMAIL_COLUMN = "email"
public const val USER_PASSWORD_COLUMN = "password"
public const val USER_PROFILE_PICTURE_COLUMN = "profile_picture"
