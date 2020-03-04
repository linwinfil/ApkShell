package com.maoxin.app

import android.app.Application

/** @author lmx
 * Created by lmx on 2020/3/4.
 */
class MyApplication : Application() {

    companion object {
        lateinit var sApp: MyApplication
    }

    fun getInstance(): MyApplication {
        return sApp
    }

    override fun onCreate() {
        super.onCreate()
        sApp = this
    }
}