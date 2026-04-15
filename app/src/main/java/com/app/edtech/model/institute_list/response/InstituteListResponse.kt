package com.app.edtech.model.institute_list.response

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
    data class Institute(
        val address: String,
        val city: String,
        val country: String,
        val distanceKm: Any,
        val email: String,
        val image: String,
        val imageUrl: String,
        val instituteCode: String,
        val instituteId: Int,
        val instituteLatitude: Double,
        val instituteLongitude: Double,
        val mobile: String,
        val name: String,
        val pincode: String,
        val role: Int,
        val schoolCollegeName: String,
        val state: String,
        val teachEducation: String,
        val userType: String
    )

    data class Pagination(
        val limit: Int,
        val page: Int,
        val total: Int,
        val totalPages: Int,
        val totalRecords: Int
    )
}