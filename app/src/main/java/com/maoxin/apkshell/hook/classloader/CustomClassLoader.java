package com.maoxin.apkshell.hook.classloader;

import dalvik.system.DexClassLoader;

/**
 * @author lmx
 * Created by lmx on 2020/5/18.
 */
public class CustomClassLoader extends DexClassLoader {

    public CustomClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
    }
}
