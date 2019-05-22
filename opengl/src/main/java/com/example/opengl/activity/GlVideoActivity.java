package com.example.opengl.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.opengl.R;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.OesFilter;
import com.example.opengl.gl.utils.GlUtils;

import androidx.annotation.Nullable;

public class GlVideoActivity extends BaseActivity
{
    public static final int REQUEST_PICK_VIDEO = 2;

    GSurfaceView glSurfaceView;
    Button button;
    SeekBar seekBar;
    OesFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_video);

        button = findViewById(R.id.btn_play);
        button.setTag(false);
        button.setOnClickListener(v ->
        {
            boolean isPlay = (boolean) v.getTag();
            isPlay = !isPlay;
            v.setTag(isPlay);
            if (isPlay) {
                filter.onResume();
            } else {
                filter.onPause();
            }
        });
        glSurfaceView = findViewById(R.id.gl_surface_view);

        glSurfaceView.setEGLContextClientVersion(2);
        filter = new OesFilter(this, GlUtils.loadShaderRawResource(this, R.raw.oes_vertex_shader), GlUtils.loadShaderRawResource(this, R.raw.oes_fragment_shader));
        filter.setOnCallbackListenerAdapter(new OesFilter.OnCallbackListenerAdapter() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture)
            {
                if (glSurfaceView != null) {
                    glSurfaceView.requestRender();
                }
            }

            @Override
            public void onPrepared(MediaPlayer mp)
            {
                if (glSurfaceView != null) {
                    glSurfaceView.requestRender();
                }
            }
        });
        glSurfaceView.setRenderer(filter);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    @Override
    public void onItemSelectedMenu(MenuItem item)
    {
        if (item.getItemId() == R.id.mPicker)
        {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("video/*");
            this.startActivityForResult(photoPickerIntent, REQUEST_PICK_VIDEO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQUEST_PICK_VIDEO && resultCode == Activity.RESULT_OK && data != null)
        {
            Uri uri = data.getData();
            filter.setData(uri);
            filter.prepare();
            seekBar.setProgress(0);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public void handleImageCallback(Bitmap bitmap)
    {
    }
}
