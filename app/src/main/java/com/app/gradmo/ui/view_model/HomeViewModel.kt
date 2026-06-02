package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.banner.response.BannerResponse
import com.app.gradmo.model.batch_list.BatchListRequest
import com.app.gradmo.model.batch_list.BatchListResponse
import com.app.gradmo.model.institute_detail.request.InstituteDetailRequest
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.model.institute_list.request.InstitutesListRequest
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.model.third_party_credentials.ThirdPartyCredentialsResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
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




    private val thirdPartyCredentialsLiveDate = SingleLiveEvent<Resources<ThirdPartyCredentialsResponse>>()

    fun getThirdPartyCredentialsLiveData(): LiveData<Resources<ThirdPartyCredentialsResponse>> {
        return thirdPartyCredentialsLiveDate
    }
    fun hitThirdPartyCredentialsDataApi(token: String) {

        try {
            thirdPartyCredentialsLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    thirdPartyCredentialsLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getThirdPartyCredentialsApi(token)
                        )
                    )
                } catch (ex: Exception) {
                    thirdPartyCredentialsLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }




    private val batchesLiveDate = SingleLiveEvent<Resources<BatchListResponse>>()

    fun getBatchesLiveData(): LiveData<Resources<BatchListResponse>> {
        return batchesLiveDate
    }
    fun hitBatchesDataApi(token: String, request: BatchListRequest) {

        try {
            batchesLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    batchesLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getBatchListApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    batchesLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}