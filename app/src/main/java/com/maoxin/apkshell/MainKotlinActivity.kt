package com.maoxin.apkshell

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.maoxin.apkshell.kotlin.KotlinInstance
import com.maoxin.apkshell.kotlin.KotlinParams
import com.maoxin.apkshell.kotlin.LazyKotlinInstance
import org.jetbrains.anko.toast

class MainKotlinActivity : AppCompatActivity() {

    val TAG = "MainKotlinActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kotlin)

//        val a: Int? = null
//        a.toString()
//
//        val b: Int? = null
//        Log.d(TAG, b?.toString() ?: "A")
//
//        val c: Int? = null
//        Log.d(TAG, c!!.toString())


        toast(LazyKotlinInstance.instance.title())


        val function: (View) -> Unit = {
            val createKotlinParamsObject = createKotlinParamsObject()
            toast("click,$createKotlinParamsObject")

            toast("get instance:${KotlinInstance.getInstance()}")
        }
        findViewById<Button>(R.id.button5).setOnClickListener(function)
    }

    private fun createKotlinParamsObject(): KotlinParams {
        return KotlinParams(width = 1920, height = 1080, maxSize = 2560, outSize = 2560.0f)
    }

}
