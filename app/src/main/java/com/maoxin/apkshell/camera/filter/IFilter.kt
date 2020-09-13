package com.maoxin.apkshell.camera.filter

import android.content.Context

/** @author lmx
 * Created by lmx on 2020/9/7.
 */
interface IFilter {
    fun onCreate(context: Context)
    fun onSizeChange(context: Context, cameraWidth: Int, cameraHeight: Int, viewWidth: Int, viewHeight: Int, degree: Int, flip: Int)
    fun onDestroy(context: Context)
    fun onDraw(width: Int, height: Int, commonVao: Int, vararg textures: Int):Int
}