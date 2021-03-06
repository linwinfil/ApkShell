package com.maoxin.app

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.maoxin.app.box.MyObjectBox
import com.maoxin.app.utils.SharedPreUtils

/** @author lmx
 * Created by lmx on 2020/3/4.
 */
class MyApplication : Application() {

    companion object {
        public val TAG = MyApplication::class.java.simpleName

        @JvmStatic
        var sApp: MyApplication? = null

        @JvmStatic
        fun getInstance(): MyApplication? {
            return sApp
        }

        @JvmStatic
        fun showToast(msg: String) {
            Toast.makeText(getInstance(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    lateinit var mMainHandler: Handler

    override fun onCreate() {
        super.onCreate()
        sApp = this

        MyObjectBox.init(this)
        SharedPreUtils.init(this, "sp_login_config")
        mMainHandler = Handler(Looper.getMainLooper())
    }
}