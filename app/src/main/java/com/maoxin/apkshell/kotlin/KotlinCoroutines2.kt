package com.maoxin.apkshell.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** @author lmx
 * Created by lmx on 2019-12-10.
 */


fun main() {
    /*test_mutex()*/

    test_threadLocal()
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