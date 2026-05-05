package com.app.edtech.network_call

import com.app.edtech.model.address.city.GetCitiesRequest
import com.app.edtech.model.address.city.GetCitiesResponse
import com.app.edtech.model.address.state.GetStatesRequest
import com.app.edtech.model.address.state.GetStatesResponse
import com.app.edtech.model.banner.response.BannerResponse
import com.app.edtech.model.create_order.CreateOrderResponse
import com.app.edtech.model.institute_detail.request.InstituteDetailRequest
import com.app.edtech.model.institute_detail.response.InstituteDetailResponse
import com.app.edtech.model.institute_list.request.InstitutesListRequest
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.login.request.LoginRequest
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.model.logout.response.DeleteResponse
import com.app.edtech.model.plan_detail.PlanDetailsResponse
import com.app.edtech.model.profile.request.UpdateProfileRequest
import com.app.edtech.model.promocode.response.PromocodeListResponse
import com.app.edtech.model.third_party_credentials.ThirdPartyCredentialsResponse
import com.app.edtech.ui.signup.model.ChangePasswordRequest
import com.app.edtech.ui.signup.model.ResetPasswordRequest
import com.app.hihlo.ui.signup.model.SignUp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap


interface ApiService {
    @POST("api/user/login")
    suspend fun login(@Body requestBody: LoginRequest): LoginResponse

    @POST("api/user/signup")
    suspend fun signup(@Body requestBody: LoginRequest): LoginResponse

    @POST("api/user/send-otp")
    suspend fun sendLoginOtp(@Body requestBody: LoginRequest): LoginResponse

    /*@POST("api/user/update-profile")
    suspend fun updateProfile(@Body requestBody: UpdateProfileRequest, @Header("Authorization") accessToken: String): LoginResponse*/

    @Multipart
    @POST("api/user/update-profile")
    suspend fun updateProfile(
        @Part image: MultipartBody.Part?,
        @PartMap requestMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Header("Authorization") accessToken: String
    ): LoginResponse
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
    ): DeleteResponse

    @POST("api/batch/slider-list")
    suspend fun getBanner(
        @Header("Authorization") token: String
    ): BannerResponse

    @POST("api/institute/listing")
    suspend fun getInstitutes(
        @Header("Authorization") token: String,
        @Body requestBody: InstitutesListRequest
    ): InstituteListResponse

    @POST("api/main/city-list")
    suspend fun getCities(@Body requestBody: GetCitiesRequest): GetCitiesResponse

    @POST("api/main/state-list")
    suspend fun getStates(@Body requestBody: GetStatesRequest): GetStatesResponse

    @POST("api/institute/details")
    suspend fun getInstituteDetail(@Header("Authorization") token: String, @Body requestBody: InstituteDetailRequest): InstituteDetailResponse

    @FormUrlEncoded
    @POST("api/plan/promo-codes")
    suspend fun getPromocodes(@Header("Authorization") token: String, @Field("batch_id") mobile: String,): PromocodeListResponse

    @FormUrlEncoded
    @POST("api/plan/plans")
    suspend fun getPlanDetails(@Header("Authorization") token: String, @Field("batch_id") mobile: String,): PlanDetailsResponse

    @GET("api/main/get_defaults_requirements")
    suspend fun getThirdPartyCredentials(@Header("Authorization") token: String): ThirdPartyCredentialsResponse

    @FormUrlEncoded
    @POST("api/payment/razorpay/create-order")
    suspend fun createOrder(@Header("Authorization") token: String, @Field("amount_in_rupees") mobile: String,): CreateOrderResponse

}

