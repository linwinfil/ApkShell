package com.maoxin.apkshell;

import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity
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

        mediaSourceBuilder = new MediaSourceBuilder(this, new IDataSourceFactory()
        {
            @Override
            public DataSource.Factory getDataSourceFactory()
            {
                SimpleCache simpleCache = new SimpleCache(new File(Environment.getExternalStorageDirectory() + File.separator + "exoplayer"),
                        new LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024));
                return new CacheDataSourceFactory(
                        simpleCache,
                        new DefaultDataSourceFactory(PlayerActivity.this, Util.getUserAgent(PlayerActivity.this, getApplicationContext().getPackageName()), new DefaultBandwidthMeter()));
            }
        });
        exoVideoPlayer = new ExoVideoPlayer(this, mediaSourceBuilder);
        exoVideoView.setPlayer(exoVideoPlayer);

        ArrayList<DefaultMediaSource> list = new ArrayList<>();
        DefaultMediaSource mediaSource = new DefaultMediaSource();
        mediaSource.setVideoUri("http://biz-zt-oss.adnonstop.com/ar_201802/20180208/22/020180208224311_5723_7811565338.mp4");
        list.add(mediaSource);
        mediaSourceBuilder.setMediaUri(list);
        exoVideoPlayer.prepare();
        exoVideoPlayer.start();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (exoVideoView != null) {
            exoVideoView.onBackPressed();
        }
    }
}
