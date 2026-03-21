package com.app.hihlo.model.generate_agora_token.response

data class IncomingCall(
    val callerId: Int,
    val callerName: String,
    val callerToken: String,
    val calleeToken: String,
    val channelName: String,
    val userId: String
)