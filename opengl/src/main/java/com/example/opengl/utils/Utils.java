package com.example.opengl.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;

import java.io.File;
import java.lang.reflect.Field;

public class Utils
{

    public static float sDensity;
    public static float sDensityDpi;
    public static int sScreenW;
    public static int sScreenH;
    public static int sCount;
    public static int sRelativeScreenW = 720;
    public static int sRelativeScreenH = 1280;

    public static void init(Activity activiy)
    {
        Display dis = activiy.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dis.getMetrics(dm);
        Point point = new Point();
        dis.getSize(point);
        int h = point.x;
        int w = point.y;
        sScreenW = w < h ? w : h;
        sScreenH = w < h ? h : w;
        sDensity = dm.density;
        sDensityDpi = dm.densityDpi;
    }

    public static int getScreenW()
    {
        return sScreenW;
    }

    public static int getScreenH()
    {
        return sScreenH;
    }

    //获取手机状态栏高度
    public static int getStatusBarHeight(Context context)
    {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try
        {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public static int getRealPixel2(int pxSrc)
    {
        int pix = (int)(pxSrc * sScreenW / sRelativeScreenW);
        if(pxSrc == 1 && pix == 0)
        {
            pix = 1;
        }
        return pix;
    }

    public static int getRealPixel(int pxSrc)
    {
        int pix = (int)(pxSrc * sDensity / 2.0);
        if(pxSrc == 1 && pix == 0)
        {
            pix = 1;
        }
        return pix;
    }

    public static boolean isFileExists(String path) {
        return !TextUtils.isEmpty(path) && new File(path).exists();
    }
}

