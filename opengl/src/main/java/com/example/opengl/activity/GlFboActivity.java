package com.example.opengl.activity;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.example.opengl.R;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.FboFilter;
import com.example.opengl.gl.utils.GlUtils;

public class GlFboActivity extends BaseActivity implements View.OnClickListener
{
    GSurfaceView glsurfaceview;
    FboFilter filter;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_fbo);

        findViewById(R.id.btn_capture).setOnClickListener(this);
        seekBar = findViewById(R.id.seekBar1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    filter.setProgress(progress);
                    glsurfaceview.requestRender();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                filter.setProgress(seekBar.getProgress());
                glsurfaceview.requestRender();
            }
        });

        glsurfaceview = findViewById(R.id.gl_surface_view);
        glsurfaceview.setEGLContextClientVersion(2);
        filter = new FboFilter(this,
                GlUtils.loadShaderRawResource(this, R.raw.default_vertex_shader),
                GlUtils.loadShaderRawResource(this, R.raw.color_fragment_shader));
        filter.setOnCaptureCallback(bitmap -> {
            System.out.println(bitmap.getWidth() + ", " + bitmap.getHeight());
        });
        glsurfaceview.setRenderer(filter);
        glsurfaceview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void handleImageCallback(Bitmap bitmap)
    {
        if (bitmap != null && filter != null)
        {
            filter.setBitmap(bitmap);
            glsurfaceview.requestRender();
        }
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
    public void onClick(View v)
    {
        if (v.getId() == R.id.btn_capture)
        {
            filter.setCapture(true);
            glsurfaceview.requestRender();
        }
    }
}
