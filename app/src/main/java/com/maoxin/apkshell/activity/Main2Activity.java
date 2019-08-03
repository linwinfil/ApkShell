package com.maoxin.apkshell.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.maoxin.apkshell.R;

import java.util.Random;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity
{

    FrameLayout rootLayout;
    FrameLayout viewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        rootLayout = findViewById(R.id.root_layout);
        rootLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("root layout click");
            }
        });
        viewLayout = findViewById(R.id.view_layout);
        viewLayout.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v)
            {
                Random random = new Random();
                int i = random.nextInt(255);
                viewLayout.setBackgroundColor(Color.rgb(i, i * 0.3f, i * 0.5f));
                System.out.println("view layout click");
            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (viewLayout.getVisibility() == View.VISIBLE) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN)
            {
                float x = ev.getX();
                float y = ev.getY();
                if (y > viewLayout.getTop())
                {
                    return super.dispatchTouchEvent(ev);
                }
                else {
                    return true;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
