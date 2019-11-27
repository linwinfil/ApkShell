package com.maoxin.apkshell.kotlin

/** @author lmx
 * Created by lmx on 2019/11/27.
 */
interface Base {
    fun print()
    fun printLine()
}

class BaseImpl(private val x: Int) : Base {
    override fun print() {
        print(x)
    }

    override fun printLine() {
        println(x)
    }
}

class Derived(b: Base) : Base by b {
    override fun printLine() {
        println("Derived override print line!")//重写此函数，则弃用委托的函数调用
    }
}


fun main() {

    val baseImpl = BaseImpl(10)
    val derived = Derived(baseImpl)

    println(baseImpl)
    println(derived)
}