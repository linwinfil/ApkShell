package com.example.opengl.activity;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.opengl.R;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.BlendFilter;
import com.example.opengl.gl.utils.GlUtils;

import java.util.ArrayList;

public class GlBlendActivity extends BaseActivity
{
    GSurfaceView glsurfaceview;
    BlendFilter filter;

    Spinner spinner_src;
    Spinner spinner_dst;
    Button button_opera;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_blend);

        spinner_src = findViewById(R.id.spinner_src);
        spinner_dst = findViewById(R.id.spinner_dst);
        button_opera = findViewById(R.id.btn_opera);

        glsurfaceview = findViewById(R.id.gl_surface_view);
        glsurfaceview.setEGLContextClientVersion(2);
        glsurfaceview.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

        filter = new BlendFilter(this,
                GlUtils.loadShaderRawResource(this, R.raw.default_vertex_shader),
                GlUtils.loadShaderRawResource(this, R.raw.default_fragment_shader));
        glsurfaceview.setRenderer(filter);
        glsurfaceview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        ArrayAdapter<Pair<String, Integer>> adapter_src = new ArrayAdapter(this, R.layout.list_item, R.id.textview, getBlendFunData());
        spinner_src.setAdapter(adapter_src);
        spinner_src.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                GlBlendActivity.Pair<String, Integer> item = adapter_src.getItem(position);
                if (item != null) {
                    ((TextView)view.findViewById(R.id.textview)).setText(item.first);
                    if (filter != null) {
                        filter.setBlendSrcInt(item.second);
                    }
                    if (glsurfaceview != null) {
                        glsurfaceview.requestRender();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        ArrayAdapter<Pair<String, Integer>> adapter_dst = new ArrayAdapter(this, R.layout.list_item, R.id.textview, getBlendFunData());
        spinner_dst.setAdapter(adapter_dst);
        spinner_dst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                GlBlendActivity.Pair<String, Integer> item = adapter_dst.getItem(position);
                if (item != null) {
                    ((TextView)view.findViewById(R.id.textview)).setText(item.first);
                    if (filter != null) {
                        filter.setBlendDstInt(item.second);
                    }
                    if (glsurfaceview != null) {
                        glsurfaceview.requestRender();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }


    private static class Pair<F, S> extends android.util.Pair<F, S>
    {
        Pair(F first, S second)
        {
            super(first, second);
        }

        @Override
        public String toString()
        {
            return String.valueOf(first);
        }
    }

    private ArrayList<Pair<String, Integer>> getBlendFunData()
    {
        ArrayList<Pair<String, Integer>> out = new ArrayList<>();
        out.add(new GlBlendActivity.Pair<>("GL_ZERO", GLES20.GL_ZERO));
        out.add(new GlBlendActivity.Pair<>("GL_ONE", GLES20.GL_ONE));
        out.add(new GlBlendActivity.Pair<>("GL_SRC_COLOR", GLES20.GL_SRC_COLOR));
        out.add(new GlBlendActivity.Pair<>("GL_ONE_MINUS_SRC_COLOR", GLES20.GL_ONE_MINUS_SRC_COLOR));
        out.add(new GlBlendActivity.Pair<>("GL_DST_COLOR", GLES20.GL_DST_COLOR));
        out.add(new GlBlendActivity.Pair<>("GL_ONE_MINUS_DST_COLOR", GLES20.GL_ONE_MINUS_DST_COLOR));
        out.add(new GlBlendActivity.Pair<>("GL_SRC_ALPHA", GLES20.GL_SRC_ALPHA));
        out.add(new GlBlendActivity.Pair<>("GL_ONE_MINUS_SRC_ALPHA", GLES20.GL_ONE_MINUS_SRC_ALPHA));
        out.add(new GlBlendActivity.Pair<>("GL_DST_ALPHA", GLES20.GL_DST_ALPHA));
        out.add(new GlBlendActivity.Pair<>("GL_ONE_MINUS_DST_ALPHA", GLES20.GL_ONE_MINUS_DST_ALPHA));
        out.add(new GlBlendActivity.Pair<>("GL_CONSTANT_COLOR", GLES20.GL_CONSTANT_COLOR));
        out.add(new GlBlendActivity.Pair<>("GL_ONE_MINUS_CONSTANT_COLOR", GLES20.GL_ONE_MINUS_CONSTANT_COLOR));
        out.add(new GlBlendActivity.Pair<>("GL_CONSTANT_ALPHA", GLES20.GL_CONSTANT_ALPHA));
        out.add(new GlBlendActivity.Pair<>("GL_ONE_MINUS_CONSTANT_ALPHA", GLES20.GL_ONE_MINUS_CONSTANT_ALPHA));
        out.add(new GlBlendActivity.Pair<>("GL_SRC_ALPHA_SATURATE", GLES20.GL_SRC_ALPHA_SATURATE));

        return out;
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
        if (bitmap != null && filter != null)
        {
            filter.setBitmap(bitmap);
            glsurfaceview.requestRender();
        }
    }
}
