package com.maoxin.apkshell.hook.ams;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.maoxin.apkshell.activity.MainOPActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.maoxin.apkshell.utils.MLog.TAG;

/**
 * @author lmx
 * Created by lmx on 2020/5/17.
 */
public class MockClass1 implements InvocationHandler {

    private Object mInstance;

    MockClass1(Object mInstance) {
        this.mInstance = mInstance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("startActivity")) {
            Intent raw = null;
            int index = 0;

            for (int i = 0; i < args.length; i++) {
                Object obj;
                if ((obj = args[i]) instanceof Intent) {
                    index = i;
                    raw = (Intent) obj;
                    break;
                }
            }
            if (raw == null) {
                return method.invoke(mInstance, args);
            }

            Intent newIntent = new Intent();
            // 替身Activity的包名, 也就是我们自己的包名
            String stubPackage = raw.getComponent().getPackageName();
            // 这里我们把启动的Activity临时替换为 MainOPActivity（已经在manifest中注册了）
            ComponentName componentName = new ComponentName(stubPackage, MainOPActivity.class.getName());
            newIntent.setComponent(componentName);

            // 把我们原始要启动的TargetActivity先存起来
            newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, raw);

            // 替换掉Intent, 达到欺骗AMS的目的
            args[index] = newIntent;

            Log.d(TAG, "hook success");
            return method.invoke(mInstance, args);
        }

        return method.invoke(mInstance, args);
    }
}
