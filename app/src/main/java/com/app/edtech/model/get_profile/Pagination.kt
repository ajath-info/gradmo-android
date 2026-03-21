package com.app.hihlo.model.get_profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pagination(
    val current_page: Int?=null,
    val per_page: Int?=null,
    val total: Int?=null,
    val total_pages: Int?=null
): Parcelable