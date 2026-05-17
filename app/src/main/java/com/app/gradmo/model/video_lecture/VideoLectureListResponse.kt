package com.app.gradmo.model.video_lecture

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class VideoLectureListResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val accessibleBatchIds: List<Int>,
        val batch_id: Int,
        val pagination: Pagination,
        val videoLectures: List<VideoLecture>
    ) {
        data class Pagination(
            val limit: Int,
            val page: Int,
            val total: Int,
            val totalPages: Int,
            val totalRecords: Int
        )

        @Parcelize
        data class VideoLecture(
            val addedAt: String?=null,
            val addedBy: String?=null,
            val adminId: String?=null,
            val batch: String?=null,
            val description: String?=null,
            val id: String?=null,
            val previewType: String?=null,
            val subject: String?=null,
            val title: String?=null,
            val topic: String?=null,
            val url: String?=null,
            val videoType: String?=null
        ): Parcelable
    }
}