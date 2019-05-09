package com.maoxin.apkshell.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.maoxin.apkshell.Mp4ParseUtils;
import com.maoxin.apkshell.R;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Main5ExActivity extends AppCompatActivity
{
    private static final String TAG = "Main5ExActivity";

    private static final int REQUEST_PICK_VIDEO = 1;

    String video_basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "apkshell" + Main5Activity.VIDEO_SEPARATOR;
    String audio_basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "apkshell" + Main5Activity.AUDIO_SEPARATOR;

    TextView mTextView;

    ArrayList<String> mVideoAddStrs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5_ex);

        mVideoAddStrs = new ArrayList<>();

        mTextView = findViewById(R.id.textView);

        findViewById(R.id.button9).setOnClickListener(v ->
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("video/*");
            Main5ExActivity.this.startActivityForResult(intent, REQUEST_PICK_VIDEO);
        });

        findViewById(R.id.button10).setOnClickListener(v -> pullVideo());

        findViewById(R.id.button11).setOnClickListener(v -> mixVideo());
    }

    private void mixVideo()
    {
        if (mVideoAddStrs.size() > 0)
        {
            boolean fail = false;
            String out = (video_basePath + System.currentTimeMillis() + ".mp4");
            try
            {
                long l = System.currentTimeMillis();
                Mp4ParseUtils.extractVideo(mVideoAddStrs, out);
                Log.i(TAG, "mixVideo: " + (System.currentTimeMillis() - l));
            }
            catch (Throwable throwable)
            {
                throwable.printStackTrace();
                fail = true;
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            }
            if (!fail)
            {
                Toast.makeText(this, out, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQUEST_PICK_VIDEO)
        {
            if (resultCode == RESULT_OK && data != null)
            {
                pushVideo(data.getData());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pushVideo(Uri data)
    {
        if (data == null) return;

        final String scheme = data.getScheme();
        String str = null;
        if (scheme == null)
        {
            str = data.getPath();
        }
        else if (ContentResolver.SCHEME_FILE.equals(scheme))
        {
            str = data.getPath();
        }
        else if (ContentResolver.SCHEME_CONTENT.equals(scheme))
        {
            Cursor cursor = this.getContentResolver().query(data, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor)
            {
                if (cursor.moveToFirst())
                {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1)
                    {
                        str = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }

        mVideoAddStrs.add(str);
        updateTextView();
    }

    private void pullVideo()
    {
        if (mVideoAddStrs.size() > 0)
        {
            mVideoAddStrs.remove(mVideoAddStrs.size() - 1);
        }
        updateTextView();
    }

    private void updateTextView()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mVideoAddStrs.size(); i++)
        {
            sb.append(mVideoAddStrs.get(i));
            if (i != mVideoAddStrs.size() - 1)
            {
                sb.append("\n");
            }
        }
        mTextView.setText(sb.toString());
    }
}
