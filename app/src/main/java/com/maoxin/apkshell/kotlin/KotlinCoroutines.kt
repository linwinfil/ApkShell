package com.maoxin.apkshell.kotlin

import kotlinx.coroutines.*
import org.jetbrains.annotations.TestOnly
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.measureTimeMillis

/** @author lmx
 * Created by lmx on 2019/12/2.
 *
 * @see https://www.cnblogs.com/mengdd/p/kotlin-coroutines-basics.html
 * @see http://mouxuejie.com/blog/2019-05-23/kotlin-coroutines-basic/
 * @see https://johnnyshieh.me/posts/kotlin-coroutine-introduction/  协程原理解析
 * 协程可以看作是能被挂起、不阻塞线程的计算，协程的挂起几乎没有代价，没有上下文切换，不需要虚拟机和操作系统的支持。
 * 协程挂起通过suspend函数实现，suspend函数用状态机的方式用挂起点将协程的运算逻辑拆分为不同的片段，每次运行协程执行不同的逻辑片段。
 * 线程上下文切换开销大，依赖于虚拟机和操作系统
 */

suspend fun main() {
    /*test_suspend_launch()*/

    test_launch()


    /*val measureTimeMillis = measureTimeMillis {
        repeat(3) {
            test_runBloking(it)
            println("-----------")
            if (it == 2) {
                println("repeat finish")
            }
        }
    }
    println("test_runBloking : $measureTimeMillis")*/

    /*test_runBloking2()*/

    /*test_yield()*/
    /*test_yield2()*/

    /*test_cancel()*/

    /*test_coroutineScope()*/

    /*test_CoroutineScope2()*/

    /*test_composing_coroutineScope {}*/

    /*test_dispatcher()*/
}

suspend fun test_launch() {
    println("aa")
    val scope = GlobalScope.launch(Dispatchers.Main) {
        delay(1000)
        println("bb")
    }
//    scope.join()
    println("ccc")
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

/**
 * 创建新的协程，并阻塞当前线程，知道当前协程结束
 */
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

@TestOnly
fun test_runBloking2() = runBlocking {
    launch {
        println("start") //2
        delay(2000)
        println("delay") //3
    }

    println("aaa") //1
    delay(4000)
}

@TestOnly
suspend fun test_withContext(dispatcher: CoroutineDispatcher) {
    withContext(dispatcher) {

    }
}

/**
 * async和launch 区别在于，async是带有deferred返回值
 */
@TestOnly
fun test_async() = runBlocking {
    val async_deferred = async { }
}

@TestOnly
suspend fun test_yield() = runBlocking {
    launch {
        /*delay(3000)*/
        println("delay ing...")
    }

    launch {
        //yield 将协程逻辑分发到Dispatcher队列中，让出当前的线程或线程池运行其他的协程逻辑，
        //在Dispatcher空闲时执行回原来的协程，简单来说就是交出执行权
        yield()
        printThreadName()
        delay(3000)
        println("yield ing ...")
    }
}

@TestOnly
suspend fun test_yield2() = runBlocking {
    launch {
        repeat(3) {
            println("job1 repeat index:$it")
            yield()
        }
    }


    launch {
        repeat(3) {
            println("job2 repeat index:$it")
            yield()
        }
    }
}

@TestOnly
suspend fun test_withtimeout() {
    try {
        withTimeout(1300) {
            //超时抛异常
            repeat(3) {
                delay(1200)
                println("job3 repeat index:$it")
            }
        }
    } catch (th: Throwable) {
        th.printStackTrace()
    } finally {
        println("...")
    }
}

suspend fun test_cancel() = runBlocking {
    launch {
        repeat(20) {
            when (it) {
                8 -> cancel()
            }
            println("$it")

            //delay 会检测当前协程是否被取消，如果已经取消，则中断内部逻辑运算，
            //如果没有调用delay，内部运算逻辑正常运行
            delay(500)
        }
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
    println("thread ${tag ?: ""}--> ${Thread.currentThread().name}")
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



