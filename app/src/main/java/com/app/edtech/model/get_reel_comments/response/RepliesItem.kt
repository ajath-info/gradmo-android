package com.app.hihlo.model.get_reel_comments.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Replies(
    val created_at: String,
    val id: Int,
    val reply: String,
    val updated_at: String,
    val user: UserX
):Parcelable