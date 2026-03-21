package com.app.hihlo.model.reel.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pagination(
    val currentPage: Int?=null,
    val pageSize: Int?=null,
    val totalItems: Int?=null,
    val totalPages: Int?=null
): Parcelable