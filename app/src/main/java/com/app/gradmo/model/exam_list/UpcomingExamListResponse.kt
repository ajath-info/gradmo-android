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
            val id: String? = null,
            val adminId: String? = null,
            val name: String? = null,
            val type: String? = null,
            val format: String? = null,

            val batchId: String? = null,
            val batchName: String? = null,

            val cardImageUrl: String? = null,

            val totalQuestion: String? = null,
            val timeDuration: String? = null,

            val scheduledDate: String? = null,
            val scheduledTime: String? = null,
            val completeBy: String? = null,

            val totalMarks: String? = null,
            val markingPercent: String? = null,

            val examTypeLabel: String? = null,
            val statusLabel: String? = null,
            val ctaLabel: String? = null,

            val resultId: String? = null,
            val examId: String? = null,
            val paperName: String? = null,

            val date: String? = null,
            val startTime: String? = null,
            val submitTime: String? = null,
            val timeTaken: String? = null,

            val assignedDate: String? = null,

            val percentage: String? = null,
            val remarks: String? = null,

            val attemptedQuestion: String? = null,
            val correctAnswers: String? = null,
            val wrongAnswers: String? = null,

            val score: String? = null,
            val scoreLabel: String? = null,

            val addedBy: String? = null,
            val addedAt: String? = null
        ) : Parcelable
    }
}