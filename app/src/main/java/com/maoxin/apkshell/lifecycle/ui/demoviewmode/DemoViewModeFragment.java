package com.maoxin.apkshell.lifecycle.ui.demoviewmode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maoxin.apkshell.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


public class DemoViewModeFragment extends Fragment
{

    private DemoViewModeViewModel mViewModel;

    public static DemoViewModeFragment newInstance()
    {
        return new DemoViewModeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.demo_view_mode_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DemoViewModeViewModel.class);
        // TODO: Use the ViewModel
    }

}
