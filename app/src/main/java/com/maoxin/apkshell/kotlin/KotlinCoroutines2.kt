package com.maoxin.apkshell.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
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

    /*test_atomic()*/

    /*test_mutex()*/

    /*test_threadLocal()*/

    test_actor()
}

//volatile 变量保证可线性化读取和写入变量，但在大量动作（在我们的示例中即“递增”操作）
@Volatile
var counter = 0

//同步原子锁
var counterAtomic: AtomicInteger = AtomicInteger(0)

fun test_normal() = runBlocking {

    counter = 0
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

fun test_actor() = runBlocking {
    val counter = counterActor() // 创建该 actor
    withContext(Dispatchers.Default) {
        massiveRun {
            counter.send(IncCounter)
        }
    }
    // 发送一条消息以用来从一个 actor 中获取计数值
    val response = CompletableDeferred<Int>()
    counter.send(GetCounter(response))
    println("Counter = ${response.await()}")
    counter.close() // 关闭该actor
}

sealed class CounterMsg
object IncCounter : CounterMsg()
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg()

suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100  // 启动的协程数量
    val k = 1000 // 每个协程重复执行同个动作的次数
    val time = measureTimeMillis {
        coroutineScope {
            // 协程的作用域
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
    println("Completed ${n * k} actions in $time ms")
}

fun counterActor() = GlobalScope.actor<CounterMsg> {
    var counter = 0 // actor 状态
    for (msg in channel) { // 即将到来消息的迭代器
        when (msg) {
            is IncCounter -> counter++
            is GetCounter -> msg.response.complete(counter)
        }
    }
}

fun test_mutex() = runBlocking {

    //互斥锁，类似synchronized
    val mutex = Mutex()
    var count = 0
    repeat(100) {
        println("repeat index:$it")
        GlobalScope.launch {
            //synchronized(this@runBlocking) {
            //    count++
            //    println("the count $count")
            //}
            mutex.withLock {
                //当前unit被锁住互斥
                count++
                println("the count $count")
            }
        }

    }

    println("count:$count")
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