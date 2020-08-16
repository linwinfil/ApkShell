package com.maoxin.app.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.os.Looper
import android.util.Base64
import android.util.DisplayMetrics
import javax.annotation.Nonnull

/** @author lmx
 * Created by lmx on 2020/3/6.
 */
object CommonUtils {

    @JvmStatic
    fun IsUiThread(): Boolean {
        return Looper.getMainLooper().thread == Thread.currentThread()
    }

    @JvmStatic
    fun base64Encode(string: String): String {
        return Base64.encodeToString(string.toByteArray(), Base64.DEFAULT)
    }

    @JvmStatic
    fun base64Decode(string: String): String {
        return String(Base64.decode(string, Base64.DEFAULT))
    }

    @JvmStatic
    var sNonCompatDensity: Float = 0f
    @JvmStatic
    var sNonCompatScaleDensity: Float = 0f
    /**
     * 今日头条屏幕适配方案
     * https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA
     * px = dp * density
     * 以设计宽度360dp基准得到指定density
     */
    @JvmStatic
    fun setCustomDensity(@Nonnull application: Application,
                         @Nonnull activity: Activity) {
        val appDisplayMetrics: DisplayMetrics = application.resources.displayMetrics
        if (sNonCompatDensity == 0f) {
            sNonCompatDensity = appDisplayMetrics.density
            sNonCompatScaleDensity = appDisplayMetrics.scaledDensity
            application.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onLowMemory() {}
                override fun onConfigurationChanged(newConfig: Configuration) {
                    if (newConfig.fontScale > 0) {
                        sNonCompatScaleDensity = application.resources.displayMetrics.scaledDensity
                    }
                }
            })
        }
        val targetDensity = appDisplayMetrics.widthPixels / 360f
        val targetScaleDensity = targetDensity * (sNonCompatScaleDensity / sNonCompatDensity)
        val targetDensityDpi = (160 * targetDensity).toInt()

        appDisplayMetrics.density = targetDensity
        appDisplayMetrics.scaledDensity = targetScaleDensity
        appDisplayMetrics.densityDpi = targetDensityDpi

        val atyDisplayMetrics = activity.resources.displayMetrics
        atyDisplayMetrics.density = targetDensity
        atyDisplayMetrics.scaledDensity = targetScaleDensity
        atyDisplayMetrics.densityDpi = targetDensityDpi
    }

}