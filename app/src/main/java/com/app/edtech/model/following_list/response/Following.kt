package com.app.hihlo.model.following_list.response

data class Following(
    val created_at: String,
    val follower_id: Int,
    val following_id: Int,
    val isFollowing: Int,
    val id: Int,
    val updated_at: String,
    val userDetails: UserDetails
)