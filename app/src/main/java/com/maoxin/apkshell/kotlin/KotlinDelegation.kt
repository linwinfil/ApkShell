package com.maoxin.apkshell.kotlin

import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**委托机制
 * @author lmx
 * Created by lmx on 2019/11/27.
 * @see https://zhuanlan.zhihu.com/p/65914552
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

class SampleMap(map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
    override fun toString(): String {
        return "{name:$name, age:$age}"
    }
}

class SampleMapV2(map: MutableMap<String, Any?>) {
    var name: String by map
    var age: Int by map
    override fun toString(): String {
        return "{name:$name, age:$age}"
    }
}


object DelegatesImpl {
    //实现了beforeChange和aafterChange函数
    inline fun <T> vetoable4observable(initialValue: T, crossinline onChangeAfter: (property: KProperty<*>, oldValue: T, newValue: T) -> Unit,
                                       crossinline onChangeBefore: (property: KProperty<*>, oldValue: T, newValue: T) -> Boolean):
            ReadWriteProperty<Any?, T> = object : ObservableProperty<T>(initialValue) {
        override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean = onChangeBefore(property, oldValue, newValue)
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) = onChangeAfter(property, oldValue, newValue)
    }
}

class SampleClassDelegateObservable {
    /**可观察属性，在name改变后，观察者监听到并反馈到afterChange函数*/
    var name: String by Delegates.observable("init name") { property, oldValue, newValue ->
        run {
            println("<-- $oldValue, $newValue -->")
        }
    }
}

class SampleDelegateVetoable {
    /**可观察属性，在name改变后，通过beforeChange函数判断是否执行afterChange函数*/
    var name: String by Delegates.vetoable("init name", onChange = { property, oldValue, newValue ->
        return@vetoable !(oldValue === newValue)
    })
}

class SampleDelegateImpl {
    var name: String by DelegatesImpl.vetoable4observable("init name", onChangeAfter = { _, oldValue, newValue ->
        println("<-- SampleDelegateImpl#onChangeAfter:$oldValue, $newValue -->")
    }, onChangeBefore = { _, oldValue, newValue ->
        val result = !(oldValue === newValue)
        println("SampleDelegateImpl#onChangeBefore:$result")
        return@vetoable4observable result
    })
}

fun main() {

    //0
    /*val baseImpl = BaseImpl(10)
    val derived = Derived(baseImpl)

    println(baseImpl)
    println(derived)*/

    //1
    /*val sampleClassDelegateObservable = SampleClassDelegateObservable()
    sampleClassDelegateObservable.name = "aaa"
    sampleClassDelegateObservable.name = "bbb"
    sampleClassDelegateObservable.name = "ccc"
    sampleClassDelegateObservable.name = sampleClassDelegateObservable.name*/

    //2
    /*val sampleDelegateVetoable = SampleDelegateVetoable()
    sampleDelegateVetoable.name = "AAA"
    sampleDelegateVetoable.name = "AABV"
    println(sampleDelegateVetoable.name)

    sampleDelegateVetoable.name = "AAA"
    println(sampleDelegateVetoable.name)*/

    //3
    /*val sampleDelegateImpl = SampleDelegateImpl()
    sampleDelegateImpl.name = "AAA"
    sampleDelegateImpl.name = "AABV"
    println(sampleDelegateImpl.name)

    sampleDelegateImpl.name = "AABV"
    sampleDelegateImpl.name = "CCC"*/

    //4
    /*val mapOf = mapOf<String, Any?>(Pair("name", "func"), Pair("age", 123), Pair("name", "func-------"), Pair("age", 1212121))
    val sampleMap = SampleMap(mapOf)
    println(sampleMap.toString())

    val toMutableMap = mapOf.toMutableMap()
    val sampleMapV2 = SampleMapV2(toMutableMap)
    println(sampleMapV2.toString())*/


}