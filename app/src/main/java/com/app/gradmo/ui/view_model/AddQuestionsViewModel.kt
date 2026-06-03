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
    private val apiRepository: ApiRepository
) : ViewModel() {

    // ── Question list (single source of truth for the adapter) ───────────
    private val _questions = MutableLiveData<MutableList<QuestionData>>(
        mutableListOf(QuestionData(imageFieldName = "question_image_0"))
    )
    val questions: LiveData<MutableList<QuestionData>> get() = _questions

    fun getQuestionList(): MutableList<QuestionData> = _questions.value ?: mutableListOf()

    // ─── Exam meta-fields (passed from CreateExamDetailsFragment) ─────────
    var batchId: Int = 0
    var examName: String = ""
    var timeDuration: Int = 0
    var scheduledDate: String = ""
    var scheduledTime: String = ""

    // ── Add / remove questions ────────────────────────────────────────────

    fun addQuestion(): Int {
        val list = getQuestionList()
        val idx  = list.size
        list.add(QuestionData(imageFieldName = "question_image_$idx"))
        _questions.value = list     // notifies counter observer in fragment
        return list.lastIndex
    }

    /**
     * Removes the question at [position].
     * Returns the index the ViewPager should navigate to after deletion,
     * or -1 if the list would be empty (caller should block deletion).
     */
    fun removeQuestion(position: Int): Int {
        val list = getQuestionList()
        if (list.size <= 1) return -1           // must keep at least one question
        list.removeAt(position)
        // Re-assign imageFieldName to keep them sequential after removal
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

    /**
     * Builds the multipart request and calls the API.
     *
     * @param token           "Bearer <accessToken>"
     * @param contentResolver used to read image bytes from content URIs
     * @param cacheDir        app's cache directory to create temp files from URIs
     */
    fun submitExam(token: String, contentResolver: ContentResolver, cacheDir: File) {
        val error = validate()
        if (error != null) {
            _submitResult.postValue(Resources.error(error, null))
            return
        }

        _submitResult.postValue(Resources.loading(null))

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val questions = getQuestionList()

                // ── Build questions_json list ────────────────────────────
                val questionsJson = questions.mapIndexed { i, q ->
                    val optionLetter = when (q.correctAnswer) {
                        1 -> "A"; 2 -> "B"; 3 -> "C"; else -> "D"
                    }
                    QuestionJson(
                        question_id   = 0,
                        subject_id    = 0,
                        chapter_id    = 0,
                        question      = q.questionText,
                        options       = listOf(q.option1, q.option2, q.option3, q.option4),
                        correct_option = q.correctAnswer.toString(),
                        answer        = optionLetter,
                        question_mask = 1,
                        question_image = "",          // server fills this after upload
                        image_field   = q.imageFieldName
                    )
                }

                val request = CreateExamRequest(
                    batch_id           = batchId,
                    name               = examName,
                    time_duration      = timeDuration,
                    mock_sheduled_date = scheduledDate,
                    mock_sheduled_time = scheduledTime,
                    total_question     = questions.size,
                    total_marks        = questions.size,   // 1 mark per question; adjust as needed
                    questions_json     = questionsJson
                )

                // ── Build multipart body ─────────────────────────────────
                val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

                // Add the JSON payload as a plain-text field
                multipartBuilder.addFormDataPart(
                    "data",
                    Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull()).toString()
                )

                // Attach each question's image (if picked) as a file part
                questions.forEach { q ->
                    if (!q.imagePath.isNullOrBlank()) {
                        val uri  = Uri.parse(q.imagePath)
                        val file = uriToTempFile(contentResolver, uri, cacheDir, q.imageFieldName)
                        if (file != null) {
                            val requestFile = file.asRequestBody(
                                contentResolver.getType(uri)?.toMediaTypeOrNull()
                                    ?: "image/*".toMediaTypeOrNull()
                            )
                            multipartBuilder.addFormDataPart(q.imageFieldName, file.name, requestFile)
                        }
                    }
                }

                val response = apiRepository.createExamApi(multipartBuilder.build(), token)

                withContext(Dispatchers.Main) {
                    if (response.status == "true") {
//                        _submitResult.postValue(response)
                    } else {
                        _submitResult.postValue(Resources.error(response.msg ?: "", null))
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    _submitResult.postValue(Resources.error(ex.localizedMessage ?: "Unknown error", null))
                }
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Copies a content URI to a temporary File so OkHttp can stream it. */
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