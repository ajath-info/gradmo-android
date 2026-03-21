package com.app.hihlo.model.get_reel_comments.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Payload(
    val comments: List<Comment> = listOf()
):Parcelable