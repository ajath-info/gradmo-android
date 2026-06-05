package com.app.gradmo.model.add_homework

data class AddHomeworkResponse(
    val `data`: Data,
    val message: String,
    val msg: String,
    val status: String
) {
    data class Data(
        val attachment: String,
        val attachmentUrl: String,
        val batchId: Int,
        val date: String,
        val id: Int,
        val subjectId: Int
    )
}