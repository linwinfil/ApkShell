package com.maoxin.apkshell.ipc.client

import android.app.Activity
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.maoxin.apkshell.IWorker
import com.maoxin.apkshell.R
import com.maoxin.apkshell.ipc.Person
import com.maoxin.apkshell.ipc.server.*
import java.lang.ref.WeakReference
import java.util.*

/**
 * @see [https://zhuanlan.zhihu.com/p/35519585]
 */
class ClientActivity : AppCompatActivity() {

    companion object {
        public val MSG_JOB_START = 1
        public val MSG_JOB_STOP = 2
    }

    private var isConnection: Boolean = false
    private var personInterface: PersonInterface? = null

    private var work_isConnection: Boolean = false
    private var work_interface: IWorker? = null

    private var mJobServiceIntent: Intent? = null
    private var mJobIncomeHandler: JobIncomeHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        findViewById<Button>(R.id.btn_bind_remote_service).setOnClickListener {
            if (!isConnection) {
                bindServiceConnection()
                return@setOnClickListener
            }
            personInterface ?: return@setOnClickListener
            personInterface?.also {
                val person = Person()
                person.uid = UUID.randomUUID().toString()
                person.name = "chuck_${person.uid}"
                it.addPerson(person)
            }
        }

        findViewById<Button>(R.id.btn_bind_worker_service).setOnClickListener {
            if (!work_isConnection) {
                work_bindServiceConnenction()
                return@setOnClickListener
            }
            work_interface?.also {
                it.onEditCode("edit the Binder/IPC code", System.currentTimeMillis())
            }
        }

        findViewById<Button>(R.id.btn_start_foreground_service).setOnClickListener {
            val intent = Intent(this, ForegroundService::class.java)
            intent.action = "com.maoxin.apkshell.ipc.server.ForegroundService"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(intent)
            }
        }

        findViewById<Button>(R.id.btn_stop_foreground_service).setOnClickListener {
            val intent = Intent(this, ForegroundService::class.java)
            intent.action = "com.maoxin.apkshell.ipc.server.ForegroundService"
            this.stopService(intent)
        }


        /*<a href="http://jackzhang.info/2019/04/29/Android-Jobscheduler-%E4%BB%A5%E5%8F%8A-Android-Job/"></a>
        * */
        findViewById<Button>(R.id.btn_start_job_service).setOnClickListener {
            //启动JobService
            mJobServiceIntent?.let { return@setOnClickListener }
            mJobServiceIntent = Intent(this, JobServiceImpl::class.java)
            mJobIncomeHandler ?: also {
                mJobIncomeHandler = JobIncomeHandler(Looper.getMainLooper(), this)
            }
            val messenger = Messenger(mJobIncomeHandler)
            mJobServiceIntent!!.putExtra("messenger", messenger)
            startService(mJobServiceIntent)
        }

        findViewById<Button>(R.id.btn_start_job).setOnClickListener {
            startJobSchedule()
        }
        findViewById<Button>(R.id.btn_stop_job).setOnClickListener {
            stopJobSchedule()
        }
    }

    /**
     * 启动Job
     */
    private fun startJobSchedule() {
        val jobScheduler: JobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val builder: JobInfo.Builder = JobInfo.Builder(999, ComponentName(this, JobServiceImpl::class.java))
        // builder.setRequiresCharging(true) //充电才能运行
        // builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR) //回退/重试策略

        //设置延迟时间 setMinimumLatency(long minLatencyMillis)和
        //设置最终期限时间 setOverrideDeadline(long maxExecutionDelayMillis)的两个方法
        //不能同时与setPeriodic(long time)同时设置
        builder.setPeriodic(5000) //执行周期
        // builder.setMinimumLatency(10_000)//最小延迟毫秒
        // builder.setOverrideDeadline(30_000)//最大延迟毫秒

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) //非蜂窝网络下执行
        val bundle = PersistableBundle()

        bundle.putLong("delay_stop", 8_000)//设置启动后，延迟8秒结束
        builder.setExtras(bundle)


        val jobInfo: JobInfo = builder.build()
        val schedule: Int = jobScheduler.schedule(jobInfo)
        println("job schedule jobInfo id==${jobInfo.id}")
        if (schedule == JobScheduler.RESULT_SUCCESS) {
            println("job schedule success")
        } else {
            println("job schedule failed")
        }
    }

    /**
     * 停止Job
     */
    private fun stopJobSchedule() {
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancelAll()
        Toast.makeText(this, "job schedule cancel all", Toast.LENGTH_SHORT).show()
    }

    private class JobIncomeHandler(looper: Looper, activity: Activity) : Handler(looper) {
        private var mActivity: WeakReference<Activity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val activity: Activity? = mActivity.get()
            activity ?: return

            when (msg.what) {
                MSG_JOB_START -> {
                    Toast.makeText(activity, "job start~~~", Toast.LENGTH_SHORT).show()
                }
                MSG_JOB_STOP -> {
                    Toast.makeText(activity, "job stop~~~", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun bindServiceConnection() {
        val intent = Intent(this, RemoteService::class.java)
        intent.action = "com.maoxin.apkshell.ipc.server.RemoteService"
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
    }

    private fun work_bindServiceConnenction() {
        val intent = Intent(this, WorkerService::class.java)
        intent.action = "com.maoxin.apkshell.ipc.server.WorkerService"
        this.bindService(intent, work_serviceConnection, Service.BIND_AUTO_CREATE)
    }

    private fun work_unbindServiceConnection() {
        unbindService(work_serviceConnection)
    }

    private fun unBindServiceConnection() {
        unbindService(serviceConnection)
    }

    private var work_serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            work_isConnection = false
            println("work bind service")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            work_isConnection = true
            println("work unbind service $service")
            work_interface = IWorker.Stub.asInterface(service)
        }
    }

    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isConnection = true
            val interfaceProxy = PersonStub.asInterfaceProxy(service)
            this@ClientActivity.personInterface = interfaceProxy
            interfaceProxy?.apply {
                val persons = interfaceProxy.persons
                println("getPersons: $persons")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnection = false
        }
    }

    override fun onStart() {
        super.onStart()
        if (!this.isConnection) {
            bindServiceConnection()
        }
    }

    override fun onStop() {
        super.onStop()
        if (this.isConnection) {
            unBindServiceConnection()
        }
        if (this.work_isConnection) {
            work_unbindServiceConnection()
        }
        mJobServiceIntent?.let { stopService(it) }
        mJobServiceIntent = null
    }
}
