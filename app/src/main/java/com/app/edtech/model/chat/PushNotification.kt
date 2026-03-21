package com.app.hihlo.model.chat

data class PushNotification(
    val message: MessageData
)

data class MessageData(
    val token: String,  // Receiver's FCM Device Token
//    val notification: NotificationData,
    val data: Map<String, String>

)