package com.maoxin.apkshell.kotlin

/** kotlin inline函数
 *  @author lmx
 * Created by lmx on 2019-08-18.
 */
fun main() {
    test_InlineFunction()
    test_InlineFunction_return()
    test_InlineFunction_return_flow()

    test_bigHigherOrderFunction()
}

fun test_InlineFunction() {
    try {
        higherOrderFunction { println("aaa") }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun test_InlineFunction_return() {
    try {
        higherOrderFunction {
            //因为inline关系，return导致内联函数被return后，下面的代码不执行
            println("bbb")
            return
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun test_InlineFunction_return_flow() {
    try {
        higherOrderFunction {
            //inline
            println("ccc")
            return@higherOrderFunction
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

inline fun higherOrderFunction(runnable: () -> Unit) {
    println("-------")
    println("1")
    runnable()
    println("2")

}

fun test_bigHigherOrderFunction() {
    bigHigherOrderFunction(
            {
                println("print first")
            },
            {
                println("print second")
                return@bigHigherOrderFunction
            },
            {
                println("print third")
                //return
                //这里不能return此函数，因为添加noinline
            }
    )
}

/**
 * noinline 强制lambda表达式 不进行inline处理，对应的方式就是翻译成内部类实现</br>
 */
inline fun bigHigherOrderFunction(noinline firstRunnable: () -> Unit, secondRunnable: () -> Unit, noinline thirdRunnable: () -> Unit) {
    println("-------")
    firstRunnable()
    secondRunnable()
    thirdRunnable()
    println("end bigHigherOrderFunction fun")
}
