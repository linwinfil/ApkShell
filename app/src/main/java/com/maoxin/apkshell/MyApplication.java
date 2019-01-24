package com.maoxin.apkshell;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.mmkv.MMKV;

/**
 * @author lmx
 * Created by lmx on 2019/1/17.
 */
public class MyApplication extends Application
{
    private static MyApplication sApp = null;

    public static MyApplication getInstance() {
        return sApp;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        sApp = this;

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }

        String initialize = MMKV.initialize(this);
        System.out.println("MMKV root dir:" + initialize);


    }
}
