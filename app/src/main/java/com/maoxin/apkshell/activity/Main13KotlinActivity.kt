package com.maoxin.apkshell.activity

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.maoxin.apkshell.R

class Main13KotlinActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main13_kotlin)

        printInternalStorage()
        printExternalStorage()
    }


    private fun printInternalStorage() {
        println(filesDir)//内部存储文件目录
        println(cacheDir)//内部缓存目录
        println(noBackupFilesDir)//内部存储不被自动备份目录
        println(codeCacheDir)//内部存储代码缓存目录
    }

    private fun printExternalStorage() {
        //外部存储目录
        println(getExternalFilesDir(null))
        println(getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        println(getExternalFilesDir(Environment.DIRECTORY_MOVIES))
        println(getExternalFilesDir(Environment.DIRECTORY_MUSIC))
    }

    private fun test_externalStorageDirectory() {
        Environment.getExternalStorageDirectory()
    }
}
