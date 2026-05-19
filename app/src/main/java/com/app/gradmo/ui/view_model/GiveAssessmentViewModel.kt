package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.exam_details.ExamDetailsRequest
import com.app.gradmo.model.exam_details.ExamDetailsResponse
import com.app.gradmo.model.exam_list.ExamDashboardResponse
import com.app.gradmo.model.exam_list.ExamsListRequest
import com.app.gradmo.model.exam_list.UpcomingExamListResponse
import com.app.gradmo.model.static.AssessmentOption
import com.app.gradmo.model.static.AssessmentQuestion
import com.app.gradmo.model.submit_exam.SubmitExamRequest
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GiveAssessmentViewModel @Inject constructor() : ViewModel() {

    // ── Timer ────────────────────────────────────────────────
    /** Total duration for the assessment in seconds. Change as needed or pass from API. */
    private var totalTimerSeconds: Int = 30 * 60  // default 30 minutes
    val startedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> = _timerText

    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished: LiveData<Boolean> = _timerFinished

    private var timerJob: Job? = null

    // ── Questions ────────────────────────────────────────────
    private val _questions = MutableLiveData<List<AssessmentQuestion>>()
    val questions: LiveData<List<AssessmentQuestion>> = _questions

    private val _currentIndex = MutableLiveData<Int>(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _selectedOptions = MutableLiveData<MutableMap<Int, Int>>(mutableMapOf())
    val selectedOptions: LiveData<MutableMap<Int, Int>> = _selectedOptions

    // ── Init ─────────────────────────────────────────────────
    init {
        loadStaticQuestions()   // replace with loadQuestionsFromApi() when ready
    }
// ── Exam Details LiveData (already wired in your repo/viewmodel) ──────────
    // Keep your existing getUpcomingExamsDetailsLiveData() function here.
    // Only timer logic is added below.

    // ── Timer ─────────────────────────────────────────────────────────────────
    fun startTimer(durationSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = durationSeconds
            while (remaining >= 0) {
                val minutes = remaining / 60
                val seconds = remaining % 60
                _timerText.postValue(String.format("%02d:%02d", minutes, seconds))
                delay(1_000L)
                remaining--
            }
            _timerFinished.postValue(true)
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
    // ── Static / Dummy data (replace with API call) ──────────
    private fun loadStaticQuestions() {
        val staticList = listOf(
            AssessmentQuestion(
                id = 1,
                questionText = "When skidding, if the rear end of the car is skidding to the right, turn your wheel to the:",
                imageUrl = null,   // set a real URL / drawable name when available
                options = listOf(
                    AssessmentOption(0, "Left — opposite direction of the skid"),
                    AssessmentOption(1, "Right — same direction as the skid"),
                    AssessmentOption(2, "Straight ahead and apply brakes gently"),
                    AssessmentOption(3, "Hard right and press the accelerator")
                )
            ),
            AssessmentQuestion(
                id = 2,
                questionText = "What is the minimum following distance you should maintain on a highway at 60 mph?",
                options = listOf(
                    AssessmentOption(0, "1 second"),
                    AssessmentOption(1, "2 seconds"),
                    AssessmentOption(2, "3 seconds"),
                    AssessmentOption(3, "4 seconds")
                )
            ),
            AssessmentQuestion(
                id = 3,
                questionText = "A solid white line on the road means:",
                options = listOf(
                    AssessmentOption(0, "You may cross to overtake"),
                    AssessmentOption(1, "Lane changing is discouraged but allowed"),
                    AssessmentOption(2, "You must not cross the line"),
                    AssessmentOption(3, "End of road ahead")
                )
            )
            // ── Add more questions here or replace with API response ──
        )
        _questions.value = staticList
    }

    // ── TODO: Replace above with API call like this ──────────
    // fun loadQuestionsFromApi(assessmentId: Int) {
    //     viewModelScope.launch {
    //         val result = repository.getQuestions(assessmentId)
    //         _questions.value = result.questions
    //         totalTimerSeconds = result.durationSeconds
    //         startTimer()
    //     }
    // }

    // ── Navigation ───────────────────────────────────────────
    fun goToNextQuestion() {
        val list = _questions.value ?: return
        val idx = _currentIndex.value ?: 0
        if (idx < list.size - 1) _currentIndex.value = idx + 1
    }

    fun goToPreviousQuestion() {
        val idx = _currentIndex.value ?: 0
        if (idx > 0) _currentIndex.value = idx - 1
    }

    fun isFirstQuestion() = (_currentIndex.value ?: 0) == 0
    fun isLastQuestion() = (_currentIndex.value ?: 0) == ((_questions.value?.size ?: 1) - 1)

    // ── Selection ────────────────────────────────────────────
    fun selectOption(questionId: Int, optionId: Int) {
        val map = _selectedOptions.value ?: mutableMapOf()
        map[questionId] = optionId
        _selectedOptions.value = map
    }

    fun getSelectedOptionForCurrentQuestion(): Int {
        val question = currentQuestion() ?: return -1
        return _selectedOptions.value?.get(question.id) ?: -1
    }

    fun currentQuestion(): AssessmentQuestion? {
        val list = _questions.value ?: return null
        return list.getOrNull(_currentIndex.value ?: 0)
    }

    private val upcomingExamsDetailsLiveData = SingleLiveEvent<Resources<ExamDetailsResponse>>()

    fun getUpcomingExamsDetailsLiveData(): LiveData<Resources<ExamDetailsResponse>> {
        return upcomingExamsDetailsLiveData
    }
    fun hitUpcomingExamsDetailsDataApi(token: String, request: ExamDetailsRequest) {

        try {
            upcomingExamsDetailsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    upcomingExamsDetailsLiveData.postValue(
                        Resources.success(
                            ApiRepository().getUpcomingExamsDetailsApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    upcomingExamsDetailsLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    private val submitExamsLiveData = SingleLiveEvent<Resources<ExamDashboardResponse>>()

    fun getSubmitExamsLiveData(): LiveData<Resources<ExamDashboardResponse>> {
        return submitExamsLiveData
    }
    fun hitSubmitExamsDataApi(token: String, request: SubmitExamRequest) {

        try {
            submitExamsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    submitExamsLiveData.postValue(
                        Resources.success(
                            ApiRepository().submitExamApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    submitExamsLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}