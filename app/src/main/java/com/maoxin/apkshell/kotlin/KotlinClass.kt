package com.maoxin.apkshell.kotlin

/** @author lmx
 * Created by lmx on 2019/2/17.
 * @see https://www.kotlincn.net/docs/reference/classes.html
 */

fun main() {
    //1
    val student = KotlinClass.Student("张三", 26)
    println(student.saysomething("func"))

    val intArray = IntArray(10)
    for (i in 0..9) {
        intArray[i] = i + 1
    }
    println(intArray.toList())
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
}

