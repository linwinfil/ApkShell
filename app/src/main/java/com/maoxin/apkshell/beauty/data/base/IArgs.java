package com.maoxin.apkshell.beauty.data.base;

/**
 * @author lmx
 * Created by lmx on 2018/11/22.
 */
public interface IArgs extends Cloneable
{
    public float getRadius();

    public void setRadius(float radius);

    public float getStrength();

    public void setStrength(float strength);

    public IArgs Clone();

    public StrengthArea getArea();

    public void setArea(StrengthArea area);
}
