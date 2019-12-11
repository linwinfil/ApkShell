package com.maoxin.apkshell.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/** @author lmx
 * Created by lmx on 2019-12-10.
 * @see [https://www.kotlincn.net/docs/reference/coroutines/shared-mutable-state-and-concurrency.html]
 */


fun main() {

    /*test_normal()*/

    test_atomic()

    /*test_mutex()*/

    /*test_threadLocal()*/
}
//volatile 变量保证可线性化读取和写入变量，但在大量动作（在我们的示例中即“递增”操作）
@Volatile
var counter = 0

//同步原子锁
var counterAtomic : AtomicInteger = AtomicInteger(0)

fun test_normal() = runBlocking {

    counter=0
    withContext(Dispatchers.Default) {
        val n = 50
        val k = 50

        val times = measureTimeMillis {
            coroutineScope {
                repeat(n) {
                    launch {
                        repeat(k) {
                            counter++
                            /*println("counting... $counter")*/
                        }
                    }
                }
            }
        }

        println("Completed $n * $k=${n * k} actions in $times ms")
    }

    println("counter=$counter")
}

fun test_atomic() = runBlocking {

    counterAtomic.set(0)

    withContext(Dispatchers.Default) {
        val n = 100
        val k = 100

        val times = measureTimeMillis {
            coroutineScope {
                repeat(n) {
                    launch {
                        repeat(k) {
                            counterAtomic.incrementAndGet()
                            /*println("counting... $counter")*/
                        }
                    }
                }
            }
        }

        println("Completed $n * $k=${n * k} actions in $times ms")
    }

    println("counter=${counterAtomic.get()}")
}


fun test_mutex() = runBlocking {

    //互斥锁，类似synchronized
    val mutex = Mutex()
    var count = 0
    repeat(100) {
        GlobalScope.launch {
            //synchronized(this@runBlocking) {
            //    count++
            //    println("the count $count")
            //}
            mutex.withLock {
                count++
                println("the count $count")
            }
        }

    }
}

fun test_threadLocal() = runBlocking {
    val threadLocal = ThreadLocal<String>().apply { set("Init") }
    printlnValue(threadLocal, "first")
    val job = GlobalScope.launch(threadLocal.asContextElement("launch")) {
        printlnValue(threadLocal, "second")
        threadLocal.set("launch changed")
        printlnValue(threadLocal, "third")
        yield()
        printlnValue(threadLocal, "four")
    }
    job.join()
    printlnValue(threadLocal)
}

private fun printlnValue(threadLocal: ThreadLocal<String>, tag: String = "") {
    println("${Thread.currentThread()} thread local value: ${threadLocal.get()} [$tag]")
}