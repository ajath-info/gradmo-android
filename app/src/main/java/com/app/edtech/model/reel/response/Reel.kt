package com.app.hihlo.model.reel.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reel(
    val assetType: String,
    val assetUrl: String,
    val caption: String,
    var commentsCount: Int,
    var isLiked: Int,
    var isFollowing: Int,
    val createdAt: String,
    val creator: Creator,
    val creatorId: Int,
    val id: Int,
    var likesCount: Int,
    val status: String,
    val updatedAt: String,
    var lastPlaybackPosition: Long = 0L
): Parcelable