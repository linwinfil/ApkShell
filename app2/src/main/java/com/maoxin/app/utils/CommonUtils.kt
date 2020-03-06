package com.maoxin.app.utils

import android.os.Looper

/** @author lmx
 * Created by lmx on 2020/3/6.
 */
class CommonUtils {
    companion object {
        @JvmStatic
        fun IsUiThread(): Boolean {
            return Looper.getMainLooper().thread.id == Thread.currentThread().id
        }
    }

}