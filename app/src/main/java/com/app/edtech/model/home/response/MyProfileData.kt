package com.app.hihlo.model.home.response

data class MyProfileData(
    val city: String,
    val country: String,
    val id: Int,
    val isCreator: Int,
    val isStoryUploaded: Int,
    val isVerified: Int,
    val myStory: MyStoryX,
    val name: String,
    val onlineStatus: String,
    val profileImage: String,
    val username: String
)