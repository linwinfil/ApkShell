package com.example.opengl.decoder;

/**
 * @author lmx
 * Created by lmx on 2019/5/22.
 */
public interface OnVideoDecoderListener
{
    void onPrepared(VideoDecoder videoDecoder);
    void onDraw();
    void onFinish();
}
