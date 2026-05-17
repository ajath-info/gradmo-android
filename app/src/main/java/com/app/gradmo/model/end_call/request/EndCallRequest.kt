package com.app.hihlo.model.end_call.request

data class EndCallRequest(val callId: String?=null, val status: String,val receiverId:String)
