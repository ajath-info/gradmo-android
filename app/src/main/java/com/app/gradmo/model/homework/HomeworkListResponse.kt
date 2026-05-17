package com.app.gradmo.model.homework

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class HomeworkListResponse(
    val homeWork: List<HomeWork>,
    val msg: String,
    val pagination: Pagination,
    val status: String
) {
    @Parcelize
    data class HomeWork(
        val addedAt: String?=null,
        val adminId: String?=null,
        val attachment: String?=null,
        val attachmentUrl: String?=null,
        val batchId: String?=null,
        val date: String?=null,
        val description: String?=null,
        val id: String?=null,
        val name: String?=null,
        val subjectId: String?=null,
        val subjectName: String?=null,
        val teachGender: String?=null,
        val teacherId: String?=null
    ): Parcelable

    data class Pagination(
        val limit: Int,
        val page: Int,
        val total: Int,
        val totalPages: Int,
        val totalRecords: Int
    )
}