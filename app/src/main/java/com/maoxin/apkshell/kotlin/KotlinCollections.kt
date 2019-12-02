package com.maoxin.apkshell.kotlin

import java.util.*

/** @author lmx
 * Created by lmx on 2019/11/29.
 */
fun main() {
    /*test_same_map()*/

    /*test_iterator()*/

    test_group()
}

private fun test_same_map() {
    val numbersMap = mapOf("key1" to 1, "key2" to 2, "key3" to 3, "key4" to 1)
    val anotherMap = mapOf("key2" to 2, "key1" to 1, "key4" to 1, "key3" to 3)
    //to符号创建了一个短时存活的Pair对象，因此建议仅在性能不重要时才使用它

    val anotherMap2 = mutableMapOf<String, Any?>().apply {
        this["key1"] = 1
        this["key2"] = 2
        this["key3"] = 3
        this["key4"] = 1
    }

    val emptyMap = emptyMap<String, Any?>()



    println("The maps are equal: ${numbersMap == anotherMap}")//键值对一致，equals一致
    println("the maps are is: ${numbersMap === anotherMap}")

    println("numbersMap == anotherMap2 equals: ${numbersMap == anotherMap}")
}

fun test_iterator() {
    val numbersMap = mutableMapOf<String, Any?>("key1" to 1, "key2" to 2, "key3" to 3, "key4" to 1)//可变的map

    val iterator = numbersMap.iterator()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next.key == "key4") {
            iterator.remove()
        }
    }
    println(numbersMap)
}

fun test_group() {
    //分组操作，按照首字母划分
    val numbers = listOf("one", "two", "three", "four", "five")

    val groupBy = numbers.groupBy { it.first().toUpperCase() }
    println(groupBy)

    val groupBy1 = numbers.groupBy(keySelector = { it.first() }, valueTransform = { it.toUpperCase(Locale.getDefault()) })
    println(groupBy1)
}

