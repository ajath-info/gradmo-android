package com.app.hihlo.model.home.response

data class Payload(
    val is_story_uploaded:Int,
    val unreadNotificationCount:Int,
    val my_story:MyStory?=null,
    val pagination:Pagination,
    val filters:Filters,
    val posts: List<Post>,
    val stories: List<Story>,
    val myProfile: MyProfileData
)