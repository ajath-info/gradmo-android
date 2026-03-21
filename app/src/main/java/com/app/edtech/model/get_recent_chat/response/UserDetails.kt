package com.app.hihlo.model.get_recent_chat.response

data class UserDetails(
    val id: Int,
    val isCreator: Int,
    val isStoryUploaded: Int,
    val myStory: MyStory,
    val name: String,
    val onlineStatus: String,
    val profileImage: String,
    val username: String
)