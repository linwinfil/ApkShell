package com.maoxin.app.login

import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maoxin.app.BR
import com.maoxin.app.base.BaseViewModel
import com.maoxin.app.data.LoginData
import com.maoxin.app.data.ResponseData
import com.maoxin.app.utils.CommonUtils

/** @author lmx
 * Created by lmx on 2020/3/4.
 */

class LoginViewModel : BaseViewModel() {
    companion object {
        const val TAG = "LoginViewModel"
    }

    private var data: MutableLiveData<ResponseData<LoginData>> = MutableLiveData()

    private val repository: LoginRepository by lazy {
        LoginRepository()
    }

    fun getLogin(): LiveData<ResponseData<LoginData>> {
        return data
    }

    fun onLogin(username: String, password: String) = launchUI(block = {
        Log.i(TAG, "onLogin block in ui thread:${CommonUtils.IsUiThread()}")
        val response: ResponseData<LoginData> = repository.onLogin(username, password)
        data.postValue(response)//执行完成后将结果发送到UI线程通知
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