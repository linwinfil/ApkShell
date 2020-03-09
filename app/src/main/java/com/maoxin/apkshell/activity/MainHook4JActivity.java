package com.maoxin.apkshell.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * #I# 无注册启动activity，hook，目前测试5.1、6.0、7.1.1、8.1、9.0都可以，10.0不行
 */
public class MainHook4JActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HookActivityUtils.getInstance().hooks(this);
        HookActivityUtils.getInstance().hookHandler();

        FrameLayout fr = new FrameLayout(this);
        setContentView(fr);
        {
            Button btn = new Button(this);
            btn.setText("TEST");
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER;
            fr.addView(btn, fl);
            btn.setOnClickListener((View view) -> {
                Intent intent = new Intent(MainHook4JActivity.this, MainHookActivity.ProxyActivity.class);
                startActivity(intent);
            });
        }
    }

    public static Class getActivityClass(Context context) {
        Class result = null;

        PackageManager packageManager = context.getPackageManager();
        try {
            Intent intent = new Intent();
            intent.setPackage(context.getApplicationContext().getPackageName());
            //intent.addCategory(Intent.CATEGORY_HOME);
            //intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setAction(Intent.ACTION_MAIN);
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
            if (list.size() > 0) {
                result = Class.forName(list.get(0).activityInfo.name);
                Log.i("MainActivity50 ", "===========activityList====== " + result);
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }

        //result = MainActivity25.Standard.class;
        return result;
    }

    public static class HookActivityUtils {
        private static final String TAG = "HookActivityUtils";
        private volatile static HookActivityUtils sHookActivityUtils;

        public static HookActivityUtils getInstance() {
            if (sHookActivityUtils == null) {
                synchronized (HookActivityUtils.class) {
                    if (sHookActivityUtils == null) {
                        sHookActivityUtils = new HookActivityUtils();
                    }
                }
            }
            return sHookActivityUtils;
        }

        private HookActivityUtils() {
        }

        public void hooks(Context mContext) {
            try {
                Object object;
                //寻找hook点，最好是静态或者单例，不容易发生改变,因为是静态，所以传入null即可
                //因为版本差异，所以要分开处理
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Field iActivityManagerSingleton = ActivityManager.class.getDeclaredField("IActivityManagerSingleton");
                    iActivityManagerSingleton.setAccessible(true);
                    object = iActivityManagerSingleton.get(null);
                } else {
                    Field gDefault = Class.forName("android.app.ActivityManagerNative").getDeclaredField("gDefault");
                    gDefault.setAccessible(true);
                    object = gDefault.get(null);
                }

                //获取单例对象,实现IActivityManager接口的实现类
                Field mFieldInstance = Class.forName("android.util.Singleton").getDeclaredField("mInstance");
                mFieldInstance.setAccessible(true);
                Object mInstance = mFieldInstance.get(object);
                //寻找到hook点后，新建一个代理对象
                ActivityManagerDelegate managerDelegate = new ActivityManagerDelegate(mInstance, mContext);
                Class<?> aClass = Class.forName("android.app.IActivityManager");
                Object proxy = Proxy.newProxyInstance(aClass.getClassLoader(), new Class<?>[]{aClass}, managerDelegate);
                //替换动态代理对象
                mFieldInstance.set(object, proxy);
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public void hookHandler() {
            try {
                Class<?> aClass = Class.forName("android.app.ActivityThread");
                Method currentActivityThread = aClass.getDeclaredMethod("currentActivityThread");
                currentActivityThread.setAccessible(true);

                //ActivityThread 本身对象
                Object invoke = currentActivityThread.invoke(null);
                Field mHFiled = aClass.getDeclaredField("mH");
                mHFiled.setAccessible(true);

                //获取handler对象
                Object handler = mHFiled.get(invoke);

                //获取handler中的mCallback
                Field mCallbackField = Handler.class.getDeclaredField("mCallback");
                mCallbackField.setAccessible(true);
                mCallbackField.set(handler, new HookCallBack((Handler) handler));
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static class ActivityManagerDelegate implements InvocationHandler {
        private static final String TAG = "ActivityManagerDelegate";
        private Object mObject;
        private Context mContext;

        public ActivityManagerDelegate(Object mObject, Context mContext) {
            this.mObject = mObject;
            this.mContext = mContext;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method != null && "startActivity".equals(method.getName())) {
                //拦截方法
                Log.e(TAG, "i got you");
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        Intent src = (Intent) args[i];
                        //找到了intent参数
                        Intent dst = new Intent();
                        ComponentName componentName = new ComponentName(mContext, getActivityClass(mContext));
                        //将真正的intent带上，后续替换
                        dst.setComponent(componentName);
                        dst.putExtra("realObj", src);
                        //修改为已注册Activity的intent，先让AMS检查通过
                        args[i] = dst;
                        break;
                    }
                }
            }
            return method.invoke(mObject, args);
        }
    }

    public static class HookCallBack implements Handler.Callback {
        private static final String TAG = "HookCallBack";
        private Handler mHandler;

        public HookCallBack(Handler mHandler) {
            this.mHandler = mHandler;
        }

        @Override
        public boolean handleMessage(Message msg) {
            //恢复
            if (msg.what == 100) {
                handleHookMsg100(msg);
            } else if (msg.what == 159) {
                handleHookMsg159(msg);
            }
            //mHandler.handleMessage(msg);
            return false;
        }

        private void handleHookMsg100(Message mMsg) {
            Object obj = mMsg.obj;
            try {
                Field intent = obj.getClass().getDeclaredField("intent");
                //这时候拿出之前存进来真正的intent
                intent.setAccessible(true);
                Intent proxyIntent = (Intent) intent.get(obj);
                Intent realIntent = proxyIntent.getParcelableExtra("realObj");
                proxyIntent.setComponent(realIntent.getComponent());
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }

        private void handleHookMsg159(Message mMsg) {
            Object obj = mMsg.obj;
            try {
                Field mActivityCallbacksField = obj.getClass().getDeclaredField("mActivityCallbacks");
                mActivityCallbacksField.setAccessible(true);
                List mActivityCallbacks = (List) mActivityCallbacksField.get(obj);
                Log.i(TAG, "handleMessage: mActivityCallbacks= " + mActivityCallbacks);

                if (mActivityCallbacks.size() > 0) {
                    Log.i(TAG, "handleMessage: size= " + mActivityCallbacks.size());
                    String className = "android.app.servertransaction.LaunchActivityItem";
                    if (mActivityCallbacks.get(0).getClass().getCanonicalName().equals(className)) {
                        Object object = mActivityCallbacks.get(0);
                        Field intentField = object.getClass().getDeclaredField("mIntent");
                        intentField.setAccessible(true);
                        Intent intent = (Intent) intentField.get(object);
                        //拿出之前存进来真正的intent
                        Intent targetIntent = intent.getParcelableExtra("realObj");
                        intent.setComponent(targetIntent.getComponent());
                    }
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
