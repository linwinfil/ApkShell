package com.maoxin.apkshell.kotlin

/** @author lmx
 * Created by lmx on 2019/7/19.
 */


var dd : Int? = 1000_000

fun main(args: Array<String>) {

    var a: Int? = 1000_000
    var b: Int? = 1000_000
    val c: Int = 1000_000
    println("a==b ${a == b}")
    println("a===b ${a === b}")
    println("dd===a ${dd === a}")
}