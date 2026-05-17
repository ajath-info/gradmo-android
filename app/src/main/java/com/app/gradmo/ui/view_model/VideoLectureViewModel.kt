package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.library_list.LibraryListRequest
import com.app.gradmo.model.video_lecture.VideoLectureListResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoLectureViewModel @Inject constructor() : ViewModel() {
    private val videoLectureLiveData = SingleLiveEvent<Resources<VideoLectureListResponse>>()

    fun getVideoLectureLiveData(): LiveData<Resources<VideoLectureListResponse>> {
        return videoLectureLiveData
    }
    fun hitVideoLectureDataApi(token: String, request: LibraryListRequest) {

        try {
            videoLectureLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    videoLectureLiveData.postValue(
                        Resources.success(
                            ApiRepository().getVideoLectureListApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    videoLectureLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}