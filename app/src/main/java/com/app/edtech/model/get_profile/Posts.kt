package com.app.hihlo.model.get_profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Posts(
    val data: MutableList<Data> = mutableListOf(),
    val pagination: Pagination = Pagination()
): Parcelable