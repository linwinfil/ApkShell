package com.example.opengl.decoder;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.view.Surface;

/**
 * @author lmx
 * Created by lmx on 2019/5/21.
 */
public class VideoDecoder implements IDecoder
{
    private String mPath;
    private Surface mSurface;

    private MediaCodec mMediaCodec;
    private MediaExtractor mMediaExtractor;

    public VideoDecoder(String mPath, Surface mSurface)
    {
        this.mPath = mPath;
        this.mSurface = mSurface;
    }

    @Override
    public void run()
    {

    }
}
