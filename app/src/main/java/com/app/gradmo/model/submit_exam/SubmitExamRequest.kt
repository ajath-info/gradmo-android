package com.app.gradmo.model.submit_exam

data class SubmitExamRequest(
    val exam_id: String?=null,
    val started_at: String?=null,
    val answers: Map<String, String>? = null,
)
