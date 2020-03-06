package com.maoxin.apkshell.activity

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.maoxin.apkshell.R
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


/**
 * https://blog.csdn.net/weixin_34186950/article/details/88015332
 */
class MainHookActivity : AppCompatActivity() {
    companion object {
        val TAG = MainHookActivity::class.java.simpleName

        @JvmStatic
        fun getActivityClassImpl(context: Context): Class<*>? {
            var result: Class<*>? = null
            val packageManager: PackageManager = context.packageManager
            try {
                val intent = Intent()
                intent.setPackage(context.applicationContext.packageName)
                intent.action = Intent.ACTION_MAIN
                val list: List<ResolveInfo>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
                } else {
                    return null
                }
                list ?: return result
                if (list.isNotEmpty()) {
                    result = Class.forName(list[0].activityInfo.name)
                    Log.i(TAG, "===========activityList====== $result")
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return result
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_hook)

        HookActivityUtils.instance.hooks(this)
        HookActivityUtils.instance.hookHandler()

        findViewById<Button>(R.id.btn_hook_activity).setOnClickListener {
            startActivity(Intent(this@MainHookActivity, MainOPActivity::class.java))
        }
    }


    class ActivityManagerDelegate(private val obj: Any,
                                  private val context: Context) : InvocationHandler {
        override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?): Any? {
            if (method?.name.equals("startActivity")) {
                args?.also {
                    for (i in it.indices) {
                        val arg = it[i]
                        if (arg is Intent) {
                            val src: Intent = arg
                            val dst = Intent()
                            dst.putExtra("realObj", src)
                            val activityClassImpl = getActivityClassImpl(context)
                            activityClassImpl?.also { clazz ->
                                dst.component = ComponentName(context, clazz)
                            }
                            it[i] = dst
                            break
                        }
                    }

                }

            }
            return method?.invoke(obj, args)
        }
    }


    class HookActivityUtils private constructor() {
        companion object {
            val instance: HookActivityUtils by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { HookActivityUtils() }
        }

        @SuppressLint("PrivateApi")
        fun hooks(context: Context) {
            try {
                var any: Any? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val iActivityManagerSingleton: Field = ActivityManager::class.java.getDeclaredField("IActivityManagerSingleton")
                    any = iActivityManagerSingleton.let {
                        it.isAccessible = true
                        it.get(null)
                    }
                } else {
                    val declaredField = Class.forName("android.app.ActivityManagerNative").getDeclaredField("gDefault")
                    any = declaredField.let {
                        it.isAccessible = true
                        it.get(null)
                    }
                }
                any ?: return

                val mFieldInstance = Class.forName("android.util.Singleton").getDeclaredField("mInstance")
                val mInstance = mFieldInstance.let {
                    it.isAccessible = true
                    it.get(null)
                }
                mInstance ?: return

                val aClass = Class.forName("android.app.IActivityManager")
                val proxy: Any = Proxy.newProxyInstance(aClass.classLoader, arrayOf(aClass), ActivityManagerDelegate(mInstance, context)) //创建代理IActivityManager
                mFieldInstance.set(any, proxy)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
        fun hookHandler() {
            try {
                val activityThread = Class.forName("android.app.ActivityThread")
                val declaredMethod = activityThread.getDeclaredMethod("currentActivityThread")
                val currentActivityThread: Any? = declaredMethod.run {
                    this.isAccessible = true
                    this.invoke(null)
                }
                val mH = activityThread.getDeclaredField("mH")
                val handler: Any? = mH.run {
                    this.isAccessible = true
                    this.get(currentActivityThread)
                }

                val mCallbackField = Handler::class.java.getDeclaredField("mCallback")
                mCallbackField.apply {
                    this.isAccessible = true
                    this.set(handler, HookCallBack(handler as Handler))
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    //代理Handler
    class HookCallBack(private val superHandler: Handler) : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            superHandler.handleMessage(msg)
            return false
        }
    }
}
