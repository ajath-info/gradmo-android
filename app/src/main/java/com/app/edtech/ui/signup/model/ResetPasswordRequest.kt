package com.app.hihlo.ui.signup.model

data class ResetPasswordRequest(
    var email: String?=null,
    var newPassword: String?=null,
    var confirmPassword: String?=null
)

data class ChangePasswordRequest(
    var oldPassword: String?=null,
    var newPassword: String?=null,
    var confirmedNewPassword: String?=null
)

