package com.example.opengl.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.example.opengl.R;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.ImageFilter;
import com.example.opengl.gl.utils.GlUtils;

import java.util.ArrayList;

public class GlImageActivity extends BaseActivity implements View.OnClickListener
{
    GSurfaceView glsurfaceView;
    ImageFilter filter;
    ArrayList<Button> buttonArrayList = new ArrayList<>();


    private static int sAnimationDuration = 1000;

    private boolean isAnimation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_image);
        glsurfaceView = findViewById(R.id.gl_surface_view);

        buttonArrayList.add(findViewById(R.id.btn_circle));
        buttonArrayList.add(findViewById(R.id.btn_multi));
        buttonArrayList.add(findViewById(R.id.btn_rect));
        buttonArrayList.add(findViewById(R.id.btn_rotate));
        buttonArrayList.add(findViewById(R.id.btn_v_flip));
        buttonArrayList.add(findViewById(R.id.btn_h_flip));
        for (Button button : buttonArrayList)
        {
            button.setOnClickListener(this);
        }

        filter = new ImageFilter(this,
                GlUtils.loadShaderRawResource(this, R.raw.default_vertex_shader),
                GlUtils.loadShaderRawResource(this, R.raw.default_fragment_shader));
        glsurfaceView.setEGLContextClientVersion(2);
        glsurfaceView.setRenderer(filter);
        glsurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glsurfaceView.requestRender();
    }

    @Override
    public void handleImageCallback(Bitmap bitmap)
    {
        if (bitmap != null && filter != null)
        {
            filter.setBitmap(bitmap);
            glsurfaceView.requestRender();
        }
    }

    @Override
    public void onClick(View v)
    {
        if (!v.isClickable())
        {
            return;
        }

        switch (v.getId())
        {
            case R.id.btn_circle:
                break;
            case R.id.btn_multi:
                if (isAnimation) return;
                boolean drawMulti = filter.isDrawMulti();
                filter.setDrawMulti(!drawMulti);
                glsurfaceView.requestRender();
                break;
            case R.id.btn_rect:
                break;
            case R.id.btn_rotate:
                if (isAnimation) return;
                filter.setDrawMulti(false);
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);
                valueAnimator.setDuration(sAnimationDuration);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(animation ->
                {
                    float animationFactor = (float) animation.getAnimatedValue();
                    filter.setAnimationFactor(animationFactor);
                    glsurfaceView.requestRender();
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        filter.setRequestAnimation(false);
                        filter.requestUpdateAngle();
                        filter.setAnimationFactor(0f);
                        v.setClickable(true);
                        isAnimation = false;
                    }

                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        isAnimation = true;
                        v.setClickable(false);
                        filter.setRequestAnimation(true);
                        filter.setSweptAngle(90);
                    }
                });
                valueAnimator.start();
                break;
            case R.id.btn_v_flip:
                //垂直翻转
                if (isAnimation) return;
                boolean isVertical = v.getTag() != null && (Boolean) v.getTag();
                v.setTag(!isVertical);
                filter.setFlipVertical(!isVertical);
                glsurfaceView.requestRender();
                break;
            case R.id.btn_h_flip:
                if (isAnimation) return;
                boolean isHorizontal = v.getTag() != null && (Boolean) v.getTag();
                v.setTag(!isHorizontal);
                filter.setFlipHorizontal(!isHorizontal);
                glsurfaceView.requestRender();
                break;
        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        filter.onResume();
        glsurfaceView.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        filter.onRelease();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        filter.onPause();
        glsurfaceView.onPause();
    }

}
