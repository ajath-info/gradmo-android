package com.app.hihlo.model.get_profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlockStatus(
    val blockId: Int,
    val canUnblock: Int,
    var iBlockedThem: Int,
    var isBlocked: Int,
    val theyBlockedMe: Int
): Parcelable