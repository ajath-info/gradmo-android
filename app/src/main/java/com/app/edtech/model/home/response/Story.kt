package com.app.hihlo.model.home.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val asset_type: String?=null,
    val asset_url: String?=null,
    val created_at: String?=null,
    val id: Int?=null,
    val is_seen: Int?=null,
    val user_id: Int?=null,
    val userDetail: UserDetails?=null,
):Parcelable