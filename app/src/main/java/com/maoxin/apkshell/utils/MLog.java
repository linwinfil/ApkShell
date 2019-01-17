package com.maoxin.apkshell.utils;

import android.text.TextUtils;
import android.util.Log;

import com.maoxin.apkshell.BuildConfig;


/**
 * @author lmx
 *         Created by lmx on 2018-03-26.
 */

public class MLog
{
    public static final String TAG = "BeautyCamera_Log_Tag";
    public static boolean sDebug = BuildConfig.DEBUG;

    public interface ILogTag
    {
        String getLogTag();
    }

    private MLog()
    {
    }

    public void testSmail(boolean test, int i)
    {
        Test.test_static(i, true);
    }

    public static final class Test
    {
        public static void test_static(int i, boolean b){};
    }


    public static void i(ILogTag tag, String msg)
    {
        if (sDebug)
        {
            Log.i(getTag(tag), msg);
        }
    }

    public static void d(ILogTag tag, String msg)
    {
        if (sDebug)
        {
            Log.d(getTag(tag), msg);
        }
    }

    public static void e(ILogTag tag, String msg)
    {
        if (sDebug)
        {
            Log.e(getTag(tag), msg);
        }
    }

    public static void v(ILogTag tag, String msg)
    {
        if (sDebug)
        {
            Log.v(getTag(tag), msg);
        }
    }

    public static void w(ILogTag tag, String msg)
    {
        if (sDebug)
        {
            Log.w(getTag(tag), msg);
        }
    }


    public static void i(String tag, String msg)
    {
        if (sDebug)
        {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg)
    {
        if (sDebug)
        {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg)
    {
        if (sDebug)
        {
            Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg)
    {
        if (sDebug)
        {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg)
    {
        if (sDebug)
        {
            Log.w(tag, msg);
        }
    }

    private static String getTag(ILogTag iLogTag)
    {
        String tag = TAG;
        if (iLogTag != null && !TextUtils.isEmpty(iLogTag.getLogTag()))
        {
            tag = iLogTag.getLogTag();
        }
        return tag;
    }
}
