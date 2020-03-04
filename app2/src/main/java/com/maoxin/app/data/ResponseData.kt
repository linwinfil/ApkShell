package com.maoxin.app.data

/** @author lmx
 * Created by lmx on 2020/3/4.
 */
data class ResponseData<out T>(val errorCode: Int, val errorMsg: String, val data: T)