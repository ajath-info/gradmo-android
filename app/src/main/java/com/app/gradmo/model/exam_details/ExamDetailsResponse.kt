package com.app.gradmo.model.exam_details

data class ExamDetailsResponse(
    val exam: Exam,
    val message: String,
    val status: String
) {
    data class Exam(
        val addedAt: String,
        val addedBy: String,
        val adminId: String,
        val batchId: String,
        val completeBy: String,
        val examTypeLabel: String,
        val format: String,
        val formatLabel: String,
        val id: String,
        val markingPercent: String,
        val name: String,
        val questionDetails: List<QuestionDetail>,
        val questionIds: String,
        val scheduledDate: String,
        val scheduledTime: String,
        val status: String,
        val timeDuration: String,
        val totalMarks: String,
        val totalQuestion: String,
        val type: String
    ) {
        data class QuestionDetail(
            val answer: String,
            val chapterId: String,
            val id: String,
            val options: String,
            val question: String,
            val questionImage: String,
            val questionImageUrl: String,
            val questionMask: String,
            val subjectId: String
        )
    }
}