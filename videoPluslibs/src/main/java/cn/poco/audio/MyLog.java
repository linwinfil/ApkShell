package cn.poco.audio;

import android.util.Log;

/**
 * Created by menghd on 2017/3/13 0013.
 */

public class MyLog {
    public static boolean isDebug = false;


    public static void e(Class<?> clazz, String msg) {
        if (isDebug) {
            Log.e(clazz.getSimpleName(), msg + "");
        }
    }

    public static void i(Class<?> clazz, String msg) {
        if (isDebug) {
            Log.i(clazz.getSimpleName(), msg + "");
        }
    }


    public static void w(Class<?> clazz, String msg) {
        if (isDebug) {
            Log.w(clazz.getSimpleName(), msg + "");
        }
    }

    public static String getLineNumber() {
        if (isDebug) {
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            return "   " + ste.getFileName() + ": Line " + ste.getLineNumber();
        }
        return "";
    }
}
