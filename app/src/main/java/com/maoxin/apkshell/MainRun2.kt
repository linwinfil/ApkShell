package com.maoxin.apkshell

import android.util.LruCache
import java.security.spec.EllipticCurve

/** @author lmx
 * Created by lmx on 2020/8/21.
 */
class MainRun2 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            //都是扩容到2的n次幂
            println(tableSizeFor(10))
            println(tableSizeFor(16))
            println(tableSizeFor(29))
            println(tableSizeFor(32))
            println(tableSizeFor(54))
            println("---------")
            println(tableSizeFor(10, false))
            println(tableSizeFor(16, false))
            println(tableSizeFor(29, false))
            println(tableSizeFor(32, false))
            println(tableSizeFor(54, false))
        }

        @JvmStatic
        fun tableSizeFor(cap: Int, cut: Boolean = true): Int {
            var n = if (cut) cap - 1 else cap
            n = n or (n ushr 1)
            n = n or (n ushr 2)
            n = n or (n ushr 4)
            n = n or (n ushr 8)
            n = n or (n ushr 16)
            return if (n < 0) 1 else if (n >= 1 shl 30) 1 shl 30 else n + 1
        }
    }

}