package com.app.edtech.model.institute_list.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class InstituteListResponse(
    val batchId: Any,
    val institutes: List<Institute>,
    val msg: String,
    val orderField: String,
    val orderType: String,
    val pagination: Pagination,
    val referenceLatitude: Double,
    val referenceLongitude: Double,
    val status: String
) {
    @Parcelize
    data class Institute(
        val address: String?=null,
        val city: String?=null,
        val country: String?=null,
        val distanceKm: String?=null,
        val email: String?=null,
        val image: String?=null,
        val imageUrl: String?=null,
        val instituteCode: String?=null,
        val instituteId: Int?=null,
        val instituteLatitude: Double?=null,
        val instituteLongitude: Double?=null,
        val mobile: String?=null,
        val name: String?=null,
        val pincode: String?=null,
        val role: Int?=null,
        val schoolCollegeName: String?=null,
        val state: String?=null,
        val teachEducation: String?=null,
        val userType: String?=null
    ):Parcelable

    data class Pagination(
        val limit: Int,
        val page: Int,
        val total: Int,
        val totalPages: Int,
        val totalRecords: Int
    )
}