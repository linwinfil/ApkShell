package com.maoxin.apkshell.beauty.data.base;

import android.support.annotation.Nullable;

import com.maoxin.apkshell.beauty.data.BeautyShapeDataUtils;
import com.maoxin.apkshell.beauty.data.ShapeDataType;


/**
 * 脸型参数
 *
 * @author lmx
 * Created by lmx on 2018/11/21.
 */
public class ShapeArgs extends AbsArgs
{
    public ShapeArgs(@ShapeDataType int shapeType, float radius, float strength)
    {
        super(shapeType, radius, strength);
    }

    public ShapeArgs(int shapeType)
    {
        super(shapeType);
    }

    @Override
    public float getValue4UI(float ui_value)
    {
        float out = 0;
        //额头的UI反过来换算（等比）
        if (getShapeType() == ShapeDataType.FOREHEAD)
        {
            if (ui_value != 0)
            {
                ui_value *= -1;
            }
        }

        StrengthArea area = getArea();
        UIArea uiArea = getUIArea();
        if (checkAreaValid(area) && checkAreaValid(uiArea))
        {
            float min = area.getMin();
            float max = area.getMax();
            float mid = area.getMid();

            float ui_min = uiArea.getMin();
            float ui_max = uiArea.getMax();
            float ui_mid = uiArea.getMid();

            if (isBidirectional())
            {
                out = ui2NativeValue(min, mid, max, ui_min, ui_mid, ui_max, ui_value);
                out = BeautyShapeDataUtils.formatFloat(out, 1);
            }
            else
            {
                out = ui2NativeValue(min, max, ui_min, ui_max, ui_value);
                out = BeautyShapeDataUtils.formatFloat(out, 1);
            }
        }
        return out;
    }

    @Override
    public float getUI4Value(float value)
    {
        float out = 0;
        StrengthArea area = getArea();
        UIArea uiArea = getUIArea();
        if (checkAreaValid(area) && checkAreaValid(uiArea))
        {
            float min = area.getMin();
            float max = area.getMax();
            float mid = area.getMid();

            float ui_min = uiArea.getMin();
            float ui_max = uiArea.getMax();
            float ui_mid = uiArea.getMid();

            if (isBidirectional())
            {
                out = native2UIValue(ui_min, ui_mid, ui_max, min, mid, max, value);
                out = BeautyShapeDataUtils.formatFloat(out, 1);
            }
            else
            {
                out = native2UIValue(ui_min, ui_max, min, max, value);
                out = BeautyShapeDataUtils.formatFloat(out, 1);
            }
        }
        //额头的UI反过来换算（等比）
        if (getShapeType() == ShapeDataType.FOREHEAD)
        {
            if (out != 0)
            {
                out *= -1;
            }
        }
        return out;
    }


    @Nullable
    @Override
    public ShapeArgs Clone()
    {
        ShapeArgs out = null;
        try
        {
            out = (ShapeArgs) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            out = null;
        }
        if (out == null)
        {
            out = new ShapeArgs(this.getShapeType(), this.getRadius(), this.getStrength());
        }

        cloneArea(out);
        cloneUIArea(out);
        return out;
    }
}
