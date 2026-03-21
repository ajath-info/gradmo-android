package com.app.hihlo.model.get_recent_chat.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyStory(
    val asset_type: String,
    val id: Int,
    val seen_count: Int,
    val url: String
): Parcelable