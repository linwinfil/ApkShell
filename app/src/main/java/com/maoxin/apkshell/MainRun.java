package com.maoxin.apkshell;

import com.maoxin.apkshell.classLoader.TestClassLoader;

import java.lang.reflect.Method;

/**
 * @author lmx
 * Created by lmx on 2019/12/20.
 */
public class MainRun {

    public static void main(String[] args) {
        String projectRootDir = TestClassLoader.getProjectRootDir();
        System.out.println(projectRootDir);
        TestClassLoader loader = new TestClassLoader();
        loader.setPath(projectRootDir);
        try {
            Class<?> aClass = loader.loadClass("Test");
            Object o = aClass.newInstance();
            System.out.println(o);


            Method add = aClass.getDeclaredMethod("add");
            add.setAccessible(true);
            add.invoke(o);

            Method add2 = aClass.getMethod("add2");
            add2.invoke(o);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
