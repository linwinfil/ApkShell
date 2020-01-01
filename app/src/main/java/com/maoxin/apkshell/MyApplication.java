package com.maoxin.apkshell;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.meituan.android.walle.ChannelInfo;
import com.meituan.android.walle.WalleChannelReader;
import com.squareup.leakcanary.LeakCanary;

import java.util.Map;

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

        //ARouter路由
        if (BuildConfig.DEBUG) {
            //必须在init之前设置
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(this);


        ChannelInfo channelInfo = WalleChannelReader.getChannelInfo(this);
        if (channelInfo != null) {
            Map<String, String> extraInfo = channelInfo.getExtraInfo();
            if (extraInfo != null) {
                for (Map.Entry<String, String> entry : extraInfo.entrySet()) {
                    System.out.println(entry.getKey() + " == " + entry.getValue());
                }
            }
            System.out.println(channelInfo.getChannel());//输出的渠道
        }
    }
}
