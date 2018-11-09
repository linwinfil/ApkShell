package com.maoxin.apkshell;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.maoxin.apkshell.cmd.BannerCore3;

public class Main7Activity extends AppCompatActivity
{
    private static final String TAG = "Main7Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);

        String test = "camera21://page?open=0&layout=1&style=2&filter=3&show=beauty";

        Uri uri = Uri.parse(test);
        BannerCore3.CmdStruct struct = BannerCore3.GetCmdStruct(uri);

        Log.d(TAG, "Main7Activity --> onCreate: ");
    }
}
