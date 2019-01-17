package com.maoxin.apkshell.beauty.data.base;

import com.maoxin.apkshell.beauty.data.BeautyShapeDataUtils;
import com.maoxin.apkshell.beauty.data.ShapeDataType;

/**
 * 美颜参数
 *
 * @author lmx
 * Created by lmx on 2018/11/21.
 */
public class BeautyArgs extends AbsArgs
{
    public BeautyArgs(@ShapeDataType int shapeType, float radius, float strength)
    {
        super(shapeType, radius, strength);
    }

    public BeautyArgs(int shapeType)
    {
        super(shapeType);
    }

    @Override
    public float getValue4UI(float ui_value)
    {
        StrengthArea area = getArea();
        UIArea uiArea = getUIArea();
        if (checkAreaValid(area) && checkAreaValid(uiArea))
        {
            float min = area.getMin();
            float max = area.getMax();

            float ui_min = uiArea.getMin();
            float ui_max = uiArea.getMax();

            float out = ui2NativeValue(min, max, ui_min, ui_max, ui_value);
            return BeautyShapeDataUtils.formatFloat(out, 1);
        }
        return 0f;
    }

    @Override
    public float getUI4Value(float value)
    {
        StrengthArea area = getArea();
        UIArea uiArea = getUIArea();
        if (checkAreaValid(area) && checkAreaValid(uiArea))
        {
            float min = area.getMin();
            float max = area.getMax();

            float ui_min = uiArea.getMin();
            float ui_max = uiArea.getMax();


            float out = native2UIValue(ui_min, ui_max, min, max, value);
            return BeautyShapeDataUtils.formatFloat(out, 1);
        }
        return 0f;
    }

    @Override
    public BeautyArgs Clone()
    {
        BeautyArgs out;
        try
        {
            out = (BeautyArgs) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            out = null;
        }
        if (out == null)
        {
            out = new BeautyArgs(this.getShapeType(), this.getRadius(), this.getStrength());
        }

        cloneArea(out);
        cloneUIArea(out);
        return out;
    }
}
