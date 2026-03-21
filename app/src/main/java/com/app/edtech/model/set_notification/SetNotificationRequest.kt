package com.app.hihlo.model.set_notification

data class SetNotificationRequest(
    val generalNotification: Int? = null,
    val audioCall: Int? = null,
    val videoCall: Int? = null,
    val payments: Int? = null,
    val follow: Int? = null,
    val following: Int? = null,
)
