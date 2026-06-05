package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.add_library.AddLibraryDataResponse
import com.app.gradmo.model.library_list.LibraryListRequest
import com.app.gradmo.model.library_list.LibraryListResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddLibraryViewModel @Inject constructor() : ViewModel() {

    private val addLibraryDataLiveDate = SingleLiveEvent<Resources<AddLibraryDataResponse>>()

    fun addLibraryDataLiveData(): LiveData<Resources<AddLibraryDataResponse>> = addLibraryDataLiveDate

    fun hitAddLibraryDataApi(
        token: String,
        batchId: String,
        subject: String,
        title: String,
        topic: String,
        pdfFile: File
    ) {
        addLibraryDataLiveDate.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                addLibraryDataLiveDate.postValue(
                    Resources.success(
                        ApiRepository().addLibraryDataApi(token, batchId, subject, title, topic, pdfFile)
                    )
                )
            } catch (ex: Exception) {
                addLibraryDataLiveDate.postValue(Resources.error(ex.localizedMessage, null))
            }
        }
    }
}