package com.maoxin.apkshell.utils

import android.content.Context
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

/** @author lmx
 * Created by lmx on 2020/9/7.
 */
object CommonUtils {
    /*
     ****************************************************************
     * 文件读取/文件夹创建
     ****************************************************************
     */
    fun ReadData(ins: InputStream): ByteArray? {
        val os = ByteArrayOutputStream(2048)
        val buf = ByteArray(1024)
        var out: ByteArray? = null
        var readSize: Int
        try {
            while (ins.read(buf).also { readSize = it } > -1) {
                os.write(buf, 0, readSize)
            }
            out = os.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return out
    }

    fun readAssetFile(context: Context, path: String?): ByteArray? {
        path?.apply {
            try {
                return ReadData(context.assets.open(this))
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return null
    }

}