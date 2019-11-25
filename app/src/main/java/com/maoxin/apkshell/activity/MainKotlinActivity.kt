package com.maoxin.apkshell.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.maoxin.apkshell.R
import com.maoxin.apkshell.kotlin.KotlinInstance
import com.maoxin.apkshell.kotlin.KotlinParams
import com.maoxin.apkshell.kotlin.LazyKotlinInstance
import org.jetbrains.anko.toast

class MainKotlinActivity : AppCompatActivity() {

    val TAG = "MainKotlinActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kotlin)

        //        val a: Int? = null
        //        a.toString()
        //
        //        val b: Int? = null
        //        Log.d(TAG, b?.toString() ?: "A")
        //
        //        val c: Int? = null
        //        Log.d(TAG, c!!.toString())


        toast(LazyKotlinInstance.instance.title())


        val function: (View) -> Unit = {
            val createKotlinParamsObject = createKotlinParamsObject()
            toast("click,$createKotlinParamsObject")

            toast("get instance:${KotlinInstance.getInstance()}")

            testNullPointCheck("88", "99")
            testNullPointCheck("ada", "aa_+a")

            testReifiedStartActivity()
        }
        findViewById<Button>(R.id.button5).setOnClickListener(function)
    }

    private fun createKotlinParamsObject(): KotlinParams {
        return KotlinParams(width = 1920, height = 1080, maxSize = 2560, outSize = 2560.0f)
    }

    fun testReifiedStartActivity() {
        startAC<MainActivity>()
    }

    inline fun <reified T : Activity> Activity.startAC() {
        startActivity(Intent(this, T::class.java))
    }


    /**
     * 空指针的判断
     */
    fun testNullPointCheck(arg1: String, arg2: String) {
        val preseInt1 = preseInt(arg1)
        val preseInt2 = preseInt(arg2)
        if (preseInt1 != null && preseInt2 != null) {
            print(preseInt1 * preseInt2)
        } else {
            print("enter $arg1 or $arg2 is non number")
        }

    }

    fun preseInt(str: String): Int? {
        return str.toInt()
    }

    /**
     * 空指针操作符
     * @see https://www.kotlincn.net/docs/reference/null-safety.html
     */
    fun testNullPoint() {
        //1，空指针的定义，需要的申明后面加上？
        var arg1: String? = "arg1"
        println("$arg1")

        arg1 = null
        println("$arg1")
        //申明了空指针属性，则访问他的属性时候，编译器警告有空指针风险
        println(arg1.toString())

        //2，假如没有指定？，则编译错误，如下所示2，访问属性，非空指针不会有异常警告
        var arg2: String = "arg2"
        println(arg2)
        //arg2 = null //编译错误

        //3，空指针的安全使用，调用安全操作符 "？"
        var a: String = "aaa"
        var b: String? = null
        println(a?.length) //无需安全调用
        println(b?.length)


        //4，elvis操作符，基本用于表达if-表达式
        val c: Int = if (b != null) b.length else -1
        val cc: Int = b?.length ?: -1

        //如果 ?: 左侧表达式非空，elvis 操作符就返回其左侧表达式，
        //否则返回右侧表达式。 请注意，当且仅当左侧为空时，才会对右侧表达式求值
        var result: String = b ?: "is a null"

        //5，!!操作符，非空断言运算符，可将任何值转换成非空类型，若该值为空则跑出空指针异常
        println(b!!.toString()) //编译后抛出空指针异常
    }

    /**
     * 类的检查与转换
     * @see https://www.kotlincn.net/docs/reference/typecasts.html
     */
    fun testIsCast() {
        var arg1: String = "aaaa"
        if (arg1 is String) {
        }

        //不安全操作符 as，因为null无法转换成string，所以要在as右边的可空类型
        var x: String? = null
        var y: String? = x as String?
        //安全操作符 as?
        var z: String? = x as? String
    }

    /**
     * for循环
     * @see https://www.kotlincn.net/docs/reference/control-flow.html#for-循环
     */
    fun testForCast() {
        var items = listOf("savsaa", "jaofoan", "anfajvbsa ")


        //indices 获取到索引
        for (index in items.indices) {
            println("item in $index is ${items[index]}")
        }
        //使用库函数，一同取出索引及值
        for ((index, value) in items.withIndex()) {
            println("item in $index is $value")
        }

        //数字迭代，区间表达式，https://www.kotlincn.net/docs/reference/ranges.html
        for (index in 1..100) {
            println("index of $index")
        }

        //区间0-6，跳过2个下标，起点开始计算
        for (index in 6 downTo 0 step 2) {
            println("index of $index")
        }
    }

    /**
     * 跳转与返回
     */
    fun testBreakStep() {

        loop@ for (index in 1..100) {
            if (index == 22) {
                continue@loop
            }
            println("for in $index")
            if (index == 55) {
                println("break in $index")
                break@loop
            }

        }

        val listOf = listOf(1, 2, 3, 4, 5, 6, 7)
        listOf.forEach {
            //跳出函数调用者
            if (it == 5) return
            println(it)
        }
        print("撒哒哒哒哒你")

        listOf.forEach lit@{
            //跳出函数lambda调用者，即foreach自身
            if (it == 3) return@lit
            println(it)
        }
        println("asasda")

        listOf.forEach {
            //也可以跳出隐式标签
            if (it == 4) return@forEach
            println(it)
        }
        println("asdfaijurebfdjfjk")
    }


}
