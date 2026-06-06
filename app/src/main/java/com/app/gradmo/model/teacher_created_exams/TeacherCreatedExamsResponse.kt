package com.app.gradmo.model.teacher_created_exams

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class TeacherCreatedExamsResponse(
    val `data`: Data,
    val msg: String,
    val status: String
) {
    data class Data(
        val batch_id: Int,
        val exams: List<Exam>,
        val pagination: Pagination
    ) {
        @Parcelize
        data class Exam(
            val addedAt: String,
            val addedBy: String,
            val adminId: String,
            val batchId: String,
            val canDelete: Boolean,
            val canEdit: Boolean,
            val examTypeLabel: String,
            val format: String,
            val formatLabel: String,
            val hasStarted: Boolean,
            val id: String,
            val lockReason: String,
            val markingPercent: String,
            val name: String,
            val questionCount: Int,
            val questionIds: String,
            val scheduledDate: String,
            val scheduledTime: String,
            val submissionCount: Int,
            val timeDuration: String,
            val totalMarks: String,
            val totalQuestion: String,
            val type: String
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