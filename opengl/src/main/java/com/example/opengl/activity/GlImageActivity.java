package com.example.opengl.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.example.opengl.R;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.ImageFilter;

import androidx.appcompat.app.AppCompatActivity;

public class GlImageActivity extends AppCompatActivity
{
    GSurfaceView glsurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_image);
        glsurfaceView = findViewById(R.id.gl_surface_view);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        ImageFilter filter = new ImageFilter(this, null, null);
        glsurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glsurfaceView.setEGLContextClientVersion(2);
        glsurfaceView.setRenderer(filter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        glsurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        glsurfaceView.onPause();
    }
}
