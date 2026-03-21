package com.app.hihlo.model.login.response

data class Payload(
    val authToken: String?=null,
    var dob: String?=null,
    var email: String?=null,
    val fullName: String?=null,
    var name: String?=null,
    var profileImage: String?=null,
    var phone: String?=null,
    var userId: Int?=null,
    val isCreator: Int?=null,
    var audio_call_charges: Int?=null,
    var video_call_charges: Int?=null,
    var city: String?=null,
    var country: String?=null,
    val S3Details:S3Detail?=null,
    val RAZOR_PAY_DETAILS:RazorPayDetails?=null,
    val AGORA_DETAILS:AgoraDetails?=null,
    val AWS_CDN_URL: String?=null,
    var userName:String?=null,
    var username: String?=null
)
data class RazorPayDetails(var RAZOR_PAYID: String?=null, var RAZOR_PAY_SECRET: String?=null)
data class AgoraDetails(var AGORA_APP_ID: String?=null, var AGORA_APP_CERTIFICATE: String?=null)