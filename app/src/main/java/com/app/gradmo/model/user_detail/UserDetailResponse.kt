package com.app.gradmo.model.user_detail

data class UserDetailResponse(
    val `data`: Data,
    val msg: String,
    val status: String
) {
    data class Data(
        val batchList: List<Batch> = listOf(),
        val enrollment_details: EnrollmentDetails?=null,
        val instituteList: List<Institute>?=null,
        val userDetails: UserDetails?=null
    ) {
        data class Batch(
            val batchImage: String,
            val batchName: String,
            val batch_id: Int,
            val batch_type: Int,
            val description: String,
            val end_date: String,
            val end_time: String,
            val enrolled_at: String,
            val enrollment_status: Int,
            val instituteName: String,
            val institute_id: Int,
            val institute_name: String,
            val instructor: String,
            val logo: String,
            val schedule: String,
            val start_date: String,
            val start_time: String,
            val title: String
        )

        data class EnrollmentDetails(
            val plan_one: PlanOne
        ) {
            data class PlanOne(
                val expires_at: Any,
                val yearly_expired: Boolean,
                val yearly_valid: Boolean
            )
        }

        data class Institute(
            val email: String,
            val image: String,
            val instituteId: Int,
            val name: String,
            val role: Int
        )

        data class UserDetails(
            val addedBy: String,
            val address: String,
            val adminId: Int,
            val admissionDate: String,
            val appVersion: String,
            val batchId: String,
            val city: String,
            val contactNo: String,
            val country: String,
            val device_id: String,
            val device_token: String,
            val device_type: String,
            val dob: String,
            val email: String,
            val enrollmentId: String,
            val fatherDesignation: String,
            val fatherName: String,
            val gender: String,
            val grade: String,
            val image: String,
            val isVerified: String,
            val is_profile_completed: Int,
            val lastLoginApp: String,
            val lastName: String,
            val last_name: String,
            val loginStatus: Int,
            val mobile: String,
            val mobileAlt: String,
            val multiBatch: String,
            val name: String,
            val payMode: Int,
            val paymentStatus: Int,
            val pincode: String,
            val schoolCollegeName: String,
            val state: String,
            val status: Int,
            val studentId: Int,
            val updatedAt: String,
            val userType: String,
            val userTypeDb: String
        )
    }
}