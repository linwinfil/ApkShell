package com.maoxin.app

import android.app.Application
import android.widget.Toast
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

    override fun onCreate() {
        super.onCreate()
        sApp = this

        SharedPreUtils.init(this, "sp_login_config")
    }
}