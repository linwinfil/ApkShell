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
import android.widget.Button;

import com.example.opengl.R;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.VideoFilter;

import androidx.annotation.Nullable;

public class VideoActivity extends BaseActivity
{
    public static final int REQUEST_PICK_VIDEO = 2;

    GSurfaceView glSurfaceView;
    Button button;
    VideoFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        button = findViewById(R.id.btn_play);
        glSurfaceView = findViewById(R.id.gl_surface_view);

        glSurfaceView.setEGLContextClientVersion(2);
        filter = new VideoFilter(this, null, null);
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
            filter.setData(uri);
            filter.prepare();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
