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
        DemoLifecyclePresenter mPresenter = new DemoLifecyclePresenter();
        getLifecycle().addObserver(mPresenter);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_view_mode_activity);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, DemoViewModeFragment.newInstance()).commitNow();
        }
    }

    class DemoLifecyclePresenter extends BaseLifecyclePresenter
    {
        DemoLifecyclePresenter() {
        }


        @Override
        public void onCreate(@NonNull LifecycleOwner owner)
        {
            sout("lifecycle onCreate");
        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner)
        {
            sout("lifecycle onStart");
        }

        @Override
        public void onResume(@NonNull LifecycleOwner owner)
        {
            sout("lifecycle onResume");
        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner)
        {
            sout("lifecycle onPause");
        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner)
        {
            sout("lifecycle onStop");
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner)
        {
            sout("lifecycle onDestroy");
            getLifecycle().removeObserver(this);
        }

        private void sout(String s) {
            System.out.println(s);
        }
    }

}
