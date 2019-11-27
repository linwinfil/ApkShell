package com.maoxin.apkshell.kotlin

/** @author lmx
 * Created by lmx on 2019/11/27.
 */

fun main() {
    val mCompanionClazz: IFactory<CompanionClazz> = CompanionClazz
    println(mCompanionClazz.toString())
    println(CompanionClazz.doingSomething())

    println(CompanionClazzV2.doingSomething("func"))
    println(CompanionClazzV2.size)
}

interface IFactory<T> {
    fun create(): T

}

class CompanionClazz {

    companion object : IFactory<CompanionClazz> {
        override fun create(): CompanionClazz {
            return CompanionClazz()
        }

        fun doingSomething() {
        }
    }
}

class CompanionClazzV2 {
    companion object {
        @JvmField
        val size = 100

        //在companion object中的公共函数必须用使用 @JvmStatic 注解才能暴露为静态方法
        //如果没有这个注解，这些函数仅可用作静态 Companion 字段上的实例方法
        @JvmStatic
        fun doingSomething(something: String) {
            println("doing something $something")
        }
    }
}