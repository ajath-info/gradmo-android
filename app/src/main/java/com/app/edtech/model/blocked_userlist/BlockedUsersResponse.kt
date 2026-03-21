package com.app.hihlo.model.blocked_userlist

import com.app.hihlo.model.following_list.response.UserDetails

data class BlockedUsersResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val blockedUsers: List<BlockedUser>
    ) {
        data class BlockedUser(
            val blockReasonId: Int,
            val blockedUserId: String,
            val createdAt: String,
            val id: Int,
            val userDetails: UserDetails
        )
    }
}