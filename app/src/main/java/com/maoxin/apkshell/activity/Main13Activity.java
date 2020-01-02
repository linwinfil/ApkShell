package com.maoxin.apkshell.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.maoxin.apkshell.R;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Android 10 沙盒存储机制
 */
@Route(path = "/activity/main_13")
public class Main13Activity extends AppCompatActivity implements View.OnClickListener
{

    @Autowired(name = "list")
    List<Object> list = null;

    @Autowired(name = "isARouter")
    boolean isARouter = false;

    @Autowired(name =  "isLogin")
    boolean isLogin = false;

    @Autowired(name = "fromClass")
    String fromClass = null;

    public static final int REQUEST_PICK_IMAGE = 0x11;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main13);

        findViewById(R.id.btn_open_media_audio).setOnClickListener(this);
        findViewById(R.id.btn_open_media_image).setOnClickListener(this);
        findViewById(R.id.btn_open_media_video).setOnClickListener(this);
        findViewById(R.id.btn_open_ex_media).setOnClickListener(this);

        //1、传统Intent方式
        // Intent intent = getIntent();
        // boolean isARouter = intent.getBooleanExtra("isARouter", false);
        // String fromClasses = intent.getStringExtra("fromClass");
        // System.out.println("isARouter:" + isARouter + ", fromClass:" + fromClasses);

        //2、ARouter参数注解
        ARouter.getInstance().inject(this);
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.btn_open_media_audio:
            {
                File externalFilesDir = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                break;
            }
            case R.id.btn_open_media_image:
            {

                File externalFilesDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                break;
            }
            case R.id.btn_open_media_video:
            {
                File externalFilesDir = this.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
                break;
            }
            case R.id.btn_open_ex_media:
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                Main13Activity.this.startActivityForResult(intent, REQUEST_PICK_IMAGE);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQUEST_PICK_IMAGE) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
