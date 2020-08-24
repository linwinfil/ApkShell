package com.maoxin.apkshell.hook.ams;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;

import com.maoxin.apkshell.hook.RefInvoke;

import java.lang.reflect.Proxy;

import androidx.annotation.NonNull;

/**
 * @author lmx
 * Created by lmx on 2020/5/17.
 */
public class MockClass2 implements Handler.Callback {
    private Handler mH;

    MockClass2(Handler mH) {
        this.mH = mH;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            // ActivityThread里面 "LAUNCH_ACTIVITY" 这个字段的值是100
            // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
            case 100:
                handleLaunchActivity(msg);
                break;

        }
        mH.handleMessage(msg);
        return true;
    }

    private void handleLaunchActivity(Message msg) {
        // 这里简单起见,直接取出TargetActivity;
        Object obj = msg.obj;

        // 把替身恢复成真身
        Intent intent = (Intent) RefInvoke.getFieldObject(obj, "intent");

        Intent targetIntent = intent.getParcelableExtra(AMSHookHelper.EXTRA_TARGET_INTENT);
        intent.setComponent(targetIntent.getComponent());


        //修改packageName，这样缓存才能命中
        ActivityInfo activityInfo = (ActivityInfo) RefInvoke.getFieldObject(obj, "activityInfo");
        activityInfo.applicationInfo.packageName = targetIntent.getPackage() == null ? targetIntent.getComponent().getPackageName() : targetIntent.getPackage();

        try {
            hookPackageManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hookPackageManager() throws Exception {
        // 这一步是因为 initializeJavaContextClassLoader 这个方法内部无意中检查了这个包是否在系统安装
        // 如果没有安装, 直接抛出异常, 这里需要临时Hook掉 PMS, 绕过这个检查.
        Object currentActivityThread = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread");

        // 获取ActivityThread里面原始的 sPackageManager
        Object sPackageManager = RefInvoke.getFieldObject(currentActivityThread, "sPackageManager");

        // 准备好代理对象, 用来替换原始的对象
        Class<?> iPackageManagerInterface = RefInvoke.getClassForName("android.content.pm.IPackageManager");
        Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(), new Class<?>[]{iPackageManagerInterface}, new MockClass3(sPackageManager));

        // 1. 替换掉ActivityThread里面的 sPackageManager 字段
        RefInvoke.setFieldObject(currentActivityThread, "sPackageManager", proxy);
    }
}
