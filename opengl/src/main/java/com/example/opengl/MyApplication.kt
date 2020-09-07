package com.example.opengl

import android.app.Application

/** @author lmx
 * Created by lmx on 2020/9/6.
 */
class MyApplication : Application() {

    companion object {
        @JvmStatic
        lateinit var sApp: MyApplication

        @JvmStatic
        fun getInstance(): MyApplication {
            return sApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        sApp = this
    }
}