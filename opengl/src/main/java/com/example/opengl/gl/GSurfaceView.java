package com.example.opengl.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * @author lmx
 * Created by lmx on 2019/5/8.
 */
public class GSurfaceView extends GLSurfaceView
{
    public GSurfaceView(Context context)
    {
        this(context, null);
    }

    public GSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
}
