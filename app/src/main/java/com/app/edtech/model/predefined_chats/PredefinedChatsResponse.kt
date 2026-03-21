package com.app.hihlo.model.predefined_chats

data class PredefinedChatsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: List<Payload>,
    val status: Int
) {
    data class Payload(
        val id: Int?=null,
        val user_id: String?=null,
        val predefined_chat: String?=null,
        val predefined_chat_id: String?=null,
        val message: String?=null,
    )
}