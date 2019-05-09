package com.example.opengl.gl.filter;

/**
 * @author lmx
 * Created by lmx on 2019/5/9.
 */
public interface IFilter
{
    void onSurfaceCreated(javax.microedition.khronos.egl.EGLConfig eglConfig);

    void onSurfaceChanged(int width, int height);

    void onDrawFrame(int textureId, float[] mvpMatrix, float[] texMatrix);
}
