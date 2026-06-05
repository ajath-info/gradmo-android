package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.attendence.AttendanceListRequest
import com.app.gradmo.model.attendence.AttendanceListResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.util.Calendar

class MarkAttendanceViewModel : ViewModel() {

    private val getAttendanceLiveData = SingleLiveEvent<Resources<AttendanceListResponse>>()

    fun getAttendanceLiveDataLiveData(): LiveData<Resources<AttendanceListResponse>> {
        return getAttendanceLiveData
    }

    fun hitAttendanceLiveDataDataApi(token: String, request: AttendanceListRequest) {
        try {
            getAttendanceLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    getAttendanceLiveData.postValue(
                        Resources.success(
                            ApiRepository().getAttendanceApi(token, request)
                        )
                    )
                } catch (ex: Exception) {
                    getAttendanceLiveData.postValue(Resources.error(ex.localizedMessage, null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}