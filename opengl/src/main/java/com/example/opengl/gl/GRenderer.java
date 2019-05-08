package com.example.opengl.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author lmx
 * Created by lmx on 2019/5/8.
 */
public class GRenderer implements GLSurfaceView.Renderer
{
    private Context mContext;

    public GRenderer(Context mContext)
    {
        this.mContext = mContext;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {

    }

    @Override
    public void onDrawFrame(GL10 gl)
    {

    }
}
