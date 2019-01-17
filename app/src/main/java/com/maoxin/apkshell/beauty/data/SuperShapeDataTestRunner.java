package com.maoxin.apkshell.beauty.data;

import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.maoxin.apkshell.beauty.data.base.ShapeArgs;
import com.maoxin.apkshell.beauty.data.base.SpecialFaceArgs;
import com.maoxin.apkshell.beauty.data.base.StrengthArea;
import com.maoxin.apkshell.beauty.data.base.UIArea;

import java.util.ArrayList;


/**
 * @author lmx
 * Created by lmx on 2018/11/30.
 */
public class SuperShapeDataTestRunner
{

    @Nullable
    public static ShapeArgs getShapeArgs(SparseArray<ShapeArgs> arr, @ShapeDataType int type)
    {
        if (arr == null || type == ShapeDataType.UNSET) return null;
        return arr.get(type);
    }

    public static SparseArray<ShapeArgs> getShapeArgs()
    {
        SparseArray<ShapeArgs> mData = new SparseArray<>();

        ShapeArgs args;

        //肤质、美肤（磨皮）
        args = new ShapeArgs(ShapeDataType.SMOOTHSKIN);
        args.setArea(new StrengthArea(args.getShapeType(), 0, 100));
        args.setUIArea(new UIArea(args.getShapeType(), 0, 10f));
        mData.put(args.getShapeType(), args);

        //美牙
        args = new ShapeArgs(ShapeDataType.TEETHWHITENING);
        args.setArea(new StrengthArea(args.getShapeType(), 0, 100));
        args.setUIArea(new UIArea(args.getShapeType(), 0, 10f));
        mData.put(args.getShapeType(), args);

        //肤色
        args = new ShapeArgs(ShapeDataType.SKINWHITENING);
        args.setArea(new StrengthArea(args.getShapeType(), 0, 100));
        args.setUIArea(new UIArea(args.getShapeType(), 0, 10f));
        mData.put(args.getShapeType(), args);

        //锐化（清晰）
        args = new ShapeArgs(ShapeDataType.CLARITYALPHA);
        args.setArea(new StrengthArea(args.getShapeType(), 0, 100));
        args.setUIArea(new UIArea(args.getShapeType(), 0, 10f));
        mData.put(args.getShapeType(), args);

        //亮眼
        args = new ShapeArgs(ShapeDataType.EYEBRIGHT);
        args.setArea(new StrengthArea(args.getShapeType(), 0, 100));
        args.setUIArea(new UIArea(args.getShapeType(), 0, 10f));
        mData.put(args.getShapeType(), args);

        //祛眼袋
        args = new ShapeArgs(ShapeDataType.EYEBAGS);
        args.setArea(new StrengthArea(args.getShapeType(), 0, 100));
        args.setUIArea(new UIArea(args.getShapeType(), 0, 10f));
        mData.put(args.getShapeType(), args);

        //瘦脸
        args = new ShapeArgs(ShapeDataType.THINFACE);
        args.setArea(new StrengthArea(ShapeDataType.THINFACE, 0f, 65f));
        args.setUIArea(new UIArea(ShapeDataType.THINFACE, 0f, 10f));
        mData.put(args.getShapeType(), args);

        //小脸（单向）
        args = new ShapeArgs(ShapeDataType.LITTLEFACE);
        args.setArea(new StrengthArea(ShapeDataType.LITTLEFACE, 0f, 80f));
        args.setUIArea(new UIArea(ShapeDataType.LITTLEFACE, 0f, 10f));
        mData.put(args.getShapeType(), args);

        //削脸（单向）
        args = new ShapeArgs(ShapeDataType.SHAVEDFACE);
        args.setArea(new StrengthArea(ShapeDataType.SHAVEDFACE, 0f, 80f));
        args.setUIArea(new UIArea(ShapeDataType.SHAVEDFACE, 0f, 10f));
        mData.put(args.getShapeType(), args);

        //大眼（单向）
        args = new ShapeArgs(ShapeDataType.BIGEYE);
        args.setArea(new StrengthArea(ShapeDataType.BIGEYE, 0f, 70f));
        args.setUIArea(new UIArea(ShapeDataType.BIGEYE, 0f, 10f));
        mData.put(args.getShapeType(), args);

        //瘦鼻
        args = new ShapeArgs(ShapeDataType.SHRINKNOSE);
        args.setArea(new StrengthArea(ShapeDataType.SHRINKNOSE, 0f, 80f));
        args.setUIArea(new UIArea(ShapeDataType.SHRINKNOSE, 0f, 10f));
        mData.put(args.getShapeType(), args);

        //下巴
        args = new ShapeArgs(ShapeDataType.CHIN);
        args.setArea(new StrengthArea(ShapeDataType.CHIN, 20f, 80f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.CHIN, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //嘴巴
        args = new ShapeArgs(ShapeDataType.MOUTH);
        args.setArea(new StrengthArea(ShapeDataType.MOUTH, 20f, 100f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.MOUTH, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //额头
        args = new ShapeArgs(ShapeDataType.FOREHEAD);
        args.setArea(new StrengthArea(ShapeDataType.FOREHEAD, 0f, 100f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.FOREHEAD, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //颧骨
        args = new ShapeArgs(ShapeDataType.CHEEKBONES);
        args.setArea(new StrengthArea(ShapeDataType.CHEEKBONES, 0f, 70f));
        args.setUIArea(new UIArea(ShapeDataType.CHEEKBONES, 0f, 10f));
        mData.put(args.getShapeType(), args);

        //眼角
        args = new ShapeArgs(ShapeDataType.CANTHUS);
        args.setArea(new StrengthArea(ShapeDataType.CANTHUS, 20f, 80f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.CANTHUS, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //眼距
        args = new ShapeArgs(ShapeDataType.EYESPAN);
        args.setArea(new StrengthArea(ShapeDataType.EYESPAN, 20f, 80f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.EYESPAN, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //鼻翼
        args = new ShapeArgs(ShapeDataType.NOSEWING);
        args.setArea(new StrengthArea(ShapeDataType.NOSEWING, 30f, 100f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.NOSEWING, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //鼻高
        args = new ShapeArgs(ShapeDataType.NOSEHEIGHT);
        args.setArea(new StrengthArea(ShapeDataType.NOSEHEIGHT, 20f, 80f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.NOSEHEIGHT, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //嘴巴整体高度
        args = new ShapeArgs(ShapeDataType.MOUSEHEIGHT);
        args.setArea(new StrengthArea(ShapeDataType.MOUSEHEIGHT, 20f, 100f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.MOUSEHEIGHT, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //微笑
        args = new ShapeArgs(ShapeDataType.SMILE);
        args.setArea(new StrengthArea(ShapeDataType.SMILE, 0f, 100f));
        args.setUIArea(new UIArea(ShapeDataType.SMILE, 0f, 10f));
        mData.put(args.getShapeType(), args);

        //鼻尖
        args = new ShapeArgs(ShapeDataType.NOSETIP);
        args.setArea(new StrengthArea(ShapeDataType.NOSETIP, 20f, 100f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.NOSETIP, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //丰唇（双向）
        args = new ShapeArgs(ShapeDataType.MOUTHTHICKNESS);
        args.setArea(new StrengthArea(ShapeDataType.MOUTHTHICKNESS, 20f, 80f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.MOUTHTHICKNESS, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //嘴宽（双向）
        args = new ShapeArgs(ShapeDataType.MOUTHWIDTH);
        args.setArea(new StrengthArea(ShapeDataType.MOUTHWIDTH, 20f, 80f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.MOUTHWIDTH, -5, 5, 0));
        mData.put(args.getShapeType(), args);

        //鼻梁（双向）
        args = new ShapeArgs(ShapeDataType.NOSERIDGE);
        args.setArea(new StrengthArea(ShapeDataType.NOSERIDGE, 30f, 100f, 50f));
        args.setUIArea(new UIArea(ShapeDataType.NOSERIDGE, -5, 5, 0));
        mData.put(args.getShapeType(), args);
        return mData;
    }


    public static ArrayList<ShapeValue> get_value_4_ui_default()
    {
        ArrayList<ShapeValue> out = new ArrayList<>();
        ShapeValue value;

        //肤质、美肤（磨皮）
        value = new ShapeValue(ShapeDataType.SMOOTHSKIN, 4);
        out.add(value);

        //美牙
        value = new ShapeValue(ShapeDataType.TEETHWHITENING, 4.0f);
        out.add(value);

        //肤色
        value = new ShapeValue(ShapeDataType.SKINWHITENING, 3.5f);
        out.add(value);

        //锐化（清晰）
        value = new ShapeValue(ShapeDataType.CLARITYALPHA, 2.0f);
        out.add(value);

        //亮眼
        value = new ShapeValue(ShapeDataType.EYEBRIGHT, 3.0f);
        out.add(value);

        //祛眼袋
        value = new ShapeValue(ShapeDataType.EYEBAGS, 5.0f);
        out.add(value);

        //小脸（单向）
        value = new ShapeValue(ShapeDataType.LITTLEFACE, 0f);
        out.add(value);

        //削脸（单向）
        value = new ShapeValue(ShapeDataType.SHAVEDFACE, 0f);
        out.add(value);

        //大眼（单向）
        value = new ShapeValue(ShapeDataType.BIGEYE, 5f);
        out.add(value);

        //瘦鼻
        value = new ShapeValue(ShapeDataType.SHRINKNOSE, 0f);
        out.add(value);

        //下巴
        value = new ShapeValue(ShapeDataType.CHIN, 0f);
        out.add(value);

        //嘴巴
        value = new ShapeValue(ShapeDataType.MOUTH, 0f);
        out.add(value);

        //额头
        value = new ShapeValue(ShapeDataType.FOREHEAD, 0f);
        out.add(value);

        //颧骨
        value = new ShapeValue(ShapeDataType.CHEEKBONES, 0f);
        out.add(value);

        //眼角
        value = new ShapeValue(ShapeDataType.CANTHUS, 1f);
        out.add(value);

        //眼距
        value = new ShapeValue(ShapeDataType.EYESPAN, 0f);
        out.add(value);

        //鼻翼
        value = new ShapeValue(ShapeDataType.NOSEWING, 0f);
        out.add(value);

        //鼻高
        value = new ShapeValue(ShapeDataType.NOSEHEIGHT, 0f);
        out.add(value);

        //嘴巴整体高度
        value = new ShapeValue(ShapeDataType.MOUSEHEIGHT, 0f);
        out.add(value);

        //微笑
        value = new ShapeValue(ShapeDataType.SMILE, 0f);
        out.add(value);

        //鼻尖
        value = new ShapeValue(ShapeDataType.NOSETIP, 0f);
        out.add(value);

        //丰唇（双向）
        value = new ShapeValue(ShapeDataType.MOUTHTHICKNESS, 0f);
        out.add(value);

        //嘴宽（双向）
        value = new ShapeValue(ShapeDataType.MOUTHWIDTH, 0f);
        out.add(value);

        //鼻梁（双向）
        value = new ShapeValue(ShapeDataType.NOSERIDGE, 0f);
        out.add(value);


        return out;
    }


    public static ArrayList<SpecialValue> get_special_value_4_ui_natural()
    {
        ArrayList<SpecialValue> out = new ArrayList<>();

        SpecialValue value;

        //自然
        SpecialFaceData specialFaceData = SuperShapeData.GetSpecialFaceData_Natural();

        //最小0
        value = new SpecialValue(ShapeDataType.FACE_NATURAL, 0);
        value.setSpecialFaceData(specialFaceData);
        out.add(value);

        //默认4
        value = new SpecialValue(ShapeDataType.FACE_NATURAL, 4);
        value.setSpecialFaceData(specialFaceData);
        out.add(value);

        //最大10
        value = new SpecialValue(ShapeDataType.FACE_NATURAL, 10);
        value.setSpecialFaceData(specialFaceData);
        out.add(value);

        return out;
    }

    public static ArrayList<SpecialValue> get_special_value_4_ui_circle()
    {
        ArrayList<SpecialValue> out = new ArrayList<>();

        SpecialValue value;

        //圆脸
        SpecialFaceData specialFaceData = SuperShapeData.GetSpecialFaceData_Circle();

        //最小0
        value = new SpecialValue(ShapeDataType.FACE_CIRCLE, 0);
        value.setSpecialFaceData(specialFaceData);
        out.add(value);

        //默认4
        value = new SpecialValue(ShapeDataType.FACE_CIRCLE, 4);
        value.setSpecialFaceData(specialFaceData);
        out.add(value);

        //最大10
        value = new SpecialValue(ShapeDataType.FACE_CIRCLE, 10);
        value.setSpecialFaceData(specialFaceData);
        out.add(value);

        return out;
    }

    public static ArrayList<SpecialValue> get_special_value_4_ui_slim()
    {
        ArrayList<SpecialValue> out = new ArrayList<>();

        SpecialValue value;

        //长脸
        SpecialFaceData specialFaceData = SuperShapeData.GetSpecialFaceData_Slim();

        //最小0
        value = new SpecialValue(ShapeDataType.FACE_SLIM, 0);
        value.setSpecialFaceData(specialFaceData);
        out.add(value);

        //默认4
        value = new SpecialValue(ShapeDataType.FACE_SLIM, 4);
        value.setSpecialFaceData(specialFaceData);
        out.add(value);

        //最大10
        value = new SpecialValue(ShapeDataType.FACE_SLIM, 10);
        value.setSpecialFaceData(specialFaceData);
        out.add(value);

        return out;
    }

    public static class SpecialValue extends ShapeValue
    {

        private SpecialFaceData specialFaceData;

        public void setSpecialFaceData(SpecialFaceData specialFaceData)
        {
            this.specialFaceData = specialFaceData;
        }

        public SpecialValue(@ShapeDataType int type, float value)
        {
            super(type, value);
        }


        @Override
        public String getValue_LogString()
        {
            checkArgs();
            StringBuilder builder = new StringBuilder();
            builder.append(STag.GetShapeTypeTag(this.type)).append(this.value).append(" --> ");
            SparseArray<SpecialFaceArgs> data = specialFaceData.getData();
            for (int i = 0, size = data.size(); i < size; i++) {
                SpecialFaceArgs args = data.valueAt(i);
                if (args == null) continue;

                builder.append(STag.GetShapeTypeTag(args.getShapeType()))
                        .append("=")
                        .append(args.getShapeType())
                        .append(", value:")
                        .append(args.getValue4UI(this.value))
                        .append(", ui_value:")
                        .append(this.value).append("; ");
            }
            return builder.toString();
        }

        @Override
        protected void checkArgs()
        {
            if (this.specialFaceData == null)
            {
                throw new IllegalStateException("special data is null");
            }
            if (this.specialFaceData.getShapeType() != this.type)
            {
                throw new IllegalStateException("special data type is fail");
            }
        }
    }


    public static class ShapeValue
    {
        @ShapeDataType
        int type = ShapeDataType.UNSET;
        float value = 0f;

        private ShapeArgs args;

        public int getType()
        {
            return type;
        }

        public ShapeValue(@ShapeDataType int type, float value)
        {
            this.type = type;
            this.value = value;
        }

        public ShapeValue setArgs(ShapeArgs args)
        {
            this.args = args;
            return this;
        }

        public String getValue_LogString()
        {
            checkArgs();
            String tag = STag.GetShapeTypeTag(this.type);
            return tag + "=" + this.type + ", ui_value:" + this.value + ", value:" + this.args.getValue4UI(value);
        }


        protected void checkArgs()
        {
            if (this.args == null) throw new IllegalStateException("shape args is null");
            if (this.args.getShapeType() != this.type)
            {
                throw new IllegalStateException("shape type is fail");
            }
        }
    }
}
