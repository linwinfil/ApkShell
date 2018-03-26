package com.maoxin.apkshell;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.maoxin.apkshell.audio.ClipMusicTask;
import com.maoxin.apkshell.audio.JointMultiAudioTask;
import com.maoxin.apkshell.audio.OnProcessListener;

import java.io.File;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity
{
    private static final String TAG = "Main2Activity";

    private Button button;

    private static final int PERMISSION_REQUEST_CODE = 1;


    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "click ");

                // String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Music" + File.separator + "SuweeraBoonrodGunlaegun.mp3";
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Music" + File.separator + "Avicii_Present_The_Days.mp3";//70
                String outPath = com.maoxin.apkshell.audio.FileUtils.GetAppPath() + File.separator + "clip_test.mp3";
                ClipMusicTask clipRunnable = new ClipMusicTask(path, 1000L * 30, 1000L * 90, outPath);
                clipRunnable.setOnProcessListener(new OnProcessListener()
                {
                    @Override
                    public void onStart()
                    {
                        dialog.show();

                        Log.d(TAG, "Main2Activity --> clipRunnable onStart: ");
                    }

                    @Override
                    public void onFinish()
                    {
                        dialog.cancel();
                        Log.d(TAG, "Main2Activity --> clipRunnable onFinish: ");
                    }

                    @Override
                    public void onError(String message)
                    {
                        Log.d(TAG, "Main2Activity --> clipRunnable onError: " + message);
                    }
                });

                new Thread(clipRunnable).start();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "click ");
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Music" + File.separator + "Avicii_Present_The_Days.mp3";//70
                String outPath = com.maoxin.apkshell.audio.FileUtils.GetAppPath() + File.separator + "clip_test.aac";
                ArrayList<String> jointPath = new ArrayList<>();
                jointPath.add(path);
                jointPath.add(path);
                jointPath.add(path);
                JointMultiAudioTask task = new JointMultiAudioTask(jointPath, outPath);
                task.setListener(new OnProcessListener()
                {
                    long start;
                    @Override
                    public void onStart()
                    {
                        start = System.currentTimeMillis();
                        dialog.show();
                        Log.d(TAG, "Main2Activity --> JointMultiAudioTask onStart: ");
                    }

                    @Override
                    public void onFinish()
                    {
                        dialog.cancel();
                        Log.d(TAG, "Main2Activity --> JointMultiAudioTask onFinish: " + (System.currentTimeMillis() - start) / 1000L);
                    }

                    @Override
                    public void onError(String message)
                    {
                        Log.d(TAG, "Main2Activity --> JointMultiAudioTask onError: " + message);
                    }
                });
                new Thread(task).start();
            }
        });


        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (PermissionChecker.checkSelfPermission(Main2Activity.this , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED
                            || PermissionChecker.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission_group.STORAGE)) {
                        }
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    } else {
                        button.setClickable(true);
                    }
                } else {
                    button.setClickable(true);
                }
            }
        }).start();

    }

    public static class TestHandlerThread extends Handler
    {

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                button.setClickable(true);
            }
        }
    }
}
