package com.app.gradmo.model.create_zoom

data class CreateZoomResponse(
    val `data`: Data,
    val msg: String,
    val status: String
) {
    data class Data(
        val zoomMeetingId: String
    )
}