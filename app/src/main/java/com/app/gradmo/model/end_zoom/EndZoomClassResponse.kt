package com.app.gradmo.model.end_zoom

data class EndZoomClassResponse(
    val `data`: Data,
    val msg: String,
    val status: Boolean
) {
    data class Data(
        val batchId: Int,
        val liveClassId: Int,
        val meetingNumber: Long
    )
}