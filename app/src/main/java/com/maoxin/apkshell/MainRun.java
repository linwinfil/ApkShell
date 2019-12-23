package com.maoxin.apkshell;

import com.maoxin.apkshell.classLoader.TestClassLoader;

import java.lang.reflect.Method;

/**
 * @author lmx
 * Created by lmx on 2019/12/20.
 */
public class MainRun {

    public static void main(String[] args) {
        // testClassLoader();
        // testClassLoader2();
        testThreadClassLoaderContext();
    }

    private static void testClassLoader() {
        String projectRootDir = TestClassLoader.getProjectRootDir();
        System.out.println(projectRootDir);
        TestClassLoader loader = new TestClassLoader();
        loader.setPath(projectRootDir);
        try {
            Class<?> aClass = loader.loadClass("Test");
            Object o = aClass.newInstance();
            System.out.println("o instanceof java.lang.Object:" + (o instanceof Object));
            System.out.println(o);
            System.out.println(o.getClass().getClassLoader());
            System.out.println("=======");
            ClassLoader classLoader = TestClassLoader.class.getClassLoader();
            System.out.println(classLoader);
            System.out.println("=======");
            System.out.println(classLoader.getClass().getClassLoader());

            Method add = aClass.getDeclaredMethod("add");
            add.setAccessible(true);
            add.invoke(o);

            Method add2 = aClass.getMethod("add2");
            add2.invoke(o);


            System.out.println("(=====)");

            TestClassLoader loader2 = new TestClassLoader();
            loader2.setPath(projectRootDir);
            Class<?> aClass2 = loader2.loadClass("Test");
            Object o2 = aClass2.newInstance();
            System.out.println(o2);
            System.out.println("o2 instanceof java.lang.Object:" + (o2 instanceof Object));
            System.out.println("o1 == o2:" + (o == o2));
            System.out.println("o1 equals o2:" + (o.equals(o2)));
            System.out.println(loader2);
            System.out.println(o2.getClass());

            System.out.println("[=====]");
            Method setTest = aClass.getMethod("setTest", aClass);
            setTest.setAccessible(true);
            setTest.invoke(o, o2);//fixme error

            Method printSetText = aClass.getMethod("printSetText");
            printSetText.invoke(o);


            System.out.println("+=====+");
            Method setObject = aClass.getMethod("setObject", Object.class);
            setObject.invoke(o, o2);//fixme error

            Method printSetObject = aClass.getMethod("printSetObject");
            printSetObject.invoke(o);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testClassLoader2() {
        String projectRootDir = TestClassLoader.getProjectRootDir();
        TestClassLoader testClassLoader = new TestClassLoader();
        testClassLoader.setPath(projectRootDir);

        try {
            Class<?> string = testClassLoader.loadClass("String");//error
            Object o = string.newInstance();
            System.out.println(o);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testThreadClassLoaderContext() {
        Thread thread = new Thread(() -> {

        });
        ClassLoader contextClassLoader = thread.getContextClassLoader();
        if (contextClassLoader != null) {
            System.out.println(contextClassLoader);
        }
    }
}
