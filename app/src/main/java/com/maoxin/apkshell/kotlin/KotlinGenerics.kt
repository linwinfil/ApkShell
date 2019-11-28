package com.maoxin.apkshell.kotlin

/** @author lmx
 * Created by lmx on 2019/11/28.
 * 泛型
 */

interface IAA {
    fun aaa()
}

interface IBB {
    fun bb()
}

interface Producer<out T> {
    fun produce(): T
}

interface comsumer<in T> {
    fun comsume(producer: T)
}

class AABBImpl : IAA, IBB {
    override fun aaa() {
        println("AABBImpl aaa")
    }

    override fun bb() {
        println("AABBImpl bbb")
    }
}

class AAImpl : IAA {
    override fun aaa() {
        println("AAImple aa")
    }
}

class BBImple : IBB {
    override fun bb() {
        println("BBImple bb")
    }
}

//多重上界
class AABBTest<T>(private val t: T?) where T : IAA, T : IBB {
    fun run() {
        t?.aaa() ?: also { println("t is null") }
        t?.bb()
    }
}


fun main() {

    // out T ==> ? extends
    // in T ==> ? super
    val aabbTest = AABBTest(AABBImpl())
    aabbTest.run()

    val aabbTest1 = AABBTest(null)
    aabbTest1.run()

}

