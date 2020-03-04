package com.maoxin.app.login

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maoxin.app.BR
import com.maoxin.app.base.BaseViewModel
import com.maoxin.app.data.LoginData
import com.maoxin.app.data.ResponseData

/** @author lmx
 * Created by lmx on 2020/3/4.
 */

class LoginViewModel : BaseViewModel() {
    private var data: MutableLiveData<ResponseData<LoginData>> = MutableLiveData()

    private val repository: LoginRepository by lazy {
        LoginRepository()
    }

    fun getLogin(): LiveData<ResponseData<LoginData>> {
        return data
    }

    fun onLogin(username: String, password: String) = launchUI(call = {
        data.postValue(repository.onLogin(username, password))
    })

    @get:Bindable
    var username: String = ""
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.username)
            }
        }

    @get:Bindable
    var password: String = ""
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.password)
            }
        }
}