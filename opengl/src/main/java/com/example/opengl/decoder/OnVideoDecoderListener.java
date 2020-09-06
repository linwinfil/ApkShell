package com.example.opengl.decoder;

import androidx.annotation.WorkerThread;

/**
 * @author lmx
 * Created by lmx on 2019/5/22.
 */
public interface OnVideoDecoderListener
{
    @WorkerThread
    void onPrepared(VideoDecoder videoDecoder);
    @WorkerThread
    void onDraw(long presentationTime);
    @WorkerThread
    void onFinish();
}
