package com.app.gradmo.model.batch_list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class BatchListResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val enrolled_batches: List<EnrolledBatche>?=null,
        val pagination: Pagination
    ) {
        @Parcelize
        data class EnrolledBatche(
            val batchImage: String?=null,
            val batchName: String?=null,
            val batch_id: Int?=null,
            val batch_type: Int?=null,
            val description: String?=null,
            val end_date: String?=null,
            val end_time: String?=null,
            val enrolled_at: String?=null,
            val enrollment_status: Int?=null,
            val instructor: String?=null,
            val logo: String?=null,
            val schedule: String?=null,
            val start_date: String?=null,
            val start_time: String?=null,
            val title: String?=null
        ): Parcelable

        data class Pagination(
            val limit: Int,
            val page: Int,
            val total: Int,
            val totalPages: Int,
            val totalRecords: Int
        )
    }
}