package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.InstituteCityResponse
import com.app.gradmo.model.address.city.GetCitiesRequest
import com.app.gradmo.model.address.city.GetCitiesResponse
import com.app.gradmo.model.banner.response.BannerResponse
import com.app.gradmo.model.institute_list.request.InstitutesListRequest
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchInstituteViewModel @Inject constructor() : ViewModel() {

    private val bannerLiveDate = SingleLiveEvent<Resources<BannerResponse>>()

    fun getBannerLiveData(): LiveData<Resources<BannerResponse>> {
        return bannerLiveDate
    }
    fun hitBannerDataApi(token: String) {

        try {
            bannerLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    bannerLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getBannerApi(token
                            )
                        )
                    )
                } catch (ex: Exception) {
                    bannerLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }



    private val institutesLiveDate = SingleLiveEvent<Resources<InstituteListResponse>>()

    fun getInstitutesLiveData(): LiveData<Resources<InstituteListResponse>> {
        return institutesLiveDate
    }
    fun hitInstitutesDataApi(token: String, request: InstitutesListRequest) {

        try {
            institutesLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    institutesLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getInstitutesApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    institutesLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }




    private val citiesLiveDate = SingleLiveEvent<Resources<InstituteCityResponse>>()

    fun getCitiesLiveData(): LiveData<Resources<InstituteCityResponse>> {
        return citiesLiveDate
    }
    fun hitCitiesDataApi(token: String) {

        try {
            citiesLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    citiesLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getInstituteCitiesApi(token)
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