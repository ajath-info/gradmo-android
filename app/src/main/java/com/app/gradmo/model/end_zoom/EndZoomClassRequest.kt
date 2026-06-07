package com.app.gradmo.model.end_zoom

data class EndZoomClassRequest(
    val batchId: Int,
    val liveClassId: Int,
    val meetingNumber: Long
)