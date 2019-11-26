package com.maoxin.apkshell.kotlin

/** kotlin inline函数，其实就是拆封函数方法里面的调用，封装到一个fun中，不产生实例对象
 * @see <a href="https://droidyue.com/blog/2019/04/27/lambda-inline-noinline-crossinline/"></a>
 *  @author lmx
 * Created by lmx on 2019-08-18.
 *
 * 避免内联过大的函数，因为内联会导致代码增加
 */
fun main() {
    test_InlineFunction()
    test_InlineFunction_return()
    test_InlineFunction_return_flow()

    test_bigHigherOrderFunction()

    test_reifiedInlineFunction()
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
                return@bigHigherOrderFunction//结束当前runnable的方法，继续执行往后的内联

                return Unit//return 当前函数方法的执行，往后的内联就不会执行了
            },
            {
                println("print third")
                //return
                //这里不能return此函数，因为添加noinline
            }
    )
}

fun test_reifiedInlineFunction() {
    reifiedInlineFunction(
            {
                println("print first line ")
            },
            {
                println("print second line")
            },
            "this is third line"
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

@PublishedApi
internal val taskLevel = 1
val taskCount = 1

/**
 *reified相当于泛型
 * inline函数无法访问private修饰属性，解决此方法是增加public、或者internal（限定在同一模块访问）增加@PublishedApi注解（推荐）
 */
inline fun <reified T> reifiedInlineFunction(noinline firstRunnable: () -> Unit, secondRunnable: () -> Unit, params: T?) {
    println("-------")
    firstRunnable()
    secondRunnable()
    println(" ---> $params <---")
    println(" ---> $taskLevel <---")
    println(" ---> $taskCount <---")
}
