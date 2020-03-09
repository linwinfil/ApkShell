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
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.annotation.Nullable
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

    //未注册的目标代理activity
    class ProxyActivity : AppCompatActivity() {
        override fun onCreate(@Nullable savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val tex = TextView(this)
            tex.text = "SUCCESS!!!"
            tex.textSize = 40F
            tex.gravity = Gravity.CENTER
            tex.setBackgroundColor(-0x100)
            setContentView(tex)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_hook)

        //hook
        HookActivityUtils.instance.hooks(this)
        HookActivityUtils.instance.hookHandler()

        findViewById<Button>(R.id.btn_hook_activity).setOnClickListener {
            startActivity(Intent(this@MainHookActivity, ProxyActivity::class.java))
        }
    }


    class ActivityManagerDelegate(private val obj: Any,
                                  private val context: Context) : InvocationHandler {
        override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?): Any? {
            if (method != null && "startActivity" == method.name) {
                //拦截方法
                for (i in args!!.indices) {
                    if (args[i] is Intent) {
                        val src = args[i] as Intent
                        //找到了intent参数
                        val dst = Intent()
                        val activityClassImpl = getActivityClassImpl(context)
                        activityClassImpl?.also {
                            val componentName = ComponentName(context, activityClassImpl)
                            //将真正的intent带上，后续替换
                            dst.component = componentName
                            dst.putExtra("realObj", src)
                            //修改为已注册Activity的intent，先让AMS检查通过
                            args[i] = dst
                        }
                        break
                    }
                }
            }
            return method!!.invoke(obj, args)
        }
    }


    class HookActivityUtils private constructor() {
        companion object {
            val instance: HookActivityUtils by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { HookActivityUtils() }
        }

        @SuppressLint("PrivateApi")
        fun hooks(context: Context) {
            try {
                val any: Any?
                //寻找hook点，最好是静态或者单例，不容易发生改变,因为是静态，所以传入null即可
                //因为版本差异，所以要分开处理
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

                val mFieldInstance = Class.forName("android.util.Singleton").getDeclaredField("mInstance")
                val mInstance = mFieldInstance.let {
                    it.isAccessible = true
                    it.get(any)
                }
                mInstance ?: return

                val activityManagerDelegate = ActivityManagerDelegate(mInstance, context)
                val aClass = Class.forName("android.app.IActivityManager")
                val proxy: Any = Proxy.newProxyInstance(aClass.classLoader, arrayOf(aClass), activityManagerDelegate) //创建代理IActivityManager
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
            when (msg.what) {
                100 -> {
                    hookMsg100(msg)
                }
                159 -> {
                    hookMsg159(msg)
                }
            }
            return false
        }

        private fun hookMsg100(msg: Message) {
            val obj: Any? = msg.obj
            obj?.also {
                try {
                    val intent = it.javaClass.getDeclaredField("intent")
                    intent.isAccessible = true
                    val proxyIntent: Intent = intent.get(obj) as Intent
                    val realIntent = proxyIntent.getParcelableExtra<Intent>("realObj") //这时候拿出之前存进来真正的intent
                    proxyIntent.component = realIntent!!.component
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

        private fun hookMsg159(msg: Message) {
            val obj: Any? = msg.obj
            obj?.also {
                try {
                    val declaredField = obj.javaClass.getDeclaredField("mActivityCallbacks")
                    declaredField.isAccessible = true
                    val mActivityCallbacks: List<*> = declaredField.get(obj) as List<*>
                    Log.i(TAG, "handleMessage: mActivityCallbacks= $mActivityCallbacks")
                    if (mActivityCallbacks.isNotEmpty()) {
                        Log.i(TAG, "handleMessage: size= ${mActivityCallbacks.size}")
                        val className = "android.app.servertransaction.LaunchActivityItem"
                        if (mActivityCallbacks[0]?.javaClass?.canonicalName.equals(className)) {
                            val obj2 = mActivityCallbacks[0]!!
                            val intentField = obj2.javaClass.getDeclaredField("mIntent")
                            intentField.isAccessible = true
                            val intent = intentField[obj2] as Intent
                            val targetIntent = intent.getParcelableExtra<Intent>("realObj") //拿出之前存进来真正的intent
                            intent.component = targetIntent!!.component
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }
}
