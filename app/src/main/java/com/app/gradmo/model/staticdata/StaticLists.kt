package com.app.gradmo.model.staticdata

import com.app.gradmo.R

object StaticLists {
    val batchDetailList = listOf(
        BatchDetailItem("Live classes", R.drawable.batch_live_class_image),
        BatchDetailItem("Video Lectures", R.drawable.batch_video_lecture_image),
        BatchDetailItem("Library", R.drawable.batch_library_image),
        BatchDetailItem("Attendance", R.drawable.batch_attendence_image),
        BatchDetailItem("Exams", R.drawable.batch_exam_image),
        BatchDetailItem("Homework", R.drawable.batch_homework_image),
    )
}

data class BatchDetailItem(val name:String, val image:Int)