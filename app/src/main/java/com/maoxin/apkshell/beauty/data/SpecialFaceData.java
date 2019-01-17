package com.maoxin.apkshell.beauty.data;


import android.util.Log;
import android.util.SparseArray;

import com.maoxin.apkshell.beauty.data.base.BaseData;
import com.maoxin.apkshell.beauty.data.base.SpecialFaceArgs;

/**
 * 专属脸型
 *
 * @author lmx
 * Created by lmx on 2018/11/22.
 */
public class SpecialFaceData extends BaseData<SpecialFaceArgs> implements ISpecialFaceData
{
    private static final String TAG = "SpecialFaceData";

    private int shapeType = ShapeDataType.UNSET;

    public SpecialFaceData()
    {
        SpecialFaceArgs args;

        //瘦脸
        args = new SpecialFaceArgs(ShapeDataType.THINFACE);
        mData.put(ShapeDataType.THINFACE, args);

        //下巴
        args = new SpecialFaceArgs(ShapeDataType.CHIN);
        mData.put(ShapeDataType.CHIN, args);

        // 额头
        args = new SpecialFaceArgs(ShapeDataType.FOREHEAD);
        mData.put(ShapeDataType.FOREHEAD, args);

        //颧骨
        args = new SpecialFaceArgs(ShapeDataType.CHEEKBONES);
        mData.put(ShapeDataType.CHEEKBONES, args);

        //整体瘦脸
        args = new SpecialFaceArgs(ShapeDataType.WHOLEFACE);
        mData.put(ShapeDataType.WHOLEFACE, args);
    }

    public void setShapeType(int shapeType)
    {
        this.shapeType = shapeType;
    }

    @Override
    @ShapeDataType
    public int getShapeType()
    {
        return shapeType;
    }

    @Override
    public float getThinFace()
    {
        return getStrength(ShapeDataType.THINFACE);
    }

    @Override
    public float getChin()
    {
        return getStrength(ShapeDataType.CHIN);
    }


    @Override
    public float getForehead()
    {
        return getStrength(ShapeDataType.FOREHEAD);
    }


    @Override
    public float getWholeFace()
    {
        return getStrength(ShapeDataType.WHOLEFACE);
    }

    @Override
    public float getCheekBones()
    {
        return getStrength(ShapeDataType.CHEEKBONES);
    }

    @Override
    public float getWholeFace_radius()
    {
        return getRadius(ShapeDataType.WHOLEFACE);
    }


    public void updateAllArgs4UI(float ui_value)
    {
        if (checkDataValid(mData))
        {
            int size = this.mData.size();
            for (int i = 0; i < size; i++)
            {
                SpecialFaceArgs args = this.mData.valueAt(i);
                if (args != null)
                {
                    float value = args.getValue4UI(ui_value);
                    args.setStrength(value);
                    Log.i(TAG, "SpecialFaceData --> updateAllArgs4UI: type:" + STag.GetShapeTypeTag(args.getShapeType()) + ", ui:" + ui_value + ", value:" + value);
                }
            }
        }
    }

    /**
     * 深度clone
     *
     * @return
     */
    @Override
    public SpecialFaceData Clone()
    {
        SpecialFaceData out;
        try
        {
            out = (SpecialFaceData) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            out = null;
        }
        if (out == null)
        {
            out = new SpecialFaceData();
        }
        out.setData(cloneData());
        out.setShapeType(this.getShapeType());
        return out;
    }

    @Override
    public BaseData UpdateAllDataTo(BaseData dst)
    {
        if (dst instanceof SpecialFaceData)
        {
            SpecialFaceData dstData = (SpecialFaceData) dst;

            SparseArray<SpecialFaceArgs> this_data = this.getData();

            if (this_data.size() > 0)
            {
                int size = this_data.size();
                for (int i = 0; i < size; i++)
                {
                    SpecialFaceArgs args_from = this_data.valueAt(i);
                    if (args_from != null)
                    {
                        dstData.setStrength(args_from.getShapeType(), args_from.getStrength());
                    }
                }
            }
        }
        return dst;
    }

    @Override
    public boolean HasDiff4Data(BaseData dst)
    {
        if (dst instanceof SpecialFaceData)
        {
            SpecialFaceData dstData = (SpecialFaceData) dst;
            SparseArray<SpecialFaceArgs> this_data = this.getData();
            if (this_data != null)
            {
                for (int i = 0, size = this_data.size(); i < size; i++)
                {
                    SpecialFaceArgs this_args = this_data.valueAt(i);
                    if (this_args != null)
                    {
                        float from = this_args.getStrength();
                        float to = dstData.getStrength(this_args.getShapeType());
                        boolean out = from == to || from == formatHalfUp(to);
                        if (!out) return true;
                    }
                }
            }
        }
        return false;
    }
}
