package com.maoxin.apkshell.kotlin

/**懒汉式单例
 * 1、显式声明构造方法为private
 * 2、companion object用在申明class内部一个对象
 * 3、LazyKotlinInstance的实例instance通过lazy来实现懒汉式加载
 * 4、lazy默认情况下是线程安全的，这就可以避免多个线程同时访问生成多个实例的问题
 * @author lmx
 * Created by lmx on 2019/1/31.
 */
class LazyKotlinInstance private constructor() {
    fun title(): String {
        return "LazyKotlinInstance"
    }

    companion object {
        val instance: LazyKotlinInstance by lazy { LazyKotlinInstance() }

    }
}