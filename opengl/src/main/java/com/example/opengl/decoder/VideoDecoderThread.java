package com.example.opengl.decoder;

import android.view.Surface;

import androidx.annotation.NonNull;

/**
 * @author lmx
 * Created by lmx on 2019/5/22.
 */
public class VideoDecoderThread extends Thread
{
    final private VideoDecoder videoDecoder;
    private volatile boolean isRelease;

    public VideoDecoderThread(String path, Surface surface) {
        videoDecoder = new VideoDecoder(path, surface);
    }

    @NonNull public VideoDecoder getVideoDecoder() {
        return videoDecoder;
    }

    @Override
    public void run()
    {
        System.out.println("isRelease" + isRelease);
        while (!isRelease) {
            videoDecoder.run();
        }

        if (isRelease) {
            videoDecoder.onRelease();
        }
    }

    public void onRelease() {
        isRelease = true;
        videoDecoder.onRelease();
    }
}
