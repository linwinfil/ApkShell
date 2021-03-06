package com.example.opengl.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * @author lmx
 * Created by lmx on 2019/5/22.
 */
public class ThreadUtils
{
    private static volatile Handler mHandler;

    public static void init() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
    }

    public static void runOnUiThread(Runnable runnable)
    {
        if (mHandler != null)
        {
            mHandler.post(runnable);
        }
    }

    public static void runOnUiThreadDelay(Runnable runnable, long delay)
    {
        if (mHandler != null)
        {
            mHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUiThread(Runnable runnable)
    {
        if (mHandler != null)
        {
            mHandler.removeCallbacks(runnable);
        }
    }

    public static boolean isOnMainThread()
    {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isOnBackgroundThread()
    {
        return !isOnMainThread();
    }

    public static void clear()
    {
        if (mHandler != null)
        {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

}
