package com.app.gradmo.model.questions

import java.io.Serializable

data class QuestionData(
    var questionText: String = "",
    var imagePath: String? = null,      // local content URI after gallery pick
    var imageFieldName: String = "",    // e.g. "question_image_0" – set by ViewModel
    var option1: String = "",
    var option2: String = "",
    var option3: String = "",
    var option4: String = "",
    /**
     * 1-based index of the correct option; -1 = not yet selected.
     * API expects "1".."4" as a String and "A".."D" as the letter answer.
     */
    var correctAnswer: Int = -1
) : Serializable

// ── Args bundle keys (used between CreateExamDetailsFragment → AddQuestionsFragment) ──

object ExamArgs {
    const val BATCH_ID      = "batch_id"
    const val NAME          = "name"
    const val DURATION      = "time_duration"   // Int minutes
    const val DUE_DATE      = "mock_scheduled_date"   // "yyyy-MM-dd"
    const val DUE_TIME      = "mock_scheduled_time"   // "HH:mm"
}

// ── API request / response models ─────────────────────────────────────────────

data class QuestionJson(
    val question_id: Int = 0,
    val subject_id: Int = 0,       // default 0; extend if you have subject picker
    val chapter_id: Int = 0,       // default 0; extend if you have chapter picker
    val question: String,
    val options: List<String>,     // always 4 elements
    val correct_option: String,    // "1".."4"
    val answer: String,            // "A".."D"
    val question_mask: Int = 1,
    val question_image: String = "",   // filename on server; "" until uploaded
    val image_field: String            // e.g. "question_image_0"
)

data class CreateExamRequest(
    val batch_id: String,
    val name: String,
    val time_duration: Int,
    val mock_sheduled_date: String,   // note: API has the typo "sheduled"
    val mock_sheduled_time: String,
    val type: Int = 1,
    val format: Int = 2,
    val marking_parcent: Int = 0,
    val total_question: Int,
    val total_marks: Int,
    val questions_json: List<QuestionJson>
)

data class CreateExamResponse(
    val status: String,
    val msg: String,
    val data: ExamResultData?
)

data class ExamResultData(
    val id: Int,
    val batch_id: Int,
    val total_question: Int,
    val total_marks: Int
)