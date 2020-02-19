package com.maoxin.apkshell.ipc.client

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.maoxin.apkshell.IWorker
import com.maoxin.apkshell.R
import com.maoxin.apkshell.ipc.Person
import com.maoxin.apkshell.ipc.server.*
import java.util.*

/**
 * @see [https://zhuanlan.zhihu.com/p/35519585]
 */
class ClientActivity : AppCompatActivity() {

    private var isConnection: Boolean = false
    private var personInterface: PersonInterface? = null

    private var work_isConnection: Boolean = false
    private var work_interface: IWorker? = null

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
    }
}
