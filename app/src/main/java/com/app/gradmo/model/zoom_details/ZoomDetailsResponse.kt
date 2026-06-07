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
            val batchId: Int?=null,
            val duration: Int?=null,
            val hostId: String?=null,
            val inAppOnly: Int?=null,
            val password: String?=null,
            val startTime: String?=null,
            val timezone: String?=null,
            val topic: String?=null,
            val zoomMeetingId: String?=null
        )
    }
}