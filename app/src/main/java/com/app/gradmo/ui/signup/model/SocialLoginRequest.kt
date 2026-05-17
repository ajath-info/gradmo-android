package com.app.hihlo.ui.signup.model

data class SocialLoginRequest(
    var socialId: String?=null,
    var socialType: String?=null,
    var deviceToken: String?=null,
    var deviceType: String?=null,
    var voipDeviceToken: String?=null
)