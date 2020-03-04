package com.maoxin.app.login

import com.maoxin.app.base.BaseRepository
import com.maoxin.app.data.LoginData
import com.maoxin.app.data.ResponseData
import com.maoxin.app.server.RetrofitFactory

/** @author lmx
 * Created by lmx on 2020/3/4.
 */
class LoginRepository : BaseRepository() {
    suspend fun onLogin(username: String, password: String): ResponseData<LoginData> = request {
        RetrofitFactory.reqApi.login(username, password)
    }
}