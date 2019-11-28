package com.maoxin.apkshell.kotlin

/** @author lmx
 * Created by lmx on 2019/11/28.
 */
enum class TestEnum {
    AAA, BBB, CCC
}

inline fun <reified T : Enum<T>> printEnumNames() {
    println(enumValues<T>().joinToString { it.name })
}
fun printTestEnum() {
    printEnumNames<TestEnum>()
}

fun main() {
    printTestEnum()
}