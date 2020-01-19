package com.maoxin.apkshell;

import android.app.Application;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.meituan.android.walle.ChannelInfo;
import com.meituan.android.walle.WalleChannelReader;
import com.squareup.leakcanary.LeakCanary;

import java.util.Map;

import butterknife.ButterKnife;

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
    public Object getSystemService(String name) {
        Object systemService = super.getSystemService(name);
        //通过伪装代理，try了addview的操作方法，以防止由activity销毁后taost的bad token exception问题
        //当时这样不排除厂商对windowmanager的重写，增加抽象方法
        if (systemService instanceof WindowManager && Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            WindowManager w = (WindowManager) systemService;
            return new WindowManager() {
                @Override
                public Display getDefaultDisplay() {
                    return w.getDefaultDisplay();
                }

                @Override
                public void removeViewImmediate(View view) {
                    w.removeViewImmediate(view);
                }

                @Override
                public void addView(View view, ViewGroup.LayoutParams params) {
                    try {
                        w.addView(view, params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
                    w.updateViewLayout(view, params);
                }

                @Override
                public void removeView(View view) {
                    w.removeView(view);
                }
            };
        }
        return systemService;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        sApp = this;

        //泄露检测
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }

        //ButterKnife
        if (BuildConfig.DEBUG) {
            ButterKnife.setDebug(true);
        }

        //ARouter路由
        if (BuildConfig.DEBUG) {
            //必须在init之前设置
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(this);


        //美团打包输出
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
