package com.adnonstop.exoplayer;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

import androidx.annotation.NonNull;

/**
 * 默认缓存缓存工厂类
 *
 * @author lmx
 *         Created by lmx on 2018-02-07.
 */

public class DefaultCacheDataSourceFactory implements DataSource.Factory
{
    public static final long MAX_BYTES = 1024L * 1024 * 100;

    private SimpleCache simpleCache;
    private DataSource dataSource;
    private CacheDataSource.EventListener listener;

    public DefaultCacheDataSourceFactory(@NonNull Context context,
                                         String userAgent,
                                         String cacheDir,
                                         CacheDataSource.EventListener listener)
    {
        this.listener = listener;
        File cacheFile = null;

        cacheFile = TextUtils.isEmpty(cacheDir) ? new File(context.getExternalCacheDir(), "media") : new File(cacheDir);
        simpleCache = new SimpleCache(cacheFile, new LeastRecentlyUsedCacheEvictor(MAX_BYTES));

        userAgent = TextUtils.isEmpty(userAgent) ? context.getApplicationContext().getPackageName() : userAgent;
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent, new DefaultBandwidthMeter());
        dataSource = dataSourceFactory.createDataSource();
    }

    @Override
    public DataSource createDataSource()
    {
        return new CacheDataSource(simpleCache,
                dataSource,
                new FileDataSource(),
                new CacheDataSink(simpleCache, CacheDataSource.DEFAULT_MAX_CACHE_FILE_SIZE),
                0,
                listener);
    }

    public void release()
    {
        simpleCache = null;
        listener = null;
    }
}
