package com.app.gradmo.model.zoom_details

data class ZoomDetailsResponse(
    val `data`: Data,
    val msg: String,
    val status: String
) {
    data class Data(
        val zoom: Zoom
    ) {
        data class Zoom(
            val batchId: Int,
            val duration: Int,
            val hostId: String,
            val inAppOnly: Int,
            val password: String,
            val startTime: Any,
            val timezone: String,
            val topic: String,
            val zoomMeetingId: String
        )
    }
}