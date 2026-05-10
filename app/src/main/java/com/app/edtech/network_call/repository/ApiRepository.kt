package com.app.edtech.network_call.repository

import com.app.edtech.model.address.city.GetCitiesRequest
import com.app.edtech.model.address.state.GetStatesRequest
import com.app.edtech.model.institute_detail.request.InstituteDetailRequest
import com.app.edtech.model.institute_list.request.InstitutesListRequest
import com.app.edtech.model.login.request.LoginRequest
import com.app.edtech.model.profile.request.UpdateProfileRequest
import com.app.edtech.model.verify_payment.VerifyPaymentRequest
import com.app.edtech.network_call.RetrofitBuilder
import com.app.edtech.ui.signup.model.ChangePasswordRequest
import com.app.edtech.ui.signup.model.ResetPasswordRequest
import com.app.hihlo.ui.signup.model.SignUp
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ApiRepository {
    private val service = RetrofitBuilder.apiService

    suspend fun loginApi(request: LoginRequest) = service.login(request)
    suspend fun signupApi(request: LoginRequest) = service.signup(request)
    suspend fun updateProfileApi(
        image: MultipartBody.Part?,
        requestMap: Map<String, RequestBody>,
        accessToken: String
    ) = service.updateProfile(image, requestMap, accessToken)
    suspend fun sendLoginOtpApi(request: LoginRequest) = service.sendLoginOtp(request)

    suspend fun resetPassword(request: ResetPasswordRequest) = service.resetPassword(request)

    suspend fun changePassword(token:String,request: ChangePasswordRequest) = service.changePassword(token,request)

    suspend fun verifyLoginOtp(mobile: String,otp:String, userType:String) = service.verifyLoginOtp(mobile, otp, userType)

    suspend fun registerUser(model: SignUp) = service.registerUser(model)

    suspend fun logoutApi(token: String, student_id: String) = service.logout(token, student_id)

    suspend fun deleteApi(token: String, student_id: String) = service.delete(token, student_id)

    suspend fun getBannerApi(token: String) = service.getBanner(token)

    suspend fun getInstitutesApi(token: String, request: InstitutesListRequest) = service.getInstitutes(token, request)

    suspend fun getCitiesApi(request: GetCitiesRequest) = service.getCities(request)

    suspend fun getStatesApi(request: GetStatesRequest) = service.getStates(request)

    suspend fun getInstituteDetailApi(token: String, request: InstituteDetailRequest) = service.getInstituteDetail(token, request)

    suspend fun getPromocodesApi(token: String, batch_id: String) = service.getPromocodes(token, batch_id)

    suspend fun getPlanDetailsApi(token: String, batch_id: String) = service.getPlanDetails(token, batch_id)

    suspend fun getThirdPartyCredentialsApi(token: String) = service.getThirdPartyCredentials(token)

    suspend fun createOrderApi(token: String, amount_in_rupees: String) = service.createOrder(token, amount_in_rupees)

    suspend fun verifyPaymentApi(token: String, request: VerifyPaymentRequest) = service.verifyPayment(token, request)

    suspend fun batchDetailsApi(token: String, batch_id: String) = service.batchDetails(token, batch_id)


}