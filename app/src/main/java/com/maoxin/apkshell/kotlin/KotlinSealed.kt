package com.maoxin.apkshell.kotlin

/** @author lmx
 * Created by lmx on 2019/11/28.
 * 密封类，不能实例化，只能用来类的受限继承结构
 */
sealed class Math(val a: Int, val b: Int) {
    class add(a: Int, b: Int) : Math(a, b)
    class minus(a: Int, b: Int) : Math(a, b)
    class times(a: Int, b: Int) : Math(a, b)
    class div(a: Int, b: Int) : Math(a, b)

}

fun eval(math: Math): Int = when (math) {
    is Math.add -> (math.a + math.b)
    is Math.minus -> (math.a - math.b)
    is Math.times -> math.a * math.b
    is Math.div -> math.a / math.b
}

fun main() {

    val eval = eval(Math.add(1, 2))
    println(eval)
}


