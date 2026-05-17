package com.app.gradmo.model.batch_detail

data class BatchDetailResponse(
    val batch_details: BatchDetails,
    val message: String,
    val status: Boolean
)

data class BatchDetails(
    val batchFecherd: List<BatchFetched>,
    val batchImage: String,
    val batchName: String,
    val batch_id: Int,
    val batch_offer_price: Int,
    val batch_price: Int,
    val batch_type: Int,
    val canEnroll: Boolean,
    val category_name: String,
    val description: String,
    val end_date: String,
    val end_time: String,
    val enrollment: Enrollment,
    val instructor: String,
    val logo: String,
    val modules: Modules,
    val pay_mode: String,
    val schedule: String,
    val start_date: String,
    val start_time: String,
    val subcategory_name: String,
    val title: String
)

data class BatchFetched(
    val batchSpecification: String,
    val fecherd: String
)

data class Enrollment(
    val added_by: String,
    val create_at: String,
    val status: Int
)

data class Modules(
    val attendance: Attendance,
    val homework: Homework,
    val library: Library,
    val live_classes: LiveClasses,
    val upcoming_exams: UpcomingExams,
    val video_lectures: VideoLectures
)

data class Attendance(
    val icon: String,
    val marked_records: Int
)

data class Homework(
    val icon: String,
    val pending_count: Int,
    val today_count: Int
)

data class Library(
    val book_count: Int,
    val has_new_content: Boolean,
    val icon: String,
    val notes_count: Int
)

data class LiveClasses(
    val current_session_id: String?,
    val icon: String,
    val is_live: Boolean
)

data class UpcomingExams(
    val count: Int,
    val icon: String
)

data class VideoLectures(
    val count: Int,
    val icon: String
)