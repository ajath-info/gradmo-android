package com.app.hihlo.model.notification.response

data class GetNotificationListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: List<Payload>,
    val status: Int
) {
    data class Payload(
        val created_at: String,
        val device_token: String,
        val device_type: String,
        val id: Int,
        val message: String,
        val read_status: String,
        val profile_image: String,
        val status: String,
        val title: String,
        val updated_at: String,
        val notification_type: String,
        val user_id: Int
    )
}