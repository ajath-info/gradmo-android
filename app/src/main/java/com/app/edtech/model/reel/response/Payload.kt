package com.app.hihlo.model.reel.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Payload(
    val pagination: Pagination?=null,
    val reels: MutableList<Reel> ?=null
): Parcelable