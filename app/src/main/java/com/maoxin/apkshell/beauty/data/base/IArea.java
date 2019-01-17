package com.maoxin.apkshell.beauty.data.base;

/**
 * @author lmx
 * Created by lmx on 2018/11/23.
 */
interface IArea
{
    float getMin();

    float getMax();

    float getMid();

    boolean isBidirectional();

    String getTitle();

    IArea setTitle(String title);

    IArea Clone();
}
