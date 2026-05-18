package com.app.gradmo.model.exam_list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class UpcomingExamListResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val batch_id: Int,
        val pagination: Pagination,
        val upcomingExams: List<UpcomingExam>
    ) {
        data class Pagination(
            val limit: Int,
            val page: Int,
            val total: Int,
            val totalPages: Int,
            val totalRecords: Int
        )

        @Parcelize
        data class UpcomingExam(
            val addedAt: String,
            val addedBy: String,
            val adminId: String,
            val batchId: String,
            val completeBy: String,
            val examTypeLabel: String,
            val format: String,
            val id: String,
            val markingPercent: String,
            val name: String,
            val scheduledDate: String,
            val scheduledTime: String,
            val timeDuration: String,
            val totalMarks: String,
            val totalQuestion: String,
            val type: String
        ): Parcelable
    }
}