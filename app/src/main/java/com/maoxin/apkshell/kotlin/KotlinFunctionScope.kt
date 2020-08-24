package com.maoxin.apkshell.kotlin

/**
 * kotlin作用域函数
 *  @author lmx
 * Created by lmx on 2019-08-15.
 */
fun main() {
    // testFunScope_Run()
    // testFunScope_With()
    // testFunScope_Apply()
    // testFunScope_Let()
    // testFunScope_Also()
    // testFunScope_takeIf()
}

/**
 * 作用域 run
 */
fun testFunScope_Run() {
    println("---this is testFunScope_Run---")
    var test = "i'm chuck"
    test.run {
        println("print $this") //这里打印test本身
        reversed()
    }.run {
        println("print $this") //这里打印的是上次reversed返回后的结果
        length
    }.run {
        println("print $this") // 这里打印的是上次调用length返回后的结果
    }
}

fun testFunScope_With() {
    println("---this is testFunScope_With---")

    var test = "she is a lisa"
    //执行with括号中的对象
    with(with(with(test) {
        println("print $this")
        reversed() //执行完成后返回结果给下个with执行
    }) {
        println("print $this")
        length //执行完成后返回结果给下个with执行
    }) {
        println("print $this")
    }
}

fun testFunScope_Apply() {
    println("---this is testFunScope_Apply---")

    var test = "something interesting!"
    //apply 都是返回test对象本身
    test.apply {
        println("print $this")
        reversed()
        toByteArray()
    }.apply {
        println("print $this")
        length
        reversed()
    }.apply {
        println("print $this")
    }
}

fun testFunScope_Let() {
    println("---this is testFunScope_Let---")
    var test = "yep yep ype"
    //it 都是当前let执行后返回的结果
    test.let {
        println("print $it")
        it.reversed()
        it.toByteArray()
    }.let {
        println("print $it")
        it.size
    }.let {
        println("print $it")
    }
}

fun testFunScope_Also() {
    println("---this is testFunScope_Also---")
    //also 都是返回test对象本身
    var test = "nothing to do"
    test.also {
        println("print $it")
        it.reversed()
    }.also {
        println("print $it")
        it.length
    }.also {
        println("print $it")
    }
}

fun testFunScope_takeIf() {
    val test = "is takeIf"
    val result = test.takeIf { it.length > 100 }
    println("$test is $result")

    val test2 = 100L
    val result2 = test2.takeIf { it > 100 }
    val result3 = test2.takeUnless { it > 100 }
    println("$test2 is $result2, $result3")

}

class TestSub {
    private var _size: Int = 0 //幕后属性
    val size get() = _size++
}


