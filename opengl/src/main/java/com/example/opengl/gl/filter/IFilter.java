package com.example.opengl.gl.filter;

import android.opengl.GLSurfaceView;

/**
 * @author lmx
 * Created by lmx on 2019/5/9.
 */
public interface IFilter extends GLSurfaceView.Renderer
{
    int Flip_Horizontal = 1;//水平翻转
    int Flip_Vertical = 1 << 1;//垂直翻转

    void onRelease();
}
