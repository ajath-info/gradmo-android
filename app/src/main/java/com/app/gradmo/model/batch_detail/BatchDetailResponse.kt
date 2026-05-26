package com.app.gradmo.model.batch_detail

data class BatchDetailResponse(
    val batch_details: BatchDetails,
    val message: String,
    val status: Boolean
)

data class BatchDetails(
    val batchFecherd: List<BatchFetched> = listOf(),
    val batchImage: String?=null,
    val batchName: String?=null,
    val batch_id: Int?=null,
    val batch_offer_price: Int?=null,
    val batch_price: Int?=null,
    val batch_type: Int?=null,
    val canEnroll: Boolean?=null,
    val category_name: String?=null,
    val description: String?=null,
    val end_date: String?=null,
    val end_time: String?=null,
    val enrollment: Enrollment?=null,
    val instructor: String?=null,
    val logo: String?=null,
    val modules: Modules?=null,
    val pay_mode: String?=null,
    val schedule: String?=null,
    val start_date: String?=null,
    val start_time: String?=null,
    val subcategory_name: String?=null,
    val title: String?=null
)

data class BatchFetched(
    val batchSpecification: String,
    val fecherd: String
)

data class Enrollment(
    val added_by: String?=null,
    val create_at: String?=null,
    val status: Int?=null
)

data class Modules(
    val attendance: Attendance?=null,
    val homework: Homework?=null,
    val library: Library?=null,
    val live_classes: LiveClasses?=null,
    val upcoming_exams: UpcomingExams?=null,
    val video_lectures: VideoLectures?=null
)

data class Attendance(
    val icon: String?=null,
    val marked_records: Int?=null
)

data class Homework(
    val icon: String?=null,
    val pending_count: Int?=null,
    val today_count: Int?=null
)

data class Library(
    val book_count: Int?=null,
    val has_new_content: Boolean?=null,
    val icon: String?=null,
    val notes_count: Int?=null
)

data class LiveClasses(
    val current_session_id: String?=null,
    val icon: String?=null,
    val is_live: Boolean?=null
)

data class UpcomingExams(
    val count: Int?=null,
    val icon: String?=null
)

data class VideoLectures(
    val count: Int?=null,
    val icon: String?=null
)