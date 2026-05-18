package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.address.city.GetCitiesRequest
import com.app.gradmo.model.address.city.GetCitiesResponse
import com.app.gradmo.model.exam_list.ExamsListRequest
import com.app.gradmo.model.exam_list.UpcomingExamListResponse
import com.app.gradmo.model.institute_list.request.InstitutesListRequest
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.model.library_list.LibraryListRequest
import com.app.gradmo.model.video_lecture.VideoLectureListResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpcomingExamsViewModel @Inject constructor() : ViewModel() {
    private val upcomingExamsLiveData = SingleLiveEvent<Resources<UpcomingExamListResponse>>()

    fun getUpcomingExamsLiveData(): LiveData<Resources<UpcomingExamListResponse>> {
        return upcomingExamsLiveData
    }
    fun hitUpcomingExamsDataApi(token: String, request: ExamsListRequest) {

        try {
            upcomingExamsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    upcomingExamsLiveData.postValue(
                        Resources.success(
                            ApiRepository().getUpcomingExamsListApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    upcomingExamsLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}