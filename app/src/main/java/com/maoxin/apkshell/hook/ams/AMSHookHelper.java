package com.maoxin.apkshell.hook.ams;

import android.annotation.SuppressLint;
import android.os.Handler;

import com.maoxin.apkshell.hook.RefInvoke;

import java.lang.reflect.Proxy;

/**
 * @author lmx
 * Created by lmx on 2020/5/17.
 * @see <a href="https://github.com/BaoBaoJianqiang/Hook31/">demo链接</a>
 */
public class AMSHookHelper {

    final static String EXTRA_TARGET_INTENT = "EXTRA_TARGET_INTENT";

    public static void hookAMS() throws Exception {
        Object getService = RefInvoke.getStaticFieldObject("android.app.ActivityManager", "getService");

        Object mInstance = RefInvoke.getFieldObject("android.util.Singleton", getService, "mInstance");

        @SuppressLint("PrivateApi") Class<?> classB2Interface = Class.forName("android.app.IActivityManager");

        Object proxy = Proxy.newProxyInstance(classB2Interface.getClassLoader(), new Class[]{classB2Interface}, new MockClass1(mInstance));

        RefInvoke.setFieldObject("android.util.Singleton", getService, "mInstance", proxy);
    }


    public static void hookActivityThread() throws Exception {
        Object sCurrentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");

        // 由于ActivityThread一个进程只有一个,我们获取这个对象的mH
        Handler mH = (Handler) RefInvoke.getFieldObject(sCurrentActivityThread, "mH");

        //把Handler的mCallback字段，替换为new MockClass2(mH)
        RefInvoke.setFieldObject(Handler.class, mH, "mCallback", new MockClass2(mH));
    }
}
