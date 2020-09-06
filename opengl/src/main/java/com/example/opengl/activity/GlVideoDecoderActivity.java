package com.example.opengl.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.opengl.R;
import com.example.opengl.decoder.OnVideoDecoderListener;
import com.example.opengl.decoder.VideoDecoder;
import com.example.opengl.decoder.VideoDecoderThread;
import com.example.opengl.gl.GSurfaceView;
import com.example.opengl.gl.filter.OesDecoderFilter;
import com.example.opengl.gl.filter.OnFilterListenerAdapter;
import com.example.opengl.gl.utils.GlUtils;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import kotlin.Pair;
import kotlin.Triple;

public class GlVideoDecoderActivity extends BaseActivity {
    public static final int REQUEST_PICK_VIDEO = 2;

    GSurfaceView glSurfaceView;
    OesDecoderFilter filter;
    VideoDecoderThread decoderThread;
    OnVideoDecoderListener mDecoderListener;
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

        mDecoderListener = new OnVideoDecoderListener() {
            @Override
            @WorkerThread
            public void onPrepared(VideoDecoder videoDecoder)
            {
                runOnUiThread(() -> {
                    if (filter != null) {
                        filter.setVideoSize(videoDecoder.getWidth(), videoDecoder.getHeight());
                        filter.setPrepared();
                    }
                    videoDecoder.ready4Render();
                });
            }

            @WorkerThread
            @Override
            public void onDraw(long presentationTime)
            {
            }

            @Override
            public void onFinish() {

            }
        };

        filter = new OesDecoderFilter(this,
                GlUtils.loadShaderRawResource(this, R.raw.oes_vertex_shader),
                GlUtils.loadShaderRawResource(this, R.raw.oes_fragment_shader));
        filter.setOnFilterListener(mOnFilterListener);
        glSurfaceView.setRenderer(filter);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 0x22);
        }
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0x22) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "外部读取权限失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onItemSelectedMenu(MenuItem item)
    {
        if (item.getItemId() == R.id.mPicker) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("video/*");
            this.startActivityForResult(photoPickerIntent, REQUEST_PICK_VIDEO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQUEST_PICK_VIDEO && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            LoadVideoTask.CallBack callBack = new LoadVideoTask.CallBack() {

                @Override
                public void callback(@Nullable String path) {
                    if (decoderThread != null) {
                        decoderThread.onRelease();
                    }
                    decoderThread = new VideoDecoderThread(path, filter.getSurface());
                    decoderThread.getVideoDecoder().setVideoListener(mDecoderListener);
                    decoderThread.start();
                }
            };
            Triple<WeakReference<Context>, Uri, WeakReference<LoadVideoTask.CallBack>> triple = new Triple<>(new WeakReference<>(this), uri, new WeakReference<>(callBack));
            new LoadVideoTask().execute(triple);
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

    protected static final class LoadVideoTask
            extends AsyncTask<Triple<WeakReference<Context>, Uri, WeakReference<LoadVideoTask.CallBack>>, Void, Pair<LoadVideoTask.CallBack, String>> {

        public interface CallBack {
            void callback(@Nullable String path);
        }

        @Override
        protected void onPostExecute(Pair<CallBack, String> callBackStringPair) {
            if (callBackStringPair != null) {
                callBackStringPair.getFirst().callback(callBackStringPair.getSecond());
            }
        }

        @Override
        protected Pair<LoadVideoTask.CallBack, String> doInBackground(Triple<WeakReference<Context>, Uri, WeakReference<CallBack>>... triples) {
            Triple<WeakReference<Context>, Uri, WeakReference<CallBack>> triple = triples[0];
            Context context = triple.getFirst().get();
            Uri uri = triple.getSecond();
            LoadVideoTask.CallBack callBack = triple.getThird().get();

            String[] projection = {MediaStore.Video.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                String out;
                try {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    cursor.moveToFirst();
                    out = cursor.getString(column_index);
                } finally {
                    cursor.close();
                }
                return new Pair<>(callBack, out);
            } else {
                return null;
            }
        }
    }
}
