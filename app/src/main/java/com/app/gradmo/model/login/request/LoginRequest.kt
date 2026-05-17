package com.app.gradmo.model.login.request

data class LoginRequest(
    val name: String ?= null,
    val email: String ?= null,
    val username: String ?= null,
    val password: String ?= null,
    val user_type: String ?= null,
    val device_id: String ?= null,
    val device_token: String ?= null,
    val device_type: String ?= null,
    val mobile: String ?= null,
)