package com.maoxin.apkshell.lifecycle;

import android.os.Bundle;

import com.maoxin.apkshell.R;
import com.maoxin.apkshell.lifecycle.ui.demoviewmode.DemoViewModeFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

public class DemoViewModeActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_view_mode_activity);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, DemoViewModeFragment.newInstance()).commitNow();
        }

        DemoLifecyclePresenter mPresenter = new DemoLifecyclePresenter();
        getLifecycle().addObserver(mPresenter);
    }

    class DemoLifecyclePresenter extends BaseLifecyclePreseneter
    {
        @Override
        public void onCreate(@NonNull LifecycleOwner owner)
        {
            System.out.println("lifecycle onCreate");
        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner)
        {
            System.out.println("lifecycle onStart");
        }

        @Override
        public void onResume(@NonNull LifecycleOwner owner)
        {
            System.out.println("lifecycle onResume");
        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner)
        {
            System.out.println("lifecycle onPause");
        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner)
        {
            System.out.println("lifecycle onStop");
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner)
        {
            System.out.println("lifecycle onDestroy");
        }
    }

}
