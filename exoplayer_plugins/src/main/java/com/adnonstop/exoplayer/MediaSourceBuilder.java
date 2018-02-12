package com.adnonstop.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

/**
 * @author lmx
 *         Created by lmx on 2018-02-06.
 */

public class MediaSourceBuilder
{
    private static final String TAG = MediaSourceBuilder.class.getSimpleName();
    private Context mContext;
    private Handler mMainHandler;

    private MediaSource mMediaSource;
    private int mLoopingCount;

    private String mUserAgent;
    private IDataSourceFactory mFactory;
    private MediaSourceEventListener mEventListener;

    public MediaSourceBuilder(@NonNull Context context,
                              @Nullable IDataSourceFactory factory,
                              @Nullable MediaSourceEventListener listener)
    {
        this.mContext = context;
        this.mFactory = factory;
        this.mEventListener = listener;
    }

    public MediaSourceBuilder(Context mContext, IDataSourceFactory mFactory)
    {
       this(mContext, mFactory, null);
    }

    public <T extends IMediaSource> void setMediaUri(List<T> list)
    {
        if (list != null && list.size() > 0)
        {
            MediaSource[] mediaSources = new MediaSource[list.size()];
            int index = 0;
            for (T media : list)
            {
                if (media != null)
                {
                    mediaSources[index] = initMediaSource(Uri.parse(media.getVideoUri()));
                }
                index++;
            }
            mMediaSource = new ConcatenatingMediaSource(mediaSources);
        }
    }

    public <T extends IMediaSource> void setMediaUri(T... list)
    {
        if (list != null && list.length > 0)
        {
            MediaSource[] mediaSources = new MediaSource[list.length];
            int index = 0;
            for (T media : list) {
                if (media != null) {
                    mediaSources[index] = initMediaSource(Uri.parse(media.getVideoUri()));
                }
                index++;
            }
            mMediaSource = new ConcatenatingMediaSource(mediaSources);
        }
    }


    public void setLoopingCount(@Size(min = 1) int loopingCount)
    {
        this.mLoopingCount = loopingCount;
    }

    public void setUserAgent(String mUserAgent)
    {
        this.mUserAgent = mUserAgent;
    }

    public MediaSource getMediaSource()
    {
        if (mLoopingCount > 0) {
            return new LoopingMediaSource(mMediaSource, mLoopingCount);
        }
        return mMediaSource;
    }

    public MediaSource initMediaSource(Uri uri)
    {
        @C.ContentType
        int streamType = Util.inferContentType(uri);
        switch (streamType)
        {
            case C.TYPE_OTHER:
            {
                return new ExtractorMediaSource.Factory(getDataSourceFactory())
                        .setExtractorsFactory(new DefaultExtractorsFactory())
                        .setMinLoadableRetryCount(5)
                        .setCustomCacheKey(uri.getPath())
                        .createMediaSource(uri, mMainHandler, null);
            }
            //TODO
            case C.TYPE_DASH:
                break;
            case C.TYPE_HLS:
                break;
            case C.TYPE_SS:
                break;
        }
        return null;
    }


    private DataSource.Factory getDataSourceFactory()
    {
        if (this.mFactory != null) {
            return this.mFactory.getDataSourceFactory();
        }
        if (TextUtils.isEmpty(mUserAgent)) mUserAgent = Util.getUserAgent(mContext, mContext.getApplicationContext().getPackageName());
        return new DefaultDataSourceFactory(mContext, mUserAgent, new DefaultBandwidthMeter());
    }


    public void release()
    {
        if (mMediaSource != null) mMediaSource.releaseSource();
        if (mMainHandler != null) mMainHandler.removeCallbacksAndMessages(mContext);
        mMediaSource = null;
        mMainHandler = null;
        mContext = null;
        mFactory = null;
    }
}
