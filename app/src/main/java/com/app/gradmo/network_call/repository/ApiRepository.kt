package com.app.gradmo.network_call.repository

import com.app.gradmo.model.add_attendance.AddAttendanceRequest
import com.app.gradmo.model.address.city.GetCitiesRequest
import com.app.gradmo.model.address.state.GetStatesRequest
import com.app.gradmo.model.attendance_students_list.GetStudentsForAttendanceRequest
import com.app.gradmo.model.attendence.AttendanceListRequest
import com.app.gradmo.model.batch_list.BatchListRequest
import com.app.gradmo.model.create_zoom.CreateZoomRequest
import com.app.gradmo.model.end_zoom.EndZoomClassRequest
import com.app.gradmo.model.exam_details.ExamDetailsRequest
import com.app.gradmo.model.exam_list.ExamsListRequest
import com.app.gradmo.model.institute_detail.request.InstituteDetailRequest
import com.app.gradmo.model.institute_list.request.InstitutesListRequest
import com.app.gradmo.model.library_list.LibraryListRequest
import com.app.gradmo.model.live_class.BatchLiveClassListRequest
import com.app.gradmo.model.live_class.LiveClassDetailsRequest
import com.app.gradmo.model.login.request.LoginRequest
import com.app.gradmo.model.notification_list.NotificationListRequest
import com.app.gradmo.model.payment_history.PaymentHistoryRequest
import com.app.gradmo.model.submit_exam.SubmitExamRequest
import com.app.gradmo.model.verify_payment.VerifyPaymentRequest
import com.app.gradmo.network_call.RetrofitBuilder
import com.app.gradmo.ui.signup.model.ChangePasswordRequest
import com.app.gradmo.ui.signup.model.ResetPasswordRequest
import com.app.hihlo.ui.signup.model.SignUp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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

    suspend fun getBannerApi(token: String, institute_id: String?=null) = service.getBanner(token, institute_id)

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

    suspend fun addLibraryDataApi(
        token: String,
        batchId: String,
        subject: String,
        title: String,
        topic: String,
        pdfFile: File
    ) = service.addLibraryData(
        token = token,
        batchId = batchId.toRequestBody("text/plain".toMediaTypeOrNull()),
        subject = subject.toRequestBody("text/plain".toMediaTypeOrNull()),
        title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
        topic = topic.toRequestBody("text/plain".toMediaTypeOrNull()),
        pdf_file = MultipartBody.Part.createFormData(
            "pdf_file", pdfFile.name,
            pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())
        )
    )

    suspend fun addHomeworkDataApi(
        token: String,
        batchId: String,
        subjectId: String,
        date: String,
        description: String,
        title: String,
        pdfFile: File
    ) = service.addHomeworkData(
        token = token,
        batchId = batchId.toRequestBody("text/plain".toMediaTypeOrNull()),
        subjectId = subjectId.toRequestBody("text/plain".toMediaTypeOrNull()),
        date = date.toRequestBody("text/plain".toMediaTypeOrNull()),
        description = description.toRequestBody("text/plain".toMediaTypeOrNull()),
//        title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
        pdf_file = MultipartBody.Part.createFormData(
            "pdf_file", pdfFile.name,
            pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())
        )
    )

    suspend fun getHomeworkListApi(token: String, request: LibraryListRequest) = service.getHomeworkList(token, request)

    suspend fun getVideoLectureListApi(token: String, request: LibraryListRequest) = service.getVideoLectureList(token, request)

    suspend fun getUpcomingExamsListApi(token: String, request: ExamsListRequest) = service.getUpcomingExamsList(token, request)

    suspend fun getUpcomingExamsDetailsApi(token: String, request: ExamDetailsRequest) = service.getUpcomingExamsDetails(token, request)

    suspend fun getExamDashboardDataApi(token: String, request: ExamsListRequest) = service.getExamDashboardData(token, request)

    suspend fun submitExamApi(token: String, request: SubmitExamRequest) = service.submitExam(token, request)

    suspend fun getAttendanceApi(token: String, request: AttendanceListRequest) = service.getAttendance(token, request)

    suspend fun getBatchListApi(token: String, request: BatchListRequest) = service.getBatchList(token, request)

    suspend fun createExamApi(
        accessToken: String,
        body: MultipartBody
    ) = service.createExam(accessToken, body)

    suspend fun getTeacherCreatedExamListApi(token: String, request: ExamsListRequest) = service.getTeacherCreatedExamList(token, request)

    suspend fun addVideoLectureApi(
        token: String,
        batchId: String,
        subject: String,
        title: String,
        topic: String,
        description: String,
        previewType: String,
        videoFile: File
    ) = service.addVideoLecture(
        token = token,
        batchId = batchId.toRequestBody("text/plain".toMediaTypeOrNull()),
        subject = subject.toRequestBody("text/plain".toMediaTypeOrNull()),
        title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
        topic = topic.toRequestBody("text/plain".toMediaTypeOrNull()),
        description = description.toRequestBody("text/plain".toMediaTypeOrNull()),
        previewType = previewType.toRequestBody("text/plain".toMediaTypeOrNull()),
        video_file = MultipartBody.Part.createFormData(
            "video_file", videoFile.name,
            videoFile.asRequestBody("video/*".toMediaTypeOrNull())
        )
    )

    suspend fun getLiveClassListApi(token: String, request: BatchLiveClassListRequest) = service.getLiveClassList(token, request)

    suspend fun getLiveClassDetailsApi(token: String, request: LiveClassDetailsRequest) = service.getLiveClassDetails(token, request)

    suspend fun getZoomDetailsApi(token: String, batch_id: String) = service.getZoomDetails(token, batch_id)

    suspend fun createZoomClassApi(token: String, request: CreateZoomRequest) = service.createZoomClass(token, request)

    suspend fun endZoomClassApi(token: String, request: EndZoomClassRequest) = service.endZoomClass(token, request)

    suspend fun contentPageDataApi(token: String) = service.contentPageData(token)

    suspend fun getPaymentHistoryApi(token: String, request: PaymentHistoryRequest) = service.getPaymentHistory(token, request)

    suspend fun getNotificationListApi(token: String, request: NotificationListRequest) = service.getNotificationList(token, request)

    suspend fun getUserDetailsApi(token: String) = service.getUserDetails(token)

    suspend fun getInstituteCitiesApi(token: String) = service.getInstituteCities(token)

}