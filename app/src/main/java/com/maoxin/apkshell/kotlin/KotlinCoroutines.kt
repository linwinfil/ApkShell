package com.maoxin.apkshell.kotlin

import kotlinx.coroutines.*
import org.jetbrains.annotations.TestOnly
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.measureTimeMillis

/** @author lmx
 * Created by lmx on 2019/12/2.
 */

suspend fun main() {
    /*test_suspend_launch()*/


    /*repeat(3) {
        test_runBloking(it*//*, true*//*)
        if (it == 2) {
            println("repeat finish")
        }
    }*/

    /*test_coroutineScope()*/

    /*test_CoroutineScope2()*/

    test_composing_coroutineScope(true, true)
}

suspend fun test_suspend_launch() {
    val job = GlobalScope.launch {
        delay(1500)//非阻塞式
        println("delay 1s to print this")
    }
    job.join()//等待job线程执行结束

    println("test_suspend111")
    Thread.sleep(2000)//阻塞
    println("test_suspend222")


    //runBlocking阻塞主线程，阻塞直至内部执行完成
    runBlocking {
        delay(3000)
        println("runBlocking print")
    }
}

@TestOnly
suspend fun test_suspend(): String {
    delay(2000)
    /*println("test_suspend over")*/
    return "test_suspend over"
}

@TestOnly
fun test_runBloking(index: Int, withIO: Boolean = false) = runBlocking {
    printThreadName()
    if (withIO) {
        withContext(Dispatchers.IO) {
            testSuspend(index)
        }
    } else {
        testSuspend(index)
    }
}

private suspend inline fun testSuspend(index: Int) {
    printThreadName()
    println("test_runBloking start $index")
    val testSuspend = test_suspend()
    println("$testSuspend $index")
}


@TestOnly
fun test_coroutineScope() = runBlocking {

    coroutineScope {
        printThreadName()
        //coroutineScope等待所有子协程执行完毕时不会阻塞当前线程
        launch {
            printThreadName()
            delay(1000)
            println("AAA")
        }
        println("DDD")

        delay(500)
        println("BBB")
    }

    printThreadName()
    println("CCC")
}

fun printThreadName() {
    println("thread --> ${Thread.currentThread().name}")
}

fun test_CoroutineScope2() {
    val coroutineScope = CoroutineScope(EmptyCoroutineContext)

    //1
    coroutineScope.launch(Dispatchers.IO) {
        //切到线程执行

        launch(Dispatchers.Main) {
            //切到主线程执行
        }
    }

    //2
    coroutineScope.launch(Dispatchers.Main) {
        //线程执行后，回调结果
        withContext(Dispatchers.IO) {
            //切换到IO 线程，并在执行完成后切回 UI 线程
            //do in 1
            printThreadName()
        }

        var result = getXXXInIO()//io函数，执行后拿到结果

        //do in 2
    }
}

suspend fun getXXXInIO(): Int = withContext(Dispatchers.IO) {
    // 一些耗时操作
    return@withContext 1
}


fun test_composing_coroutineScope(withAsync: Boolean = false, throwsEx :Boolean = false) = runBlocking {
    //如果其中一个子协程发生异常失败中断，等待中的父线程也会被取消
    val measureTimeMillis = measureTimeMillis {
        if (withAsync) {
            val doA = async { doA() }//默认 lazy 延迟启动，需要手动start，如果没有start，await内部实现start
            val doB = async(start = CoroutineStart.DEFAULT) { doB(throwsEx) }
            println("A+B:${(doA.await() + doB.await())}")

        } else {
            val doA = doA()
            val doB = doB(throwsEx)
            println("A+B:${(doA + doB)}")
        }
    }
    println("completed in time:$measureTimeMillis")

}

suspend fun doA(): Int {
    delay(1000)
    return 22
}

suspend fun doB(throwsEx: Boolean = false): Int {
    if (throwsEx) {
        throw IllegalStateException()
    }
    delay(1200)
    return 11
}



