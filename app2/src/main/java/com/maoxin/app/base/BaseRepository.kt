package com.maoxin.app.base

import android.util.Log
import com.maoxin.app.data.ResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** @author lmx
 * Created by lmx on 2020/3/4.
 */
open class BaseRepository {
    suspend fun <T : Any> request(call: suspend () -> ResponseData<T>): ResponseData<T> {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }.apply {
            when (errorCode) {
                1001 -> {
                    throw ParamsException()
                }
                0 -> {
                    Log.i("", "请求成功")
                }
            }
        }
    }
}

class ParamsException(msg: String = "params error") : Exception(msg)