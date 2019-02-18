package com.maoxin.apkshell.kotlin

/** @author lmx
 * Created by lmx on 2019/2/17.
 * @see https://www.kotlincn.net/docs/reference/classes.html
 */
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
            aa.also{
                println("is a $it")
            }
        }
    }
}