package com.app.hihlo.model.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class MessageStatus {
    NOT_SENT, SENT, DELIVERED, SEEN
}

@Parcelize
data class Messages(
    val sender: String? = "",
    val senderId: String? = "",
    val senderName: String? = "",
    val receiver: String? = "",
    val message: String? = "",
    var time: String? = "",
    var date: String? = "",
    val messageType: String? = "",
    val messageId: String? = "",
    val url: String? = "",
    val paid: String? = "0",
    val repliedMessage: String? = "0",
    val duration: String? = "",
//    var reaction:  Map<String, Reaction> = mapOf(),
    var statusSent: String = MessageStatus.SENT.name
): Parcelable {

    val id: String get() = "$sender-$receiver-$message-$time"
}

