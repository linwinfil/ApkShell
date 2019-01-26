package com.maoxin.apkshell.lifecycle;

import android.os.Bundle;

import com.maoxin.apkshell.R;
import com.maoxin.apkshell.lifecycle.ui.demoviewmode.DemoViewModeFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

public class DemoViewModeActivity extends AppCompatActivity
{
    private DemoLifecyclePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_view_mode_activity);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, DemoViewModeFragment.newInstance()).commitNow();
        }

        mPresenter = new DemoLifecyclePresenter();
        getLifecycle().addObserver(mPresenter);
    }

    class DemoLifecyclePresenter extends BaseLifecyclePreseneter
    {
        @Override
        public void onCreate(@NonNull LifecycleOwner owner)
        {

        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner)
        {

        }

        @Override
        public void onResume(@NonNull LifecycleOwner owner)
        {

        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner)
        {

        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner)
        {

        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner)
        {

        }
    }

}
