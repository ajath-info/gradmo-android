package com.app.edtech.model.library_list

data class LibraryListResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val batch_id: Int,
        val library: List<Library>,
        val pagination: Pagination
    ) {
        data class Library(
            val addedAt: String,
            val downloadUrl: String,
            val fileName: String,
            val fileSize: String,
            val fileSizeBytes: Int,
            val id: Int,
            val subject: String,
            val title: String,
            val topic: String
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