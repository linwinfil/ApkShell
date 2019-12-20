package com.maoxin.apkshell.classLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lmx
 * Created by lmx on 2019/12/20.
 * <a href="https://zhuanlan.zhihu.com/p/31182000"/>
 */
public class TestClassLoader extends ClassLoader {

    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] loadClassData = loadClassData(name);
        if (loadClassData == null) {
            throw new ClassNotFoundException("not found " + name);
        }
        else {
            return defineClass(name, loadClassData, 0, loadClassData.length);
        }
    }

    /**
     * 加载类的二进制流
     *
     * @param className
     * @return
     */
    private byte[] loadClassData(String className) {
        File f = new File(path, className.substring(className.lastIndexOf('.') + 1) + ".class");
        try (InputStream input = new FileInputStream(f)) {
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length;
                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }
                return output.toByteArray();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getProjectRootDir() {
        return "C:\\Users\\jhon\\Desktop\\";
    }
}
