package com.maoxin.apkshell.beauty.data.base;


import com.maoxin.apkshell.beauty.data.BeautyShapeDataUtils;
import com.maoxin.apkshell.beauty.data.STag;
import com.maoxin.apkshell.beauty.data.ShapeDataType;

/**
 * @author lmx
 * Created by lmx on 2018/11/21.
 */
public abstract class AbsArgs implements IArgs
{
    private float radius;
    private float strength;
    private StrengthArea area;
    private UIArea uiArea;


    @ShapeDataType
    private int shapeType = ShapeDataType.UNSET;

    public AbsArgs(@ShapeDataType int shapeType, float radius, float strength)
    {
        this.shapeType = shapeType;
        this.radius = radius;
        this.strength = strength;
    }

    public AbsArgs(int shapeType)
    {
        this.shapeType = shapeType;
    }

    @Override
    public float getRadius()
    {
        return radius;
    }

    @Override
    public void setRadius(float radius)
    {
        this.radius = radius;
    }

    @Override
    public float getStrength()
    {
        return strength;
    }

    @Override
    public void setStrength(float strength)
    {
        this.strength = strength;
    }


    @Override
    public StrengthArea getArea()
    {
        return this.area;
    }

    @Override
    public void setArea(StrengthArea area)
    {
        this.area = area;
    }

    public UIArea getUIArea()
    {
        return uiArea;
    }

    public void setUIArea(UIArea ui_area)
    {
        this.uiArea = ui_area;
    }

    @ShapeDataType
    public int getShapeType()
    {
        return shapeType;
    }

    private void setShapeType(@ShapeDataType int shapeType)
    {
        this.shapeType = shapeType;
    }


    /**
     * 检查区间范围是否有效
     *
     * @param area
     * @return
     */
    public static boolean checkAreaValid(IArea area)
    {
        return area != null;
    }

    /**
     * 是否双向
     *
     * @return
     */
    public boolean isBidirectional()
    {
        return BeautyShapeDataUtils.GetSeekbarType(getShapeType()) == STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL;
    }

    public void cloneArea(AbsArgs dst)
    {
        StrengthArea area = getArea();
        if (dst != null && area != null)
        {
            dst.setArea(area.Clone());
        }
    }

    public void cloneUIArea(AbsArgs dst)
    {
        UIArea uiArea = getUIArea();
        if (dst != null && uiArea != null)
        {
            dst.setUIArea(uiArea.Clone());
        }
    }

    /**
     * ui值 -> 数据值
     *
     * @param ui_value
     * @return
     */
    public abstract float getValue4UI(float ui_value);

    /**
     * 数据值 -> ui值
     *
     * @param value
     * @return
     */
    public abstract float getUI4Value(float value);

    @Override
    public String toString()
    {
        return "AbsArgs{" + "radius=" + radius + ", strength=" + strength + ", area=" + area.toString() + ", uiArea=" + uiArea.toString() + ", shapeType=" + shapeType + '}';
    }

    /**
     * 底层值转ui值，双向(非)线性
     *
     * @param uiMin    ui最小值
     * @param uiMiddle ui中间值
     * @param uiMax    ui最大值
     * @param nMin     底层最小值
     * @param nMiddle  底层中间值
     * @param nMax     底层最大值
     * @param nValue   当前底层值
     * @return
     */
    public static float native2UIValue(float uiMin, float uiMiddle, float uiMax, float nMin, float nMiddle, float nMax, float nValue) {
        if (nValue == nMiddle) {
            return uiMiddle;
        } else if (nValue < nMiddle) {
            return uiMin + (nValue - nMin) / (nMiddle - nMin) * (uiMiddle - uiMin);
        }
        return uiMiddle + (nValue - nMiddle) / (nMax - nMiddle) * (uiMax - uiMiddle);
    }

    /**
     * ui值转底层值，双向(非)线性
     *
     * @param nMin     底层最小值
     * @param nMiddle  底层中间值
     * @param nMax     底层最大值
     * @param uiMin    ui最小值
     * @param uiMiddle ui中间值
     * @param uiMax    ui最大值
     * @param uiValue  当前ui值
     * @return
     */
    public static float ui2NativeValue(float nMin, float nMiddle, float nMax, float uiMin, float uiMiddle, float uiMax, float uiValue) {
        if (uiValue == uiMiddle) {
            return nMiddle;
        } else if (uiValue < uiMiddle) {
            return nMin + (uiValue - uiMin) / (uiMiddle - uiMin) * (nMiddle - nMin);
        }
        return nMiddle + (uiValue - uiMiddle) / (uiMax - uiMiddle) * (nMax - nMiddle);
    }

    /**
     * ui值转底层值
     *
     * @param nMin    底层最小值
     * @param nMax    底层最大值
     * @param uiMin   ui最小值
     * @param uiMax   ui最大值
     * @param uiValue 当前ui值
     * @return
     */
    public static float ui2NativeValue(float nMin, float nMax, float uiMin, float uiMax, float uiValue)
    {
        return nMin + (uiValue - uiMin) / (uiMax - uiMin) * (nMax - nMin);
    }

    /**
     * 底层值转ui值
     *
     * @param uiMin  ui最小值
     * @param uiMax  ui最大值
     * @param nMin   底层最小值
     * @param nMax   底层最大值
     * @param nValue 当前底层值
     * @return
     */
    public static float native2UIValue(float uiMin, float uiMax, float nMin, float nMax, float nValue)
    {
        return uiMin + (nValue - nMin) / (nMax - nMin) * (uiMax - uiMin);
    }
}
