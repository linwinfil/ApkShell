package com.maoxin.apkshell.activity

import android.os.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.maoxin.apkshell.MyApplication
import com.maoxin.apkshell.R
import java.lang.reflect.Field

class MainToastActivity : AppCompatActivity() {

    companion object {
        private var sField_TN: Field? = null
        private var sField_TN_Handler: Field? = null

        init {
            try {
                //Android 8.0（26）及以上已经修复Toast的BadWindowTokenException
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    sField_TN = Toast::class.java.getDeclaredField("mTN")
                    sField_TN?.also {
                        it.isAccessible = true
                        sField_TN_Handler = it.type.getDeclaredField("mHandler")
                        sField_TN_Handler!!.isAccessible = true
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_toast)

        findViewById<Button>(R.id.btn_show_toast).setOnClickListener {
            val toast = Toast.makeText(this@MainToastActivity, "弹弹弹-${System.currentTimeMillis()}", Toast.LENGTH_SHORT)
            MyApplication.sMainHandler.postDelayed({
                println("is destroy:${this@MainToastActivity.isDestroyed}")
                toast.show()
            }, 10_000)

            finish()
        }

        findViewById<Button>(R.id.btn_hook_toast).setOnClickListener {
            val toast = Toast.makeText(this@MainToastActivity, "hook 弹弹弹-${System.currentTimeMillis()}", Toast.LENGTH_SHORT)
            hook(toast)
            MyApplication.sMainHandler.postDelayed({
                println("is destroy:${this@MainToastActivity.isDestroyed}")
                toast.show()
            }, 10_000)
            finish()
        }
    }


    /**
     * @param toast
     * @see [使用定义的Handler代理Toast中消息分发的handler，在dispatchMessage中try-catch避免由于toast中taoken失效后抛出的异常](http://www.10tiao.com/html/223/201801/2651232846/1.html)
     * NOTE 此hock 会触发 Android 9.0的非SDK接口限制机制，Accessing hidden field Landroid/widget/Toast$TN;->mHandler:Landroid/os/Handler;
     */
    private fun hook(toast: Toast) {
        try {
            if (sField_TN != null && sField_TN_Handler != null) {
                val tn = sField_TN!!.get(toast)
                val preHandler = sField_TN_Handler!!.get(tn) as Handler
                sField_TN_Handler!!.set(tn, ToastSafelyHandlerWrapper(preHandler))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private class ToastSafelyHandlerWrapper internal constructor(private val impl: Handler?) : Handler() {
        override fun dispatchMessage(msg: Message) {
            try {
                super.dispatchMessage(msg)
            } catch (ex: Exception) {
                ex.printStackTrace()
                println(" toast handler cache exception!")
            }
        }

        override fun handleMessage(msg: Message) {
            //需要委托给原Handler执行
            impl?.handleMessage(msg)
        }

    }
}
