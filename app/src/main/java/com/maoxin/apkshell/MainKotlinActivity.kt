package com.maoxin.apkshell

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
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


        findViewById<Button>(R.id.button5).setOnClickListener { toast("click") }
    }

}
