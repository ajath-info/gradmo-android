package com.app.edtech.ui.signup.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.edtech.network_call.repository.ApiRepository
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import com.app.hihlo.model.login.response.LoginResponse
import com.app.hihlo.ui.signup.model.SignUp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterationViewModel @Inject constructor(application: Application): AndroidViewModel(application) {

    private val RegisterLiveDate = SingleLiveEvent<Resources<LoginResponse>>()

    fun getRegisterLiveData(): LiveData<Resources<LoginResponse>> {
        return RegisterLiveDate
    }

    fun hitRegisterUser(model: SignUp) {

        try {
            RegisterLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    RegisterLiveDate.postValue(
                        Resources.success(
                            ApiRepository().registerUser(model)
                        )
                    )


                } catch (ex: Exception) {
                    RegisterLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}