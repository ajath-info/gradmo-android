package com.app.gradmo.model.static

data class AssessmentQuestion(
    val id: Int,
    val questionText: String,
    val imageUrl: String? = null,          // null = no image shown
    val options: List<AssessmentOption>,
    val correctOptionId: Int? = null       // filled after user submits / from API
)

data class AssessmentOption(
    val id: Int,                           // 0-based index matches label A/B/C/D
    val optionText: String
)