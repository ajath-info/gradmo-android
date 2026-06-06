package com.app.gradmo.ui.view_model

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.questions.CreateExamRequest
import com.app.gradmo.model.questions.CreateExamResponse
import com.app.gradmo.model.questions.QuestionData
import com.app.gradmo.model.questions.QuestionJson
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class AddQuestionsViewModel @Inject constructor(
//    private val apiRepository: ApiRepository
) : ViewModel() {

    // ── Question list (single source of truth for the adapter) ───────────
    private val _questions = MutableLiveData<MutableList<QuestionData>>(
        mutableListOf(QuestionData(imageFieldName = "question_image_0"))
    )
    val questions: LiveData<MutableList<QuestionData>> get() = _questions

    fun getQuestionList(): MutableList<QuestionData> = _questions.value ?: mutableListOf()

    // ── Exam meta-fields (populated from args in the fragment) ────────────
    var batchId: String = "0"
    var examName: String = ""
    var timeDuration: Int = 0
    var scheduledDate: String = ""
    var scheduledTime: String = ""

    // ── Add / remove ──────────────────────────────────────────────────────

    fun addQuestion(): Int {
        val list = getQuestionList()
        list.add(QuestionData(imageFieldName = "question_image_${list.size}"))
        _questions.value = list
        return list.lastIndex
    }

    /**
     * Returns the page index to navigate to after deletion,
     * or -1 if the list has only one item (deletion blocked).
     */
    fun removeQuestion(position: Int): Int {
        val list = getQuestionList()
        if (list.size <= 1) return -1
        list.removeAt(position)
        list.forEachIndexed { i, q -> q.imageFieldName = "question_image_$i" }
        _questions.value = list
        return if (position >= list.size) list.lastIndex else position
    }

    fun getCurrentCount(): Int = getQuestionList().size

    // ── Validation ────────────────────────────────────────────────────────

    fun validate(): String? {
        getQuestionList().forEachIndexed { index, q ->
            val n = index + 1
            if (q.questionText.isBlank())
                return "Question $n: Question text is required."
            if (q.option1.isBlank() || q.option2.isBlank() ||
                q.option3.isBlank() || q.option4.isBlank()
            ) return "Question $n: All 4 options must be filled."
            if (q.correctAnswer == -1)
                return "Question $n: Please select the correct answer."
        }
        return null
    }

    // ── Submit ────────────────────────────────────────────────────────────

    private val _submitResult = SingleLiveEvent<Resources<CreateExamResponse>>()
    val submitResult: LiveData<Resources<CreateExamResponse>> get() = _submitResult

    fun submitExam(token: String, contentResolver: ContentResolver, cacheDir: File) {
        val error = validate()
        if (error != null) {
            _submitResult.postValue(Resources.error(error, null))
            return
        }

        _submitResult.postValue(Resources.loading(null))

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val questionList = getQuestionList()

                // ── Build questions_json ──────────────────────────────────
                val questionsJson = questionList.mapIndexed { _, q ->
                    QuestionJson(
                        question_id    = 0,
                        subject_id     = 0,
                        chapter_id     = 0,
                        question       = q.questionText,
                        options        = listOf(q.option1, q.option2, q.option3, q.option4),
                        correct_option = q.correctAnswer.toString(),
                        answer         = when (q.correctAnswer) { 1 -> "A"; 2 -> "B"; 3 -> "C"; else -> "D" },
                        question_mask  = 1,
                        question_image = "",
                        image_field    = q.imageFieldName
                    )
                }

                // ── Build MultipartBody ───────────────────────────────────
                // API expects individual flat form fields (not a wrapped JSON blob),
                // matching the Postman --form style exactly.
                val multipartBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .apply {
                        // Flat scalar fields
                        addFormDataPart("batch_id",            batchId.toString())
                        addFormDataPart("name",                examName)
                        addFormDataPart("time_duration",       timeDuration.toString())
                        addFormDataPart("mock_sheduled_date",  scheduledDate)
                        addFormDataPart("mock_sheduled_time",  scheduledTime)
                        addFormDataPart("type",                "1")
                        addFormDataPart("format",              "2")
                        addFormDataPart("marking_parcent",     "0")
                        addFormDataPart("total_question",      questionList.size.toString())
                        addFormDataPart("total_marks",         questionList.size.toString())

                        // questions_json as a JSON-string form field (not a file)
                        addFormDataPart("questions_json",      Gson().toJson(questionsJson))

                        // One image file part per question that has an image picked
                        questionList.forEach { q ->
                            if (!q.imagePath.isNullOrBlank()) {
                                val uri  = Uri.parse(q.imagePath)
                                val file = uriToTempFile(contentResolver, uri, cacheDir, q.imageFieldName)
                                if (file != null) {
                                    val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                                    addFormDataPart(
                                        q.imageFieldName,   // e.g. "question_image_0"
                                        file.name,
                                        file.asRequestBody(mimeType.toMediaTypeOrNull())
                                    )
                                }
                            }
                        }
                    }
                    .build()

                // ── Hit the API ───────────────────────────────────────────
                val response = ApiRepository().createExamApi(token, multipartBody)

                withContext(Dispatchers.Main) {
                    if (response.status == "true") {
                        _submitResult.postValue(Resources.success(response))
                    } else {
                        _submitResult.postValue(Resources.error(response.msg ?: "Failed", null))
                    }
                }

            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    _submitResult.postValue(
                        Resources.error(ex.localizedMessage ?: "Unknown error", null)
                    )
                }
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun uriToTempFile(
        contentResolver: ContentResolver,
        uri: Uri,
        cacheDir: File,
        name: String
    ): File? = try {
        val ext  = contentResolver.getType(uri)?.substringAfterLast('/') ?: "jpg"
        val file = File(cacheDir, "$name.$ext")
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        file
    } catch (e: Exception) {
        null
    }
}