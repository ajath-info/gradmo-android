package com.app.edtech.network_call.repository

import com.app.edtech.model.login.request.LoginRequest
import com.app.edtech.model.profile.request.UpdateProfileRequest
import com.app.edtech.network_call.RetrofitBuilder
import com.app.edtech.ui.signup.model.ChangePasswordRequest
import com.app.edtech.ui.signup.model.ResetPasswordRequest
import com.app.hihlo.ui.signup.model.SignUp

class ApiRepository {
    private val service = RetrofitBuilder.apiService

    suspend fun loginApi(request: LoginRequest) = service.login(request)
    suspend fun signupApi(request: LoginRequest) = service.signup(request)
    suspend fun updateProfileApi(request: UpdateProfileRequest, accessToken: String) = service.updateProfile(request, accessToken)
    suspend fun sendLoginOtpApi(request: LoginRequest) = service.sendLoginOtp(request)

    suspend fun resetPassword(request: ResetPasswordRequest) = service.resetPassword(request)

    suspend fun changePassword(token:String,request: ChangePasswordRequest) = service.changePassword(token,request)

    suspend fun verifyLoginOtp(mobile: String,otp:String, userType:String) = service.verifyLoginOtp(mobile, otp, userType)

    suspend fun registerUser(model: SignUp) = service.registerUser(model)



}