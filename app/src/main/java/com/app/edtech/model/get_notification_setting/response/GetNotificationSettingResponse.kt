package com.app.hihlo.model.get_notification_setting.response

data class GetNotificationSettingResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        var audioCall: String,
        var followers: String,
        var following: String,
        var generalNotification: String,
        var payments: String,
        var videoCall: String
    )
}