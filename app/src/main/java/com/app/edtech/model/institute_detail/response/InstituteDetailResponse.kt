package com.app.edtech.model.institute_detail.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class InstituteDetailResponse(
    val batches: List<Batche>,
    val institute: Institute,
    val msg: String,
    val rating: Rating,
    val reviews: List<Review>,
    val status: String
) {
    @Parcelize
    data class Batche(
        val admin_id: String?=null,
        val batch_image: String?=null,
        val batch_mode: String?=null,
        val batch_name: String?=null,
        val batch_offer_price: String?=null,
        val batch_price: String?=null,
        val batch_type: String?=null,
        val cat_id: String?=null,
        val description: String?=null,
        val end_date: String?=null,
        val end_time: String?=null,
        val id: String?=null,
        val institute_id: String?=null,
        val no_of_student: String?=null,
        val pay_mode: String?=null,
        val start_date: String?=null,
        val start_time: String?=null,
        val status: String?=null,
        val sub_cat_id: String?=null
    ):Parcelable

    data class Institute(
        val email: String,
        val image: String,
        val imageUrl: String,
        val instituteId: Int,
        val mobile: String,
        val name: String,
        val parentId: Int,
        val pincode: String,
        val role: Int,
        val teachEducation: String,
        val teachGender: String,
        val updatedAt: String,
        val userType: String
    )

    data class Rating(
        val averageRating: Int,
        val totalReviews: Int
    )

    data class Review(
        val approvedBy: Int,
        val createdAt: String,
        val id: Int,
        val instituteId: Int,
        val msg: String,
        val rating: Int,
        val status: Int,
        val userId: Int,
        val userType: String
    )
}