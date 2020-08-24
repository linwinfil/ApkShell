package com.maoxin.apkshell.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.reflect.Field;

import androidx.annotation.StringRes;

/**
 * android.view.WindowManager$BadTokenException它只出现在 Android 8.0 之前的系统中，
 * 看起来是在 Toast 显示的时候窗口的 token 已经无效了。这有可能出现在 Toast 需要显示时，窗口已经销毁了
 * 但在Android 8.0中，直接catch了mWM.addView的异常；<br/>
 * Android 9.0不能使用反射，因为sdk非公开api限制调用
 * <p>
 * Created by admin on 2017/5/19.
 */

public class ToastUtil
{
    public static void show(Context context, CharSequence cs)
    {
        show(context, cs, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, @StringRes int resId)
    {
        show(context, resId, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, CharSequence cs, int length)
    {
        if (context == null) return;
        context = context.getApplicationContext();
        Toast toast = createToast(context);
        if (toast != null) {
            toast.setText(cs);
            toast.setDuration(length);
            toast.show();
        }
    }

    public static void show(Context context, @StringRes int resId, int length) {
        if (context == null) return;
        try {
            String str = context.getResources().getString(resId);
            show(context, str, length);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static Toast createToast(Context context) {
        context = context.getApplicationContext();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @SuppressLint("ShowToast")
            Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
            /*hook(toast);*/
            return toast;
        } else {
            return Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
    }


    private static Field sField_TN;
    private static Field sField_TN_Handler;

    static
    {
        try
        {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                sField_TN = Toast.class.getDeclaredField("mTN");
                sField_TN.setAccessible(true);
                sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
                sField_TN_Handler.setAccessible(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param toast
     * @see <a href="http://www.10tiao.com/html/223/201801/2651232846/1.html">使用定义的Handler代理Toast中消息分发的handler，在dispatchMessage中try-catch避免由于toast中taoken失效后抛出的异常</a>
     * NOTE 此hock 会触发 Android 9.0的非SDK接口限制机制，Accessing hidden field Landroid/widget/Toast$TN;->mHandler:Landroid/os/Handler; (dark greylist, reflection)
     */
    private static void hook(Toast toast)
    {
        try
        {
            if (sField_TN != null && sField_TN_Handler != null) {
                Object tn = sField_TN.get(toast);
                Handler preHandler = (Handler) sField_TN_Handler.get(tn);
                sField_TN_Handler.set(tn, new ToastSafelyHandlerWrapper(preHandler));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static class ToastSafelyHandlerWrapper extends Handler
    {
        private Handler impl;

        ToastSafelyHandlerWrapper(Handler impl)
        {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg)
        {
            try
            {
                super.dispatchMessage(msg);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public void handleMessage(Message msg)
        {
            //需要委托给原Handler执行
            if (impl != null) {
                impl.handleMessage(msg);
            }
        }
    }
}
