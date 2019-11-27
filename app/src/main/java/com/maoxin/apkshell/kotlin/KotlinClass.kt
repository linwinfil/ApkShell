package com.maoxin.apkshell.kotlin

import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/** @author lmx
 * Created by lmx on 2019/2/17.
 * @see https://www.kotlincn.net/docs/reference/classes.html
 */

fun main() {
    //1
    /*val student = KotlinClass.Student("张三", 26)
    println(student.saysomething("func"))

    val sampleClassDelegateObservable = KotlinClass.SampleClassDelegateObservable()
    sampleClassDelegateObservable.name = "aaa"
    sampleClassDelegateObservable.name = "bbb"
    sampleClassDelegateObservable.name = "ccc"
    sampleClassDelegateObservable.name = sampleClassDelegateObservable.name*/

    //2
    /*val sampleDelegateVetoable = KotlinClass.SampleDelegateVetoable()
    sampleDelegateVetoable.name = "AAA"
    sampleDelegateVetoable.name = "AABV"
    println(sampleDelegateVetoable.name)

    sampleDelegateVetoable.name = "AAA"
    println(sampleDelegateVetoable.name)*/

    //3
    /*val sampleDelegateImpl = KotlinClass.SampleDelegateImpl()
    sampleDelegateImpl.name = "AAA"
    sampleDelegateImpl.name = "AABV"
    println(sampleDelegateImpl.name)

    sampleDelegateImpl.name = "AABV"
    sampleDelegateImpl.name = "CCC"*/

    //4
    val sampleMap = KotlinClass.SampleMap(mapOf(
            "name" to "func",
            "age" to 123,
            "name" to "func——————",
            "age" to 11112
    ))
    println(sampleMap.toString())

    val sampleMapV2 = KotlinClass.SampleMapV2(hashMapOf(
            "name" to "adada",
            "age" to 1212,
            "name" to "chunk",
            "age" to 222
    ))
    println(sampleMapV2.toString())

}

class KotlinClass {

    //没有类体的class，可以不需要花括号
    class ClassEmpth

    interface IPerson {
        open fun gogogo(forWhat: Int)
        open fun saysomething(something: String)
    }

    //构造函数使用constructor申明，也可以省略
    open class Person constructor(name: String) : IPerson {
        override fun saysomething(something: String) {
            println("say something $something")
        }

        override fun gogogo(forWhat: Int) {
            println("go go go for $forWhat")
        }
    }

    //主构造函数不能放任何代码，初始化的代码可以放在“init”关键字初始化块
    class ClassInitOrder(name: String) {
        //also返回对象类型本身
        val first_str = "this is first str: $name".also(::println)

        init {
            println("first init block that prints:$name")
        }

        val second = "this is second str length: ${name.length}".also(::println)

        init {
            println("second init block that prints:${name.length}")
        }

        init {
            val aa = 5
            aa.also {
                println("is a $it")
            }
        }
    }

    /**
     * 次构造函数相当于JAVA的重写
     */
    open class Student(var name: String) {
        init {
            println("init ${this.javaClass.simpleName}")
        }

        constructor(name: String, age: Int) : this(name) {
            println("$name + $age")
        }

        constructor(name: String, age: Int, sex: String) : this(name, age) {
            println("$name + $age + $sex")
        }


        open fun saysomething(something: String) {
            println(something)
        }

        override fun toString(): String {
            return super.toString()
        }
    }

    class SubStudent(name: String) : Student(name), IPerson {

        var age = 22

        override fun saysomething(something: String) {
            super.saysomething(something)
            super<Student>.saysomething(something)
        }

        override fun gogogo(forWhat: Int) {
        }


        //内部类
        inner class InnerSubStudent(name: String) {
            private fun innserSaySomething(something: String) {
                println(something)
                super@SubStudent.saysomething(something)//调用父类SubStudent
                saysomething(something)//调用外部类
            }
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

    //不可变
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