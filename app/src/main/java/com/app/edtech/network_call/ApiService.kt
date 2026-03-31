package com.app.edtech.network_call

import com.app.edtech.model.login.request.LoginRequest
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.model.profile.request.UpdateProfileRequest
import com.app.edtech.ui.signup.model.ChangePasswordRequest
import com.app.edtech.ui.signup.model.ResetPasswordRequest
import com.app.hihlo.ui.signup.model.SignUp
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiService {
    @POST("api/user/login")
    suspend fun login(@Body requestBody: LoginRequest): LoginResponse

    @POST("api/user/signup")
    suspend fun signup(@Body requestBody: LoginRequest): LoginResponse

    @POST("api/user/send-otp")
    suspend fun sendLoginOtp(@Body requestBody: LoginRequest): LoginResponse

    @POST("api/user/update-profile")
    suspend fun updateProfile(@Body requestBody: UpdateProfileRequest, @Header("Authorization") accessToken: String): LoginResponse
    @FormUrlEncoded
    @POST("api/user/verify-otp")
    suspend fun verifyLoginOtp(
        @Field("mobile") mobile: String,
        @Field("otp") otp: String,
        @Field("user_type") user_type: String,
    ): LoginResponse

    @POST("api/user/update-password")
    suspend fun resetPassword(@Body requestBody: ResetPasswordRequest): LoginResponse


    @POST("change-password")
    suspend fun changePassword(@Header("Authorization") token: String,@Body requestBody: ChangePasswordRequest): LoginResponse


    @POST("signup")
    suspend fun registerUser(
        @Body model: SignUp
    ): LoginResponse

    @FormUrlEncoded
    @POST("api/user/logout")
    suspend fun logout(@Header("Authorization") token: String, @Field("student_id") student_id: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("api/user/delete-account")
    suspend fun delete(@Header("Authorization") token: String, @Field("student_id") student_id: String
    ): LoginResponse

}

