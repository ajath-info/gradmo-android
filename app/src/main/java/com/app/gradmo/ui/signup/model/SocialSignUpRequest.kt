package com.app.hihlo.ui.signup.model

data class SocialSignUpRequest(
    var name:String?=null,
    var email:String?=null,
    var social_id:String?=null,
    var social_type:String?=null,
    var profile_image:String?=null,
    var deviceToken:String?=null,
    var deviceType:String?=null
)
