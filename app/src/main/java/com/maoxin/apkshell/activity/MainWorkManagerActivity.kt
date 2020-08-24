package com.maoxin.apkshell.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.work.*
import com.maoxin.apkshell.R
import com.maoxin.apkshell.generated.callback.OnClickListener
import com.maoxin.apkshell.work.SimpleWork
import java.util.concurrent.TimeUnit

/**
 * 讲解:<a href="https://juejin.im/post/6844903768627249159"/>
 */
class MainWorkManagerActivity : AppCompatActivity() {
    var workRequest: WorkRequest? = null
    var workRequestPeriod: WorkRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_work_manager)

        findViewById<Button>(R.id.btn_start_work).setOnClickListener {

            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED) //网络连接设置
                    .setRequiresBatteryNotLow(false) //是否为低电量时运行 默认false
                    .setRequiresCharging(false) //是否要插入设备（接入电源），默认false
                    // .setRequiresDeviceIdle(false) //设备是否为空闲，默认false
                    .setRequiresStorageNotLow(false) //设备可用存储是否不低于临界阈值
                    .build()

            //单次任务
            workRequest = OneTimeWorkRequest.Builder(SimpleWork::class.java)
                    .setConstraints(constraints)
                    .build()
            WorkManager.getInstance().enqueue(workRequest!!)
            WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest!!.id).observe(this@MainWorkManagerActivity, Observer {
                if (it?.state!!.isFinished) {
                    println("work is finished")
                } else {
                    println("work name:" + (it.state.name))
                }
            })

            //重复任务
            workRequestPeriod = PeriodicWorkRequest.Builder(SimpleWork::class.java, 10, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .build()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        workRequest?.also {
            WorkManager.getInstance().cancelWorkById(it.id)
        }
    }
}