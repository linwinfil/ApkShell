package com.example.opengl.activity;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.example.opengl.R;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.BlendFilter;
import com.example.opengl.gl.utils.GlUtils;

public class GlBlendActivity extends BaseActivity
{
    GSurfaceView glsurfaceview;
    BlendFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_blend);

        glsurfaceview = findViewById(R.id.gl_surface_view);
        glsurfaceview.setEGLContextClientVersion(2);
        glsurfaceview.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

        filter = new BlendFilter(this,
                GlUtils.loadShaderRawResource(this, R.raw.default_vertex_shader),
                GlUtils.loadShaderRawResource(this, R.raw.default_fragment_shader));
        glsurfaceview.setRenderer(filter);
        glsurfaceview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        glsurfaceview.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        glsurfaceview.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        filter.onRelease();
    }

    @Override
    public void handleImageCallback(Bitmap bitmap)
    {
        if (bitmap != null && filter != null) {
            filter.setBitmap(bitmap);
            glsurfaceview.requestRender();
        }
    }
}
