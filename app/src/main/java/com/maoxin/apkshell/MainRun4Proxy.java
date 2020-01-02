package com.maoxin.apkshell;

import com.maoxin.apkshell.classLoader.TestClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lmx
 * Created by lmx on 2019/12/25.
 */
public class MainRun4Proxy {

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println(getClass().getSimpleName() + ", finalize");
    }

    public static void main(String[] args) {
        testProxyInterfaces();
        // testProxyClassLoader();
        // testGCbug();
    }


    private static void testGCbug() {
        MainRun4Proxy a = new MainRun4Proxy();
        for (int i = 0; i < 1_000; i++) {
            if (i % 100 == 0) System.gc();
        }
        System.out.println("ok");
        //如果a没有被引用到，频繁GC导致a被finalize
        /*System.out.println(a);*/
    }

    private static void testProxyInterfaces() {
        ActiveImpl active = new ActiveImpl();//监听，打出log，跟proxy无关系

        ArrayList<Object> target = new ArrayList<>();

        List proxyInstance = (List) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //method 被代理对象的接口方法，args 接口方法设置的参数
                Object invoke = method.invoke(target, args);
                active.invokeMethod(method, args);
                return invoke;
            }
        });
        proxyInstance.add("func");
        proxyInstance.add("big func");
        proxyInstance.add(2019);
        proxyInstance.set(1, "set to small func");

        System.out.println("class loader:" + proxyInstance.getClass().getClassLoader());
        System.out.println("isEmpty:" + proxyInstance.isEmpty());
        System.out.println("size:" + proxyInstance.size());
        System.out.println(proxyInstance.toString());
    }


    private static void testProxyClassLoader() {//自定义加载外部字节码，动态代理调用方法
        try {
            ActiveImpl active = new ActiveImpl();//监听，打出log，跟proxy无关系

            TestClassLoader testClassLoader = new TestClassLoader();
            Class<?> loadClass = testClassLoader.loadClass("Test");
            Object o = loadClass.newInstance();
            Object proxyInstance = Proxy.newProxyInstance(testClassLoader, o.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Object invoke = method.invoke(o, args);
                    active.invokeMethod(method, args);
                    return invoke;
                }
            });

            System.out.println(proxyInstance);
            System.out.println("===");
            Method iTestInvovke = loadClass.getMethod("iTestInvovke", String[].class);
            String[] strings = {"A", "B", "C"};
            iTestInvovke.invoke(o, (Object) strings);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void testProxyClass() {
        ActiveImpl active = new ActiveImpl();
        try {
            Class<?> aClass = Proxy.getProxyClass(ActiveImpl.class.getClassLoader(), ActiveImpl.class.getInterfaces());
            Constructor<?> constructor = aClass.getConstructor(InvocationHandler.class);
            ActiveImpl proxy = (ActiveImpl) constructor.newInstance(new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return null;
                }
            });
            proxy.invokeMethod(null, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface IActive {
        void invokeMethod(Method method, Object[] args);
    }

    private static class ActiveImpl implements IActive {
        @Override
        public void invokeMethod(Method method, Object[] args) {
            System.out.println("invokeMethod:" + method.toString() + " -- " + Arrays.toString(args));
        }
    }
}
