package com.example.opengl.activity;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SeekBar;

import com.example.opengl.R;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.ShapeFilter;
import com.example.opengl.gl.utils.GlUtils;

public class GlShapeActivity extends BaseActivity
{
    GSurfaceView glSurfaceView;
    SeekBar seekBar_alpha;
    SeekBar seekBar_ratio;

    ShapeFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_shape);

        glSurfaceView = findViewById(R.id.gl_surface_view);
        seekBar_alpha = findViewById(R.id.seek_bar_alpha);
        seekBar_ratio = findViewById(R.id.seek_bar_ratio);

        glSurfaceView.setEGLContextClientVersion(2);
        filter  = new ShapeFilter(this, GlUtils.loadShaderRawResource(this, R.raw.default_vertex_shader), GlUtils.loadShaderRawResource(this, R.raw.shape_fragment_shader));
        glSurfaceView.setRenderer(filter);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                onStopTrackingTouch(seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (seekBar == seekBar_alpha) {
                    filter.setAlpha(seekBar.getProgress() * 1f / 100f);
                } else if (seekBar == seekBar_ratio) {
                    filter.setRatio(seekBar.getProgress() * 1f / 100f);
                }
                glSurfaceView.requestRender();
            }
        };
        seekBar_ratio.setOnSeekBarChangeListener(listener);
        seekBar_alpha.setOnSeekBarChangeListener(listener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        filter.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        filter.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        filter.onRelease();
    }

    @Override
    public void onInflateOptionsMenu(Menu menu)
    {

    }

    @Override
    public void handleImageCallback(Bitmap bitmap)
    {

    }
}
