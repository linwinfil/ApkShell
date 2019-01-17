package com.maoxin.apkshell.beauty.data.base;

import com.maoxin.apkshell.beauty.data.BeautyShapeDataUtils;
import com.maoxin.apkshell.beauty.data.STag;
import com.maoxin.apkshell.beauty.data.ShapeDataType;

/**
 * 底层接口定义区间范围
 *
 * @author lmx
 * Created by lmx on 2018/11/22.
 */
public class StrengthArea implements IArea
{
    private float min = 0f;
    private float max = 100f;
    private float mid = -1f;
    @ShapeDataType
    private int shapeType = ShapeDataType.UNSET;

    private String title = "";

    public StrengthArea(@ShapeDataType int shapeType, float min, float max)
    {
        this.min = min;
        this.max = max;
        this.shapeType = shapeType;
    }

    public StrengthArea(@ShapeDataType int shapeType, float min, float max, float mid)
    {
        this.min = min;
        this.max = max;
        this.mid = mid;
        this.shapeType = shapeType;
    }

    @Override
    public boolean isBidirectional()
    {
        return BeautyShapeDataUtils.GetSeekbarType(this.shapeType) == STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL;
    }

    @Override
    public float getMin()
    {
        return min;
    }

    @Override
    public float getMax()
    {
        return max;
    }

    @Override
    public float getMid()
    {
        return mid;
    }

    public int getShapeType()
    {
        return shapeType;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public StrengthArea setTitle(String title)
    {
        this.title = title;
        return this;
    }

    @Override
    public String toString()
    {
        return "StrengthArea{" + "min=" + min + ", max=" + max + ", mid=" + mid + ", shapeType=" + shapeType + ", title='" + title + '\'' + '}';
    }

    @Override
    public StrengthArea Clone()
    {
        StrengthArea out = new StrengthArea(this.shapeType, this.min, this.max, this.mid);
        out.setTitle(this.title);
        return out;
    }
}
