package com.app.hihlo.model.get_profile

import android.os.Parcelable
import com.app.hihlo.model.get_recent_chat.response.MyStory
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetailsX(
    val about: String?=null,
    val city: String?=null,
    val country: String?=null,
    val dob: String?=null,
    val email: String?=null,
    val followers_count: Int?=null,
    val following_count: Int?=null,
    val gender: String?=null,
    val id: Int?=null,
    val interest_name: String?=null,
    val is_verified: String?=null,
    val name: String?=null,
    val phone: String?=null,
    val posts_count: Int?=null,
    val blockStatus: BlockStatus?=null,
    val profile_image: String?=null,
    var profileImage: String?=null,
    val reels_count: Int?=null,
    val is_following: Int?=null,
    val is_seen: Int?=null,
    val role: String?=null,
    var user_live_status: String?=null,
    val isCreator: Int?=null,
    val creatorStatus: String?=null,
    val isStoryUploaded: Int?=null,
    val is_story_uploaded: Int?=null,
    val audio_call_charges: Int?=null,
    val video_call_charges: Int?=null,
    val story: MyStory?=null,
    val myStory: MyStory?=null,
    val notificationSettings: NotificationSettings?=null,
    val username: String?=null
):Parcelable

@Parcelize
data class NotificationSettings(
    val general_notification: String?=null,
    val audio_call: String?=null,
    val video_call: String?=null,
    val followers: String?=null,
    val payments: String?=null,
    val following: String?=null,
    val status: String?=null,
):Parcelable