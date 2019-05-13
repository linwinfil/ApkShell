package com.example.opengl.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.example.opengl.R;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.ImageFilter;
import com.example.opengl.gl.utils.GlUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GlImageActivity extends AppCompatActivity implements View.OnClickListener
{
    GSurfaceView glsurfaceView;
    ImageFilter filter;
    ArrayList<Button> buttonArrayList = new ArrayList<>();


    private static int sAnimationDuration = 1000;

    private static final int REQUEST_PICK_IMAGE = 1;

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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.gl_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.mPicker:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                GlImageActivity.this.startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                boolean drawMulti = filter.isDrawMulti();
                filter.setDrawMulti(!drawMulti);
                glsurfaceView.requestRender();
                break;
            case R.id.btn_rect:
                break;
            case R.id.btn_rotate:
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
                        v.setClickable(true);
                        filter.setRequestAnimation(false);
                        filter.requestUpdateAngle();
                        filter.setAnimationFactor(0f);
                    }

                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        v.setClickable(false);
                        filter.setRequestAnimation(true);
                        filter.setSweptAngle(90);
                    }
                });
                valueAnimator.start();
                break;
            case R.id.btn_v_flip:
                break;
            case R.id.btn_h_flip:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQUEST_PICK_IMAGE)
        {
            if (resultCode == Activity.RESULT_OK && data != null)
            {
                handleImage(data.getData());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleImage(final Uri selectedImage)
    {
        new LoadImageTask(GlImageActivity.this, selectedImage, bitmap ->
        {
            if (bitmap != null && filter != null)
            {
                filter.setBitmap(bitmap);
                glsurfaceView.requestRender();
            }
        }).execute();
    }

    private static class LoadImageTask extends AsyncTask<Void, Void, Bitmap>
    {

        public interface CallBack
        {
            void callback(Bitmap bitmap);
        }

        public Uri mUri;
        public Context mContext;
        public CallBack mCallBack;

        public LoadImageTask(Context mContext, Uri mUri, CallBack mCallBack)
        {
            this.mContext = mContext;
            this.mUri = mUri;
            this.mCallBack = mCallBack;
        }

        @Override
        protected Bitmap doInBackground(Void... voids)
        {
            try
            {
                InputStream inputStream;
                if (mUri.getScheme().startsWith("http") || mUri.getScheme().startsWith("https"))
                {
                    inputStream = new URL(mUri.toString()).openStream();
                }
                else
                {
                    inputStream = mContext.getContentResolver().openInputStream(mUri);
                }
                return BitmapFactory.decodeStream(inputStream, null, null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            super.onPostExecute(bitmap);
            if (mCallBack != null) mCallBack.callback(bitmap);
        }
    }

}
