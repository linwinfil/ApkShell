package com.maoxin.apkshell.view;

import android.view.animation.Interpolator;

import androidx.annotation.FloatRange;

// 弹性插值器
public class SpringInterpolator implements Interpolator
{
    private float mFactor;

    public SpringInterpolator()
    {
        setFactorSize(0.4f);
    }

    /**
     * 动画系数，系数越小，弹性次数越多
     * @param factor
     */
    public void setFactorSize(@FloatRange(from = 0f, to = 2f) float factor)
    {
        mFactor = factor;
    }

    @Override
    public float getInterpolation(float input)
    {
        return (float) (Math.pow(2, -10 * input) * Math.sin((input - mFactor / 4) * (2 * Math.PI) / mFactor) + 1f);
    }
}
