package com.maoxin.apkshell;

import android.os.Bundle;
import android.os.Handler;

import com.tencent.mmkv.MMKV;

import androidx.appcompat.app.AppCompatActivity;

public class Main11Activity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main11);

        MMKV.initialize(this);


        new Handler().postDelayed(() ->
        {
            MMKV mmkv = MMKV.defaultMMKV();

            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++)
            {
                mmkv.encode("Test" + i, i);
            }
            System.out.println("mmkv encode:" + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++)
            {
                int i1 = mmkv.decodeInt("Test" + i);
            }
            System.out.println("mmkv decode:" + (System.currentTimeMillis() - start));
        }, 600);
    }

}
