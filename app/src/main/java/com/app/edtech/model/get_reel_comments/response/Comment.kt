package com.app.hihlo.model.get_reel_comments.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Comment(
    val comment: String,
    val created_at: String,
    val id: Int,
    val replies: List<Replies>,
    val updated_at: String,
    val user: User
):Parcelable