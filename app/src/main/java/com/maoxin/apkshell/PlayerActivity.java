package com.maoxin.apkshell;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.adnonstop.exoplayer.DefaultMediaSource;
import com.adnonstop.exoplayer.ExoVideoPlayer;
import com.adnonstop.exoplayer.ExoVideoView;
import com.adnonstop.exoplayer.IDataSourceFactory;
import com.adnonstop.exoplayer.MediaSourceBuilder;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements IDataSourceFactory
{
    MediaSourceBuilder mediaSourceBuilder;
    ExoVideoPlayer exoVideoPlayer;
    ExoVideoView exoVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        exoVideoView = findViewById(R.id.player_view);

        mediaSourceBuilder = new MediaSourceBuilder(this, this);
        exoVideoPlayer = new ExoVideoPlayer(this, mediaSourceBuilder);


        ArrayList<DefaultMediaSource> list = new ArrayList<>();
        mediaSourceBuilder.setMediaUri();
    }

    @Override
    public DataSource.Factory getDataSourceFactory()
    {
        SimpleCache simpleCache = new SimpleCache(null, new LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024));
        return new CacheDataSourceFactory(
                simpleCache,
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, getApplicationContext().getPackageName()), new DefaultBandwidthMeter()));
    }
}
