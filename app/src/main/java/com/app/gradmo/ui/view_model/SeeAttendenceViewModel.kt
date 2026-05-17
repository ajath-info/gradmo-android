package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.batch_detail.BatchDetailResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.MutableLiveData
import com.app.gradmo.model.static.CalenderModels.AttendanceRepository
import com.app.gradmo.model.static.CalenderModels.MonthAttendanceData
import java.util.Calendar

@HiltViewModel
class SeeAttendenceViewModel @Inject constructor() : ViewModel() {


        // ── Current displayed month ───────────────────────────────────────────────
        private val _currentYear  = MutableLiveData<Int>()
        private val _currentMonth = MutableLiveData<Int>()   // 1-based

        // ── Attendance data exposed to UI ─────────────────────────────────────────
        private val _attendanceData = MutableLiveData<MonthAttendanceData>()
        val attendanceData: LiveData<MonthAttendanceData> = _attendanceData

        private val _isLoading = MutableLiveData<Boolean>(false)
        val isLoading: LiveData<Boolean> = _isLoading

        init {
            // Start with the current month
            val cal = Calendar.getInstance()
            _currentYear.value  = cal.get(Calendar.YEAR)
            _currentMonth.value = cal.get(Calendar.MONTH) + 1  // Calendar is 0-based
            loadAttendance()
        }

        fun goToPreviousMonth() {
            var year  = _currentYear.value  ?: return
            var month = _currentMonth.value ?: return
            month--
            if (month < 1) { month = 12; year-- }
            _currentYear.value  = year
            _currentMonth.value = month
            loadAttendance()
        }

        fun goToNextMonth() {
            var year  = _currentYear.value  ?: return
            var month = _currentMonth.value ?: return
            month++
            if (month > 12) { month = 1; year++ }
            _currentYear.value  = year
            _currentMonth.value = month
            loadAttendance()
        }

        private fun loadAttendance() {
            val year  = _currentYear.value  ?: return
            val month = _currentMonth.value ?: return

            viewModelScope.launch {
                _isLoading.value = true

                // ── STATIC DATA (replace this block with your API call) ───────────
                val data = AttendanceRepository.getStaticAttendance(year, month)
                // ── END STATIC DATA ──────────────────────────────────────────────

                // When using API, it would look like:
                // val response = apiService.getAttendance(accessToken, year, month)
                // val data = AttendanceRepository.mapApiResponse(
                //     year, month,
                //     response.presentCount, response.totalCount,
                //     response.records.map { it.day to it.status }
                // )

                _attendanceData.value = data
                _isLoading.value = false
            }
        }





    private val batchDetailsLiveData = SingleLiveEvent<Resources<BatchDetailResponse>>()

    fun getBatchDetailsLiveData(): LiveData<Resources<BatchDetailResponse>> {
        return batchDetailsLiveData
    }
    fun hitBatchDetailsApi(token: String, batch_id: String) {
        try {
            batchDetailsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    batchDetailsLiveData.postValue(
                        Resources.success(
                            ApiRepository().batchDetailsApi(token, batch_id
                            )
                        )
                    )
                } catch (ex: Exception) {
                    batchDetailsLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}