package com.example.opengl.gl.filter;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * @author lmx
 * Created by lmx on 2019/5/10.
 */
public class CameraFilter extends AFilter
{
    public CameraFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        super(mContext, mVertexShader, mFragmentShader);
    }

    @Override
    public void onSurfaceCreatedInit(EGLConfig eglConfig)
    {

    }

    @Override
    public void onSurfaceChangedInit(int width, int height)
    {

    }

    @Override
    public int onCreateProgram(EGLConfig eglConfig)
    {
        return 0;
    }

    @Override
    public void onDraw()
    {

    }

    @Override
    public int getTextureType()
    {
        return 0;
    }
}
