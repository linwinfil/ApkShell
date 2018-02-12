package com.adnonstop.exoplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

/**
 * @author lmx
 *         Created by lmx on 2018-02-12.
 */

public class VideoPlayerUtils
{

    public static int GetOrientation(@NonNull Context context) {
        Resources resources = context.getResources();
        if (resources == null || resources.getConfiguration() == null) {
            return Configuration.ORIENTATION_PORTRAIT;
        }
        return resources.getConfiguration().orientation;
    }

    public static void HideActionBar(@NonNull Context context) {
        AppCompatActivity appCompActivity = GetAppCompActivity(context);
        if (appCompActivity != null) {
            ActionBar ab = appCompActivity.getSupportActionBar();
            if (ab != null) {
                ab.hide();
            }
        }
        ScanForActivity(context)
                .getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void ShowActionBar(@NonNull Context context) {
        AppCompatActivity appCompActivity = GetAppCompActivity(context);
        if (appCompActivity != null) {
            ActionBar ab = appCompActivity.getSupportActionBar();
            if (ab != null) {
                ab.show();
            }
        }
        ScanForActivity(context).getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Nullable
    public static AppCompatActivity GetAppCompActivity(@NonNull Context context)
    {
        if (context instanceof AppCompatActivity)
        {
            return (AppCompatActivity) context;
        }
        else if (context instanceof ContextThemeWrapper)
        {
            return GetAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public static Activity ScanForActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return ScanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

}
