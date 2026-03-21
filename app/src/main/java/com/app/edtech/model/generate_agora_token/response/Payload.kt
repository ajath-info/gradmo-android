package com.app.hihlo.model.generate_agora_token.response

data class Payload(
    val agoraToken: String,
    val callerToken: String,
    val calleeToken: String,
    val calleeId: String,
    val callerId: Int,
    val uid: Int,
    val callType: String,
    val channelName: String,
    val id: String,
    val incomingCall: IncomingCall,
    val type: String
)