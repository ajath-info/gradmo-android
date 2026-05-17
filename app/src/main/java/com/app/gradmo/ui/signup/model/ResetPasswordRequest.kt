package com.app.gradmo.ui.signup.model

data class ResetPasswordRequest(
    var mobile: String?=null,
    var password: String?=null,
    var confirm_password: String?=null,
    var user_type: String?=null,
)

data class ChangePasswordRequest(
    var oldPassword: String?=null,
    var newPassword: String?=null,
    var confirmedNewPassword: String?=null
)

