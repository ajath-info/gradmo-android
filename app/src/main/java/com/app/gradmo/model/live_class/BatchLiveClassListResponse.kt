package com.app.gradmo.model.live_class

data class BatchLiveClassListResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val batch_id: Int,
        val liveClasses: List<LiveClasse>,
        val pagination: Pagination
    ) {
        data class LiveClasse(
            val batchId: Int,
            val chapterId: Int,
            val chapterName: String,
            val date: String,
            val endTime: String,
            val entryDateTime: String,
            val isBatchZoom: Int,
            val isLive: Int,
            val liveClassId: Int,
            val startTime: String,
            val subjectId: Int,
            val subjectName: String,
            val teacherId: Int,
            val teacherImage: String,
            val teacherImageUrl: String,
            val teacherName: String,
            val typeClass: Int,
            val typeLabel: String
        )

        data class Pagination(
            val limit: Int,
            val page: Int,
            val total: Int,
            val totalPages: Int,
            val totalRecords: Int
        )
    }
}