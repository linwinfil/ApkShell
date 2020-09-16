package com.maoxin.app.box

import android.content.Context
import com.maoxin.app.BuildConfig
import com.maoxin.app.data.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import io.objectbox.android.ObjectBoxLiveData

/** @author lmx
 * Created by lmx on 2020/8/16.
 */
object MyObjectBox {
    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox
                .builder()
                .androidContext(context.applicationContext)
                .build()

        if (BuildConfig.DEBUG) {
            val start = AndroidObjectBrowser(boxStore).start(context.applicationContext)
            println("android object browser $start")
        }
    }
}