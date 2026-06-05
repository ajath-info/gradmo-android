package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.add_homework.AddHomeworkResponse
import com.app.gradmo.model.batch_detail.BatchDetailResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
@HiltViewModel
class CreateHomeWorkViewModel @Inject constructor() : ViewModel() {

    private val createHomeworkLiveData = SingleLiveEvent<Resources<AddHomeworkResponse>>()

    fun getCreateHomeWorkLiveData(): LiveData<Resources<AddHomeworkResponse>> = createHomeworkLiveData

    fun hitCreateHomeWorkApi(
        token: String,
        batchId: String,
        description: String,
        title: String,
        pdfFile: File
    ) {
        createHomeworkLiveData.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date())

                createHomeworkLiveData.postValue(
                    Resources.success(
                        ApiRepository().addHomeworkDataApi(
                            token = token,
                            batchId = batchId,
                            subjectId = "29",       // static for now
                            date = currentDate,
                            description = description,
                            title = title,
                            pdfFile = pdfFile
                        )
                    )
                )
            } catch (ex: Exception) {
                createHomeworkLiveData.postValue(Resources.error(ex.localizedMessage, null))
            }
        }
    }
}