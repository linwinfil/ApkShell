package com.maoxin.apkshell.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author lmx
 * Created by lmx on 2020/2/24.
 */
public class MainContentProvider extends ContentProvider {
    private static final String TAG = "MainContentProvider";

    private static void lI(String msg) {
        Log.i(TAG, "logI: " + msg);
    }

    @Override
    public boolean onCreate() {
        //run on [main thread]
        lI("onCreate true");
        return true;
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        if (info != null) {
            System.out.println(info.toString());
        }
        lI("attachInfo");
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        lI("query");
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        lI("getType");
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        lI("insert");
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        lI("delete");
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        lI("update");
        return 0;
    }
}
