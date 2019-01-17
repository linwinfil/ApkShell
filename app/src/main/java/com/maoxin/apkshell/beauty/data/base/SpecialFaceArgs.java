package com.maoxin.apkshell.beauty.data.base;


import com.maoxin.apkshell.beauty.data.BeautyShapeDataUtils;

/**
 * 专属脸型参数
 *
 * @author lmx
 * Created by lmx on 2018/11/23.
 */
public class SpecialFaceArgs extends AbsArgs
{
    public SpecialFaceArgs(int shapeType, float radius, float strength)
    {
        super(shapeType, radius, strength);
    }

    public SpecialFaceArgs(int shapeType)
    {
        super(shapeType);
    }


    @Override
    public float getValue4UI(float ui_value)
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


            out = /*com.adnonstop.gl.filter.data.shape.ShapeData.*/ui2NativeValue(min, mid, max, ui_min, ui_mid, ui_max, ui_value);
            return BeautyShapeDataUtils.formatFloat(out, 1);
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

            out = /*com.adnonstop.gl.filter.data.shape.ShapeData.*/native2UIValue(ui_min, ui_mid, ui_max, min, mid, max, value);
            return BeautyShapeDataUtils.formatFloat(out, 1);
        }
        return out;
    }

    @Override
    public SpecialFaceArgs Clone()
    {
        SpecialFaceArgs out;
        try
        {
            out = (SpecialFaceArgs) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            out = null;
        }
        if (out == null)
        {
            out = new SpecialFaceArgs(this.getShapeType(), this.getRadius(), this.getStrength());
        }

        cloneArea(out);
        cloneUIArea(out);

        return out;
    }

    @Override
    public String toString()
    {
        return "SpecialFaceArgs{} " + super.toString();
    }
}
