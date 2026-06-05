package com.app.gradmo.model.add_library

data class AddLibraryDataResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val batch_id: Int,
        val downloadUrl: String,
        val fileName: String,
        val id: Int,
        val subject: String,
        val title: String,
        val topic: String
    )
}