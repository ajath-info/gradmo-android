package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.homework.HomeworkListResponse
import com.app.gradmo.model.library_list.LibraryListRequest
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeworkViewModel @Inject constructor() : ViewModel() {
    private val homeworkLiveData = SingleLiveEvent<Resources<HomeworkListResponse>>()

    fun getHomeworkLiveData(): LiveData<Resources<HomeworkListResponse>> {
        return homeworkLiveData
    }
    fun hitHomeworkDataApi(token: String, request: LibraryListRequest) {

        try {
            homeworkLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    homeworkLiveData.postValue(
                        Resources.success(
                            ApiRepository().getHomeworkListApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    homeworkLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}