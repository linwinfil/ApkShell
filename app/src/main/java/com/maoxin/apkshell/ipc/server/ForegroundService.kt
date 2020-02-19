package com.maoxin.apkshell.ipc.server

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.maoxin.apkshell.ipc.Person
import java.util.*

/** @author lmx
 * Created by lmx on 2020/2/19.
 */
class ForegroundService : Service() {
    private val TAG: String = "ForegroundService"
    private var mList: ArrayList<Person> = ArrayList()

    override fun onBind(intent: Intent?): IBinder? {
        return mStub
    }

    override fun onCreate() {
        super.onCreate()

        val person = Person()
        person.uid = UUID.randomUUID().toString()
        person.name = "chuck"
        println(person.toString())
        mList.add(person)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {



        val builder: NotificationCompat.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this, TAG)
        } else {
            NotificationCompat.Builder(this)
        }
        builder.setContentTitle("标题，前台服务")
        builder.setContentText("内容，前台服务！")
        builder.setOngoing(false)
        builder.setAutoCancel(true)
        val aintent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com"))
        aintent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 9990, aintent, PendingIntent.FLAG_UPDATE_CURRENT, null)
        builder.setContentIntent(pendingIntent)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(TAG, "前台服务通知", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        this.startForeground(999, builder.build())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    private var mStub = object : PersonStub() {
        override fun addPerson(person: Person?) {
            person?.let { mList.add(it) }
        }

        override fun getPersons(): List<Person> {
            return mList
        }

    }
}