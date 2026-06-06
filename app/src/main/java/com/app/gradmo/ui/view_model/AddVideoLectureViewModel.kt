package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.add_library.AddLibraryDataResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddVideoLectureViewModel @Inject constructor() : ViewModel() {

    private val addVideoLectureLiveData = SingleLiveEvent<Resources<AddLibraryDataResponse>>()

    fun addVideoLectureLiveData(): LiveData<Resources<AddLibraryDataResponse>> = addVideoLectureLiveData

    fun hitAddVideoLectureApi(
        token: String,
        batchId: String,
        subject: String,
        title: String,
        topic: String,
        description: String,
        previewType: String = "url",
        videoFile: File
    ) {
        addVideoLectureLiveData.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                addVideoLectureLiveData.postValue(
                    Resources.success(
                        ApiRepository().addVideoLectureApi(
                            token, batchId, subject, title, topic, description, previewType, videoFile
                        )
                    )
                )
            } catch (ex: Exception) {
                addVideoLectureLiveData.postValue(Resources.error(ex.localizedMessage, null))
            }
        }
    }
}