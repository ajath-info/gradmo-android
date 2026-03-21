package com.app.hihlo.model.home.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyStory(
    val id: String? = null,
    val url: String? = null,
    val created_at: String? = null,
    val asset_type: String? = null,
    val seen_count: Int? = null
) : Parcelable
