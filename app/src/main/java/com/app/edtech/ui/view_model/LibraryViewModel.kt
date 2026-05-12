package com.app.edtech.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edtech.model.address.city.GetCitiesRequest
import com.app.edtech.model.address.city.GetCitiesResponse
import com.app.edtech.model.library_list.LibraryListRequest
import com.app.edtech.model.library_list.LibraryListResponse
import com.app.edtech.network_call.repository.ApiRepository
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor() : ViewModel() {
    private val libraryLiveDate = SingleLiveEvent<Resources<LibraryListResponse>>()

    fun getLibraryLiveData(): LiveData<Resources<LibraryListResponse>> {
        return libraryLiveDate
    }
    fun hitLibraryDataApi(token: String, request: LibraryListRequest) {

        try {
            libraryLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    libraryLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getLibraryListApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    libraryLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }




    private val citiesLiveDate = SingleLiveEvent<Resources<GetCitiesResponse>>()

    fun getCitiesLiveData(): LiveData<Resources<GetCitiesResponse>> {
        return citiesLiveDate
    }
    fun hitCitiesDataApi(request: GetCitiesRequest) {

        try {
            citiesLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    citiesLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getCitiesApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    citiesLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}