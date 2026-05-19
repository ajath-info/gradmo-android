package com.app.gradmo.model.exam_list

import com.app.gradmo.model.exam_list.UpcomingExamListResponse.Data.UpcomingExam

data class ExamDashboardResponse(
    val `data`: Data,
    val msg: String?=null,
    val message: String?=null,
    val status: String
) {
    data class Data(
        val batch: Batch,
        val completedExams: List<UpcomingExam>,
        val upcomingExams: List<UpcomingExam>
    ) {
        data class Batch(
            val batchId: Int,
            val batchName: String,
            val cardImageUrl: String
        )
    }
}