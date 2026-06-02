package com.app.gradmo.network_call.repository

import com.app.gradmo.model.address.city.GetCitiesRequest
import com.app.gradmo.model.address.state.GetStatesRequest
import com.app.gradmo.model.attendence.AttendanceListRequest
import com.app.gradmo.model.batch_list.BatchListRequest
import com.app.gradmo.model.exam_details.ExamDetailsRequest
import com.app.gradmo.model.exam_list.ExamsListRequest
import com.app.gradmo.model.institute_detail.request.InstituteDetailRequest
import com.app.gradmo.model.institute_list.request.InstitutesListRequest
import com.app.gradmo.model.library_list.LibraryListRequest
import com.app.gradmo.model.login.request.LoginRequest
import com.app.gradmo.model.submit_exam.SubmitExamRequest
import com.app.gradmo.model.verify_payment.VerifyPaymentRequest
import com.app.gradmo.network_call.RetrofitBuilder
import com.app.gradmo.ui.signup.model.ChangePasswordRequest
import com.app.gradmo.ui.signup.model.ResetPasswordRequest
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

    suspend fun getLibraryListApi(token: String, request: LibraryListRequest) = service.getLibraryList(token, request)

    suspend fun getHomeworkListApi(token: String, request: LibraryListRequest) = service.getHomeworkList(token, request)

    suspend fun getVideoLectureListApi(token: String, request: LibraryListRequest) = service.getVideoLectureList(token, request)

    suspend fun getUpcomingExamsListApi(token: String, request: ExamsListRequest) = service.getUpcomingExamsList(token, request)

    suspend fun getUpcomingExamsDetailsApi(token: String, request: ExamDetailsRequest) = service.getUpcomingExamsDetails(token, request)

    suspend fun getExamDashboardDataApi(token: String, request: ExamsListRequest) = service.getExamDashboardData(token, request)

    suspend fun submitExamApi(token: String, request: SubmitExamRequest) = service.submitExam(token, request)

    suspend fun getAttendanceApi(token: String, request: AttendanceListRequest) = service.getAttendance(token, request)

    suspend fun getBatchListApi(token: String, request: BatchListRequest) = service.getBatchList(token, request)
}