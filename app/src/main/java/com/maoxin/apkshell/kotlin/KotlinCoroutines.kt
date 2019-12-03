package com.maoxin.apkshell.kotlin

import kotlinx.coroutines.*
import org.jetbrains.annotations.TestOnly
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.measureTimeMillis

/** @author lmx
 * Created by lmx on 2019/12/2.
 *
 * @see {https://www.cnblogs.com/mengdd/p/kotlin-coroutines-basics.html}
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

    test_composing_coroutineScope {

    }

    /*test_dispatcher()*/
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
    val block: suspend CoroutineScope.() -> Unit = {
        delay(3000)
        println("runBlocking print")
    }
    runBlocking(block = block)
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

    //coroutineScope等待所有子协程执行完毕时不会阻塞当前线程
    coroutineScope {
        printThreadName()
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

fun printThreadName(tag: Any? = null) {
    println("thread $tag--> ${Thread.currentThread().name}")
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


fun test_composing_coroutineScope(withAsync: Boolean = false, throwsEx: Boolean = false, aaa: Any? = null, block: (ada: Boolean) -> Unit) = runBlocking {
    //如果其中一个子协程发生异常失败中断，等待中的父线程也会被取消
    println("${withAsync},${throwsEx},${aaa}")
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

fun test_dispatcher() = runBlocking<Unit> {
    /**
     * @see [Dispatchers.Unconfined]调度器在程序运行到第一个挂起点时，在调度线程中启动；
     * 它将在挂起函数执行的线程中恢复，恢复的线程完全取决于该挂起函数在哪个线程执行。
     */
    launch(Dispatchers.Unconfined) {
        printThreadName(1)
        //aapit
        // app2

        delay(1000)
        printThreadName(2)

    }
    launch {
        printThreadName(3)
        delay(1800)
        printThreadName(4)
    }
    launch(Dispatchers.IO) {
        printThreadName(5)
    }
    launch(Dispatchers.Default) {
        printThreadName(6)
        printThreadName(7)
        printThreadName(8)
    }
}



