package com.maoxin.apkshell

import java.lang.Exception
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/** @author lmx
 * Created by lmx on 2020/8/17.
 */
class MainRun4Lock2 {


    companion object {

        lateinit var lock: Lock
        lateinit var condition: Condition

        @JvmStatic
        fun main(args: Array<String>) {
            lock = ReentrantLock()
            condition = lock.newCondition()

            val product = Product()
            val consumer = Consumer()

            consumer.start()
            product.start()
        }

        class Consumer : Thread() {
            override fun run() {
                try {
                    lock.lock()
                    println("我在等待一个信号${this@Consumer.name}")
                    condition.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    println("拿到一个信号${this@Consumer.name}")
                    lock.unlock()
                }
            }
        }

        class Product : Thread() {
            override fun run() {
                try {
                    lock.lock()
                    println("我拿到锁${this@Product.name}")
                    sleep(2000)
                    condition.signalAll()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    lock.unlock()
                }
            }
        }
    }


}