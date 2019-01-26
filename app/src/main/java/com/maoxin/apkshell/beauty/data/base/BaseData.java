package com.maoxin.apkshell.beauty.data.base;

import android.util.SparseArray;

import com.maoxin.apkshell.beauty.data.BeautyShapeDataUtils;
import com.maoxin.apkshell.beauty.data.IData;
import com.maoxin.apkshell.beauty.data.ShapeDataType;

import androidx.annotation.Nullable;

/**
 * @author lmx
 * Created by lmx on 2018/11/22.
 */
public abstract class BaseData<D extends AbsArgs> implements IData
{
    /**
     * 半径radius {@link AbsArgs#getRadius()}、力度Strength{@link AbsArgs#getStrength()、区间范围area{@link AbsArgs#getArea()}}
     */
    protected SparseArray<D> mData = new SparseArray<>();

    public SparseArray<D> getData()
    {
        return mData;
    }

    public void setData(SparseArray<D> mData)
    {
        this.mData = mData;
    }

    public float getStrength(@ShapeDataType int shapeType)
    {
        float out = 0f;
        D args = getArgs(shapeType);
        if (args != null)
        {
            out = args.getStrength();
        }
        return out;
    }

    public boolean setRadius(@ShapeDataType int shapeType, float value)
    {
        D args = getArgs(shapeType);
        if (args != null)
        {
            args.setRadius(value);
            return true;
        }
        return false;
    }

    public boolean setStrength(@ShapeDataType int shapeType, float value)
    {
        D args = getArgs(shapeType);
        if (args != null)
        {
            args.setStrength(value);
            return true;
        }
        return false;
    }

    public float getRadius(@ShapeDataType int shapeType)
    {
        float out = 0f;
        D args = getArgs(shapeType);
        if (args != null)
        {
            out = args.getRadius();
        }
        return out;
    }


    public void setStrengthArea(@ShapeDataType int shapeType, StrengthArea area)
    {
        D args = getArgs(shapeType);
        if (args != null)
        {
            args.setArea(area);
        }
    }

    public StrengthArea getStrengthArea(@ShapeDataType int shapeType)
    {
        D args = getArgs(shapeType);
        if (args != null)
        {
            return args.getArea();
        }
        return null;
    }

    public void setUIArea(@ShapeDataType int shapeType, UIArea area)
    {
        D args = getArgs(shapeType);
        if (args != null)
        {
            args.setUIArea(area);
        }
    }

    public UIArea getUIArea(@ShapeDataType int shapeType)
    {
        D args = getArgs(shapeType);
        if (args != null)
        {
            return args.getUIArea();
        }
        return null;
    }


    public float getArgs_UI_Value(@ShapeDataType int shapeType, float def)
    {
        D args = getArgs(shapeType);
        if (args != null)
        {
            def = args.getUI4Value(args.getStrength());
        }
        return def;
    }

    public float setArgs_Strength_Value_4_UI(@ShapeDataType int shapeType, float ui_value)
    {
        float strength = 0f;
        D args = getArgs(shapeType);
        if (args != null)
        {
            strength = args.getValue4UI(ui_value);
            args.setStrength(strength);
        }
        return strength;
    }


    protected static boolean checkDataValid(SparseArray data)
    {
        return data != null && data.size() > 0;
    }

    @Nullable
    public D getArgs(@ShapeDataType int shapeType)
    {
        if (checkDataValid(mData))
        {
            return mData.get(shapeType);
        }
        return null;
    }

    public void setArgs(@ShapeDataType int shapeType, D args)
    {
        if (checkDataValid(mData) && args != null)
        {
            mData.put(shapeType, args);
        }
    }

    public SparseArray<D> cloneData()
    {
        SparseArray<D> out = new SparseArray<>();
        if (checkDataValid(mData))
        {
            int size = this.mData.size();
            for (int i = 0; i < size; i++)
            {
                D args = this.mData.valueAt(i);
                if (args != null)
                {
                    out.put(this.mData.keyAt(i), (D) args.Clone());
                }
            }
        }
        return out;
    }

    public float formatHalfUp(float value)
    {
        return BeautyShapeDataUtils.formatHalfUp(value);
    }


    public abstract BaseData UpdateAllDataTo(BaseData dst);

    /**
     * 对比参数是否一致
     *
     * @param dst
     * @return true：不一致 false：一致
     */
    public abstract boolean HasDiff4Data(BaseData dst);

    public abstract BaseData Clone();
}
