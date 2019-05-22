package com.example.opengl.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;

import com.example.opengl.R;
import com.example.opengl.decoder.OnVideoDecoderListener;
import com.example.opengl.decoder.VideoDecoder;
import com.example.opengl.decoder.VideoDecoderThread;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.OesDecoderFilter;
import com.example.opengl.gl.filter.OnFilterListenerAdapter;
import com.example.opengl.gl.utils.GlUtils;

import androidx.annotation.Nullable;

public class GlVideoDecoderActivity extends BaseActivity
{
    public static final int REQUEST_PICK_VIDEO = 2;

    GSurfaceView glSurfaceView;
    OesDecoderFilter filter;
    VideoDecoderThread decoderThread;
    OnVideoDecoderListenerAdaptrer mDecoderListener;
    OnFilterListenerAdapter mOnFilterListener;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_video_decoder);

        glSurfaceView = findViewById(R.id.gl_surface_view);
        glSurfaceView.setEGLContextClientVersion(2);


        mOnFilterListener = new OnFilterListenerAdapter() {
            @Override
            public void onSurfaceCreated()
            {
            }

            @Override
            public void onSurfaceChanged(int width, int height)
            {
            }
        };

        mDecoderListener = new OnVideoDecoderListenerAdaptrer()
        {
            @Override
            public void onPrepared(VideoDecoder videoDecoder)
            {

            }

            @Override
            public void onDraw()
            {
                super.onDraw();
            }
        };

        filter = new OesDecoderFilter(this, GlUtils.loadShaderRawResource(this, R.raw.oes_vertex_shader),
                GlUtils.loadShaderRawResource(this, R.raw.oes_fragment_shader));
        filter.setOnFilterListener(mOnFilterListener);
        glSurfaceView.setRenderer(filter);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
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
            new LoadVideoTask(this, uri, path ->
            {
                if (decoderThread != null) {
                    decoderThread.onRelease();
                }
                decoderThread = new VideoDecoderThread(path, filter.getSurface());
                decoderThread.getVideoDecoder().setVideoListener(mDecoderListener);
                decoderThread.start();
            });
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

    protected static final class LoadVideoTask extends AsyncTask<Void, Void, String>
    {
        public interface CallBack
        {
            void callback(@Nullable String path);
        }

        @SuppressLint("StaticFieldLeak")
        private Context context;
        private Uri uri;
        private CallBack callBack;

        public LoadVideoTask(Context context, Uri uri, CallBack callBack)
        {
            this.context = context;
            this.uri = uri;
            this.callBack = callBack;
        }

        @Override
        protected void onPostExecute(String s)
        {
            if (callBack != null)
            {
                callBack.callback(s);
            }
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            String[] projection = {MediaStore.Video.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null)
            {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            else { return null; }
        }
    }

    public abstract static class OnVideoDecoderListenerAdaptrer implements OnVideoDecoderListener {

        @Override
        public void onPrepared(VideoDecoder videoDecoder)
        {

        }

        @Override
        public void onDraw()
        {

        }

        @Override
        public void onFinish()
        {

        }
    }
}
