package com.maoxin.apkshell.kotlin

/** @author lmx
 * Created by lmx on 2019/2/17.
 * @see https://www.kotlincn.net/docs/reference/classes.html
 */

fun main() {
    val student = KotlinClass.Student("张三", 26)
}

class KotlinClass {

    //没有类体的class，可以不需要花括号
    class ClassEmpth

    //构造函数使用constructor申明，也可以省略
    class Person constructor(name: String)

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

    class SubStudent(name: String) : Student(name) {
        override fun saysomething(something: String) {
            super.saysomething(something)
        }
    }
}