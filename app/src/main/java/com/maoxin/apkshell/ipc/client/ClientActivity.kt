package com.maoxin.apkshell.ipc.client

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.maoxin.apkshell.R
import com.maoxin.apkshell.ipc.Person
import com.maoxin.apkshell.ipc.server.PersonInterface
import com.maoxin.apkshell.ipc.server.PersonStub
import com.maoxin.apkshell.ipc.server.RemoteService
import java.util.*

/**
 * @see [https://zhuanlan.zhihu.com/p/35519585]
 */
class ClientActivity : AppCompatActivity() {

    private var isConnection: Boolean = false
    private var personInterface: PersonInterface? = null

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
    }


    private fun bindServiceConnection() {
        val intent = Intent(this, RemoteService::class.java)
        intent.action = "com.maoxin.apkshell.ipc.server"
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
    }

    private fun unBindServiceConnection() {
        unbindService(serviceConnection)
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
    }
}
