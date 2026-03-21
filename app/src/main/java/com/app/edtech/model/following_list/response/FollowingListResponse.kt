package com.app.hihlo.model.following_list.response

data class FollowingListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)