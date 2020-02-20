package com.maoxin.apkshell.ipc.server

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import com.maoxin.apkshell.ipc.client.ClientActivity

/** @author lmx
 * Created by lmx on 2020/2/20.
 */
class JobServiceImpl : JobService() {

    private var mMessenger: Messenger? = null

    companion object {
        public val TAG = JobServiceImpl::class.java.simpleName
    }

    override fun onCreate() {
        Log.i(TAG, "onCreate")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.hasExtra("messenger") == true) {
            mMessenger = intent.getParcelableExtra("messenger")
        }
        return Service.START_NOT_STICKY
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.i(TAG, "onStopJob")

        //1、true表示进行重试
        //2、false表示不在进行重试，Job将被丢弃销毁

        sendMsg(ClientActivity.MSG_JOB_STOP, params?.jobId, mMessenger)

        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.i(TAG, "onStartJob")

        if (Looper.getMainLooper().thread.id == Thread.currentThread().id) {
            println("job onStartJob in main thread")
        } else {
            println("job onStartJob in Sub thread")
        }

        //1、如果返回false，内部开启了工作线程去处理逻辑，这个时候Job会被强制销毁，同时后台任务还在继续
        //2、如果我们再onDestroy中没有释放线程，这时候会出现内存泄露，要杜绝这种写法

        //1、如果后台需要执行简单的任务，是可以直接返回false
        //2、如果后台执行耗时操作，并且返回true，这个时候，JobSchedule不会销毁Job，会有10min时间等待，后台任务应该调用jobFinished来销毁Job

        sendMsg(ClientActivity.MSG_JOB_START, params?.jobId, mMessenger)

        val delayStopInterval: Long = params?.extras?.getLong("delay_stop") ?: 0

        if (delayStopInterval > 0) {
            Handler().postDelayed({
                sendMsg(ClientActivity.MSG_JOB_STOP, params?.jobId, mMessenger)
                jobFinished(params, false)
            }, delayStopInterval)
            return true
        }
        return false
    }

    inline fun sendMsg(what: Int, obj: Any?, msger: Messenger?) {
        msger?.also {
            val msg: Message = Message.obtain()
            msg.what = what
            msg.obj = obj
            it.send(msg)
        } ?: let {
            Log.e(TAG, "messenger is null!!!")
        }
    }
}


