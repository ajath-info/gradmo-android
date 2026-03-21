package com.app.hihlo.model.following_list.response

data class Payload(
    val followingList: List<Following>,
    val followersList: List<Following>,
    val userDetails: UserDetails
)