package com.maoxin.apkshell.beauty.data;

/**
 * 专属脸型 接口定义
 *
 * @author lmx
 * Created by lmx on 2018/11/22.
 */
public interface ISpecialFaceData extends IData
{
    int getShapeType();

    float getThinFace();

    float getChin();

    float getForehead();

    float getCheekBones();

    float getWholeFace();

    float getWholeFace_radius();

    ISpecialFaceData Clone();
}
