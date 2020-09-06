package com.maoxin.apkshell.view;

import android.graphics.Matrix;

public class Shape
{
    public Matrix mOwnMatrix; // 用于自身缩放、平移
    public Matrix mExtraMatrix; // 用于额外变换

    public Matrix mCurrentStateMatrix; // 记载着视图上呈现出的内容

    public Shape()
    {
        mOwnMatrix = new Matrix();
        mExtraMatrix = new Matrix();
        mCurrentStateMatrix = new Matrix();
    }
}
