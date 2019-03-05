package com.maoxin.apkshell;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class Main12Activity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main12);

        findViewById(R.id.tv_test_task_stack_builder).setOnClickListener(v -> test_TaskStackBuilder());
    }

    private void test_TaskStackBuilder()
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent();
        intent.setClass(this, Main12Activity.class);

        //创建返回栈
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        //添加activity到返回栈
        taskStackBuilder.addParentStack(Main12Activity.class);
        //添加intent到栈顶
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "test_task_stack_channel_id").setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round)).setSmallIcon(getApplicationInfo().icon).setWhen(System.currentTimeMillis()).setAutoCancel(true)//自己维护统治
                .setContentTitle("TaskStackBuilder返回栈测试").setTicker("TaskStackBuilder返回栈测试_Ticker").setContentText("暂无内容").setContentIntent(pendingIntent);
        builder.setFullScreenIntent(pendingIntent, true);
        Notification notification = builder.build();
        notificationManager.notify(0, notification);
    }
}
