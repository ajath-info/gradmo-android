package com.app.hihlo.model.following_list.response

data class UserDetails(
    val city: String,
    val country: String,
    val profile_image: String,
    val user_live_status: String,
    val username: String,
    val isCreator: Int,
    val name: String
)