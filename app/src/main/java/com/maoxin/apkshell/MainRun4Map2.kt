package com.maoxin.apkshell

import java.util.*

/** @author lmx
 * Created by lmx on 2020/8/19.
 */
class MainRun4Map2 {
    //栈的逆序
    fun getBottomAndRemove(stack: Stack<Int>): Int {
        val result = stack.pop() //弹出栈顶
        if (stack.isEmpty()) return result
        val bottom = getBottomAndRemove(stack)
        stack.push(result)
        return bottom
    }

    fun reverseStack(stack: Stack<Int>) {
        if (stack.isEmpty()) {
            return
        } else {
            val bottomAndRemove = getBottomAndRemove(stack)
            reverseStack(stack)
            stack.push(bottomAndRemove)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var stack: Stack<Int> = Stack()
            for (i in 1..3) {
                stack.push(i)
            }

            val testStack = MainRun4Map2()
            testStack.reverseStack(stack)
            while (!stack.isEmpty()) {
                println(stack.pop())
            }
        }
    }
}