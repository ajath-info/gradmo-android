package com.app.edtech.ui.signup.model

data class ResetPasswordRequest(
    var mobile: String?=null,
    var password: String?=null,
    var confirm_password: String?=null
)

data class ChangePasswordRequest(
    var oldPassword: String?=null,
    var newPassword: String?=null,
    var confirmedNewPassword: String?=null
)

