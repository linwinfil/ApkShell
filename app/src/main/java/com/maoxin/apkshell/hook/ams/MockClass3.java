package com.maoxin.apkshell.hook.ams;

import android.content.pm.PackageInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author lmx
 * Created by lmx on 2020/5/23.
 */
public class MockClass3 implements InvocationHandler {

    Object base;

    MockClass3(Object base) {
        this.base = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("getPackageInfo")) {
            return new PackageInfo();
        }
        return method.invoke(base, args);
    }
}
