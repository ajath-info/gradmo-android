package com.app.gradmo.network_call

import com.app.gradmo.model.add_attendance.AddAttendanceRequest
import com.app.gradmo.model.add_attendance.AddAttendanceResponse
import com.app.gradmo.model.add_homework.AddHomeworkResponse
import com.app.gradmo.model.add_library.AddLibraryDataResponse
import com.app.gradmo.model.exam_details.ExamDetailsResponse
import com.app.gradmo.model.address.city.GetCitiesRequest
import com.app.gradmo.model.address.city.GetCitiesResponse
import com.app.gradmo.model.address.state.GetStatesRequest
import com.app.gradmo.model.address.state.GetStatesResponse
import com.app.gradmo.model.attendance_students_list.GetStudentsForAttendanceRequest
import com.app.gradmo.model.attendance_students_list.GetStudentsForAttendanceResponse
import com.app.gradmo.model.attendence.AttendanceListRequest
import com.app.gradmo.model.attendence.AttendanceListResponse
import com.app.gradmo.model.banner.response.BannerResponse
import com.app.gradmo.model.batch_detail.BatchDetailResponse
import com.app.gradmo.model.batch_list.BatchListRequest
import com.app.gradmo.model.batch_list.BatchListResponse
import com.app.gradmo.model.content_page.ContentPagesResponse
import com.app.gradmo.model.create_order.CreateOrderResponse
import com.app.gradmo.model.create_zoom.CreateZoomRequest
import com.app.gradmo.model.create_zoom.CreateZoomResponse
import com.app.gradmo.model.end_zoom.EndZoomClassRequest
import com.app.gradmo.model.end_zoom.EndZoomClassResponse
import com.app.gradmo.model.exam_details.ExamDetailsRequest
import com.app.gradmo.model.exam_list.ExamDashboardResponse
import com.app.gradmo.model.exam_list.ExamsListRequest
import com.app.gradmo.model.exam_list.UpcomingExamListResponse
import com.app.gradmo.model.homework.HomeworkListResponse
import com.app.gradmo.model.institute_detail.request.InstituteDetailRequest
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.model.institute_list.request.InstitutesListRequest
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.model.library_list.LibraryListRequest
import com.app.gradmo.model.library_list.LibraryListResponse
import com.app.gradmo.model.live_class.BatchLiveClassListRequest
import com.app.gradmo.model.live_class.BatchLiveClassListResponse
import com.app.gradmo.model.live_class.LiveClassDetailsRequest
import com.app.gradmo.model.live_class.LiveClassDetailsResponse
import com.app.gradmo.model.login.request.LoginRequest
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.logout.response.DeleteResponse
import com.app.gradmo.model.notification_list.NotificationListRequest
import com.app.gradmo.model.notification_list.NotificationListResponse
import com.app.gradmo.model.payment_history.PaymentHistoryRequest
import com.app.gradmo.model.payment_history.PaymentHistoryResponse
import com.app.gradmo.model.plan_detail.PlanDetailsResponse
import com.app.gradmo.model.promocode.response.PromocodeListResponse
import com.app.gradmo.model.questions.CreateExamResponse
import com.app.gradmo.model.submit_exam.SubmitExamRequest
import com.app.gradmo.model.teacher_created_exams.TeacherCreatedExamsResponse
import com.app.gradmo.model.third_party_credentials.ThirdPartyCredentialsResponse
import com.app.gradmo.model.verify_payment.VerifyPaymentRequest
import com.app.gradmo.model.verify_payment.VerifyPaymentResponse
import com.app.gradmo.model.video_lecture.VideoLectureListResponse
import com.app.gradmo.model.zoom_details.ZoomDetailsResponse
import com.app.gradmo.ui.signup.model.ChangePasswordRequest
import com.app.gradmo.ui.signup.model.ResetPasswordRequest
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

    @POST("api/payment/razorpay/verify-payment")
    suspend fun verifyPayment(@Header("Authorization") token: String, @Body requestBody: VerifyPaymentRequest,): VerifyPaymentResponse

    @FormUrlEncoded
    @POST("api/batch/batch-details")
    suspend fun batchDetails(@Header("Authorization") token: String, @Field("batch_id") mobile: String,): BatchDetailResponse

    @POST("api/batch/library-list")
    suspend fun getLibraryList(
        @Header("Authorization") token: String,
        @Body requestBody: LibraryListRequest
    ): LibraryListResponse

    @Multipart
    @POST("api/batch/library-add-book")
    suspend fun addLibraryData(
        @Header("Authorization") token: String,
        @Part("batch_id") batchId: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("title") title: RequestBody,
        @Part("topic") topic: RequestBody,
        @Part pdf_file: MultipartBody.Part
    ): AddLibraryDataResponse

    @Multipart
    @POST("api/batch/homework-add")
    suspend fun addHomeworkData(
        @Header("Authorization") token: String,
        @Part("batch_id") batchId: RequestBody,
        @Part("subject_id") subjectId: RequestBody,
        @Part("date") date: RequestBody,
        @Part("description") description: RequestBody,
//        @Part("title") title: RequestBody,
        @Part pdf_file: MultipartBody.Part
    ): AddHomeworkResponse

    @POST("api/batch/homework-list")
    suspend fun getHomeworkList(
        @Header("Authorization") token: String,
        @Body requestBody: LibraryListRequest
    ): HomeworkListResponse

    @POST("api/batch/video-lecture-list")
    suspend fun getVideoLectureList(
        @Header("Authorization") token: String,
        @Body requestBody: LibraryListRequest
    ): VideoLectureListResponse

    @POST("api/batch/upcoming-exam-list")
    suspend fun getUpcomingExamsList(
        @Header("Authorization") token: String,
        @Body requestBody: ExamsListRequest
    ): UpcomingExamListResponse

    @POST("api/batch/upcoming-exam-details")
    suspend fun getUpcomingExamsDetails(
        @Header("Authorization") token: String,
        @Body requestBody: ExamDetailsRequest
    ): ExamDetailsResponse

    @POST("api/batch/student-exam-dashboard")
    suspend fun getExamDashboardData(
        @Header("Authorization") token: String,
        @Body requestBody: ExamsListRequest
    ): ExamDashboardResponse

    @POST("api/batch/student-submit-exam")
    suspend fun submitExam(
        @Header("Authorization") token: String,
        @Body requestBody: SubmitExamRequest
    ): ExamDashboardResponse

    @POST("api/user/attendance-list")
    suspend fun getAttendance(
        @Header("Authorization") token: String,
        @Body requestBody: AttendanceListRequest
    ): AttendanceListResponse

    @POST("api/batch/batch-list")
    suspend fun getBatchList(
        @Header("Authorization") token: String,
        @Body requestBody: BatchListRequest
    ): BatchListResponse

    @POST("api/batch/exam-add")   // ← update to your real endpoint
    suspend fun createExam(
        @Header("Authorization") accessToken: String,
        @Body request: MultipartBody          // whole multipart body built in ViewModel
    ): CreateExamResponse

    @POST("api/batch/exam-manage-list")
    suspend fun getTeacherCreatedExamList(
        @Header("Authorization") token: String,
        @Body requestBody: ExamsListRequest
    ): TeacherCreatedExamsResponse

    @Multipart
    @POST("api/batch/video-lecture-add")
    suspend fun addVideoLecture(
        @Header("Authorization") token: String,
        @Part("batch_id") batchId: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("title") title: RequestBody,
        @Part("topic") topic: RequestBody,
        @Part("description") description: RequestBody,
        @Part("preview_type") previewType: RequestBody,
        @Part video_file: MultipartBody.Part
    ): AddLibraryDataResponse

    @POST("api/batch/live-class-list")
    suspend fun getLiveClassList(
        @Header("Authorization") token: String,
        @Body requestBody: BatchLiveClassListRequest
    ): BatchLiveClassListResponse

    @POST("api/batch/live-class-details")
    suspend fun getLiveClassDetails(
        @Header("Authorization") token: String,
        @Body requestBody: LiveClassDetailsRequest
    ): LiveClassDetailsResponse

    @FormUrlEncoded
    @POST("api/batch/batch-zoom-details")
    suspend fun getZoomDetails(
        @Header("Authorization") token: String,
        @Field("batch_id") batch_id: String,
    ): ZoomDetailsResponse

    @POST("api/batch/batch-zoom-create")
    suspend fun createZoomClass(
        @Header("Authorization") token: String,
        @Body requestBody: CreateZoomRequest
    ): CreateZoomResponse

    @POST("api/batch/live-meeting-end")
    suspend fun endZoomClass(
        @Header("Authorization") token: String,
        @Body requestBody: EndZoomClassRequest
    ): EndZoomClassResponse

    @POST("api/batch/attendance-roster-matrix")
    suspend fun getStudentsForAttendance(
        @Header("Authorization") token: String,
            @Body requestBody: GetStudentsForAttendanceRequest
    ): GetStudentsForAttendanceResponse

    @POST("api/user/add-attendance")
    suspend fun addAttendance(
        @Header("Authorization") token: String,
        @Body requestBody: AddAttendanceRequest
    ): AddAttendanceResponse

    @GET("api/main/pages")
    suspend fun contentPageData(
        @Header("Authorization") token: String,
    ): ContentPagesResponse

    @POST("api/user/payment-history")
    suspend fun getPaymentHistory(
        @Header("Authorization") token: String,
        @Body requestBody: PaymentHistoryRequest
    ): PaymentHistoryResponse

    @POST("api/main/notifications-list")
    suspend fun getNotificationList(
        @Header("Authorization") token: String,
        @Body requestBody: NotificationListRequest
    ): NotificationListResponse
}

