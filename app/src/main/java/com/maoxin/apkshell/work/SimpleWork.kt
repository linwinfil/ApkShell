package com.maoxin.apkshell.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

/** @author lmx
 * Created by lmx on 2020/8/9.
 */
class SimpleWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        //处理一些后台任务代码，比如上传日志

        return doWorkImpl()
    }

    fun doWorkImpl(): Result {
        println("something do working success")

        return Result.success()
    }
}
