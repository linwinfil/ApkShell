package com.maoxin.apkshell.beauty.data;


import com.maoxin.apkshell.beauty.data.base.StrengthArea;
import com.maoxin.apkshell.beauty.data.base.UIArea;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * 1、官方默认脸型数据值，float取值范围[0f, 100f]<br/>
 * 2、以下预设数据由设计录入文档数据写入<br/>
 *
 * @author lmx
 * Created by lmx on 2017-12-13.
 */

public class SuperShapeData
{
    @IntDef({Type.THINFACE_RADIUS, Type.THINFACE_STRENGTH, Type.LITTLEFACE_RADIUS, Type.LITTLEFACE_STRENGTH, Type.SHAVEDFACE_RADIUS, Type.SHAVEDFACE_STRENGTH, Type.BIGEYE_RADIUS, Type.BIGEYE_STRENGTH, Type.SHRINKNOSE_RADIUS, Type.SHRINKNOSE_STRENGTH, Type.CHIN_RADIUS, Type.CHIN_STRENGTH, Type.MOUTH_RADIUS, Type.MOUTH_STRENGTH, Type.FOREHEAD_RADIUS, Type.FOREHEAD_STRENGTH, Type.CHEEKBONES_RADIUS, Type.CHEEKBONES_STRENGTH, Type.CANTHUS_RADIUS, Type.CANTHUS_STRENGTH, Type.EYESPAN_RADIUS, Type.EYESPAN_STRENGTH, Type.NOSEWING_RADIUS, Type.NOSEWING_STRENGTH, Type.NOSEHEIGHT_RADIUS, Type.NOSEHEIGHT_STRENGTH, Type.MOUSEHEIGHT_RADIUS, Type.MOUSEHEIGHT_STRENGTH, Type.SMILE_RADIUS, Type.SMILE_STRENGTH, Type.NOSETIP_RADIUS, Type.NOSETIP_STRENGTH, Type.MOUTHTHICKNESS_RADIUS, Type.MOUTHTHICKNESS_STRENGTH, Type.MOUTHWIDTH_RADIUS, Type.MOUTHWIDTH_STRENGTH, Type.NOSERIDGE_RADIUS, Type.NOSERIDGE_STRENGTH, Type.WHOLEFACE_RADIUS, Type.WHOLEFACE_STRENGTH, Type.SMOOTHSKIN_STRENGTH, Type.TEETHWHITENING_STRENGTH, Type.SKINWHITENING_STRENGTH, Type.CLARITYALPHA_STRENGTH, Type.EYEBRIGHT_STRENGTH, Type.EYEBAGS_STRENGTH, Type.NOSEFACESHADOW_STRENGTH,})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type
    {
        //瘦脸
        int THINFACE_RADIUS = 0;
        int THINFACE_STRENGTH = 1;

        //小脸
        int LITTLEFACE_RADIUS = 2;
        int LITTLEFACE_STRENGTH = 3;

        //削脸
        int SHAVEDFACE_RADIUS = 4;
        int SHAVEDFACE_STRENGTH = 5;

        //大眼
        int BIGEYE_RADIUS = 6;
        int BIGEYE_STRENGTH = 7;

        //瘦鼻
        int SHRINKNOSE_RADIUS = 8;
        int SHRINKNOSE_STRENGTH = 9;

        //下巴
        int CHIN_RADIUS = 10;
        int CHIN_STRENGTH = 11;

        //嘴巴
        int MOUTH_RADIUS = 12;
        int MOUTH_STRENGTH = 13;

        //额头
        int FOREHEAD_RADIUS = 14;
        int FOREHEAD_STRENGTH = 15;

        //颧骨
        int CHEEKBONES_RADIUS = 16;
        int CHEEKBONES_STRENGTH = 17;

        //眼角
        int CANTHUS_RADIUS = 18;
        int CANTHUS_STRENGTH = 19;

        //眼距
        int EYESPAN_RADIUS = 20;
        int EYESPAN_STRENGTH = 21;

        //鼻翼
        int NOSEWING_RADIUS = 22;
        int NOSEWING_STRENGTH = 23;

        //鼻高
        int NOSEHEIGHT_RADIUS = 24;
        int NOSEHEIGHT_STRENGTH = 25;

        //嘴巴整体高度
        int MOUSEHEIGHT_RADIUS = 26;
        int MOUSEHEIGHT_STRENGTH = 27;

        //微笑
        int SMILE_RADIUS = 28;
        int SMILE_STRENGTH = 29;

        //鼻尖
        int NOSETIP_RADIUS = 30;
        int NOSETIP_STRENGTH = 31;

        //丰唇
        int MOUTHTHICKNESS_RADIUS = 32;
        int MOUTHTHICKNESS_STRENGTH = 33;

        //嘴宽
        int MOUTHWIDTH_RADIUS = 34;
        int MOUTHWIDTH_STRENGTH = 35;

        //鼻梁
        int NOSERIDGE_RADIUS = 36;
        int NOSERIDGE_STRENGTH = 37;

        //整体瘦脸 新增加 20181122
        int WHOLEFACE_RADIUS = 38;
        int WHOLEFACE_STRENGTH = 39;

        //肤质、美肤（磨皮）
        int SMOOTHSKIN_STRENGTH = 40;

        //美牙
        int TEETHWHITENING_STRENGTH = 41;

        //肤色
        int SKINWHITENING_STRENGTH = 42;

        //锐化（清晰）
        int CLARITYALPHA_STRENGTH = 43;

        //亮眼（美颜模块）
        int EYEBRIGHT_STRENGTH = 44;

        //祛眼袋（美颜模块）
        int EYEBAGS_STRENGTH = 45;

        //鼻子立体（美颜模块）
        int NOSEFACESHADOW_STRENGTH = 46;
    }

    //脸型数据参数个数
    private static final int SHAPE_DATA_LENGTH = 47;


    /**
     * 专属脸型-自然脸<br/>
     * 整体瘦脸：def：36.4，max：46.0<br/>
     * 瘦脸：def：15，max：24.6<br/>
     * 颧骨：def：18，max：23.2<br/>
     * 下巴：def：50，max：45.3<br/>
     * 额头：def：50，max：50.0<br/>
     *
     * @return
     */
    protected static SpecialFaceData GetSpecialFaceData_Natural()
    {
        SpecialFaceData out = new SpecialFaceData();
        out.setShapeType(ShapeDataType.FACE_NATURAL);
        out.setRadius(ShapeDataType.WHOLEFACE, 0f);
        out.setStrength(ShapeDataType.WHOLEFACE, 36.4f);
        out.setStrength(ShapeDataType.THINFACE, 15f);
        out.setStrength(ShapeDataType.CHEEKBONES, 18f);
        out.setStrength(ShapeDataType.CHIN, 50f);
        out.setStrength(ShapeDataType.FOREHEAD, 50f);

        //最大最小区间
        out.setStrengthArea(ShapeDataType.WHOLEFACE, new StrengthArea(ShapeDataType.WHOLEFACE, 0, 46f, 36.4f));
        out.setStrengthArea(ShapeDataType.THINFACE, new StrengthArea(ShapeDataType.THINFACE, 0, 24.6f, 15f));
        out.setStrengthArea(ShapeDataType.CHEEKBONES, new StrengthArea(ShapeDataType.CHEEKBONES, 0, 23.2f, 18f));
        out.setStrengthArea(ShapeDataType.CHIN, new StrengthArea(ShapeDataType.CHIN, 50f, 45.3f, 50f));
        out.setStrengthArea(ShapeDataType.FOREHEAD, new StrengthArea(ShapeDataType.FOREHEAD, 50f, 50f, 50f));

        //UI最大区间，定点6为中间默认值
        out = SetSpecialFaceDataUIArea(out);
        return out;
    }


    /**
     * 专属脸型-圆脸专属<br/>
     * 整体瘦脸：def：36.4，max：46.4<br/>
     * 瘦脸：def：19.6，max：25.9<br/>
     * 颧骨：def：29.1，max：35.6<br/>
     * 下巴：def：59.6，max：70.5<br/>
     * 额头：def：62.5，max：68.9<br/>
     *
     * @return
     */
    protected static SpecialFaceData GetSpecialFaceData_Circle()
    {
        SpecialFaceData out = new SpecialFaceData();
        out.setShapeType(ShapeDataType.FACE_CIRCLE);
        out.setRadius(ShapeDataType.WHOLEFACE, 0f);
        out.setStrength(ShapeDataType.WHOLEFACE, 36.4f);
        out.setStrength(ShapeDataType.THINFACE, 19.6f);
        out.setStrength(ShapeDataType.CHEEKBONES, 29.1f);
        out.setStrength(ShapeDataType.CHIN, 59.6f);
        out.setStrength(ShapeDataType.FOREHEAD, 62.5f);

        //最大最小区间
        out.setStrengthArea(ShapeDataType.WHOLEFACE, new StrengthArea(ShapeDataType.WHOLEFACE, 0, 46.4f, 36.4f));
        out.setStrengthArea(ShapeDataType.THINFACE, new StrengthArea(ShapeDataType.THINFACE, 0, 25.9f, 19.6f));
        out.setStrengthArea(ShapeDataType.CHEEKBONES, new StrengthArea(ShapeDataType.CHEEKBONES, 0, 35.6f, 29.1f));
        out.setStrengthArea(ShapeDataType.CHIN, new StrengthArea(ShapeDataType.CHIN, 50f, 70.5f, 59.6f));
        out.setStrengthArea(ShapeDataType.FOREHEAD, new StrengthArea(ShapeDataType.FOREHEAD, 50f, 68.9f, 62.5f));

        //UI最大区间，定点6为中间默认值
        out = SetSpecialFaceDataUIArea(out);
        return out;
    }

    /**
     * 专属脸型-长脸专属<br/>
     * 整体瘦脸：def：34.6，max：46.4<br/>
     * 瘦脸：def：19.6，max：25.9<br/>
     * 颧骨：def：29.1，max：35.6<br/>
     * 下巴：def：36.8，max：28.0<br/>
     * 额头：def：44.1，max：30.5<br/>
     *
     * @return
     */
    protected static SpecialFaceData GetSpecialFaceData_Slim()
    {
        SpecialFaceData out = new SpecialFaceData();
        out.setShapeType(ShapeDataType.FACE_SLIM);
        out.setRadius(ShapeDataType.WHOLEFACE, 0f);
        out.setStrength(ShapeDataType.WHOLEFACE, 34.6f);
        out.setStrength(ShapeDataType.THINFACE, 19.6f);
        out.setStrength(ShapeDataType.CHEEKBONES, 29.1f);
        out.setStrength(ShapeDataType.CHIN, 36.8f);
        out.setStrength(ShapeDataType.FOREHEAD, 44.1f);

        //最大最小区间
        out.setStrengthArea(ShapeDataType.WHOLEFACE, new StrengthArea(ShapeDataType.WHOLEFACE, 0, 46.4f, 34.6f));
        out.setStrengthArea(ShapeDataType.THINFACE, new StrengthArea(ShapeDataType.THINFACE, 0, 25.9f, 19.6f));
        out.setStrengthArea(ShapeDataType.CHEEKBONES, new StrengthArea(ShapeDataType.CHEEKBONES, 0, 35.6f, 29.1f));
        out.setStrengthArea(ShapeDataType.CHIN, new StrengthArea(ShapeDataType.CHIN, 50f, 28f, 36.8f));
        out.setStrengthArea(ShapeDataType.FOREHEAD, new StrengthArea(ShapeDataType.FOREHEAD, 50f, 30.5f, 44.1f));

        //UI最大区间，定点6为中间默认值
        out = SetSpecialFaceDataUIArea(out);
        return out;
    }

    /**
     * UI最大区间，定点6为中间默认值
     *
     * @param data
     * @return
     */
    public static SpecialFaceData SetSpecialFaceDataUIArea(SpecialFaceData data)
    {
        if (data == null) return null;
        data.setUIArea(ShapeDataType.WHOLEFACE, new UIArea(ShapeDataType.WHOLEFACE, 0, 10, 6f));
        data.setUIArea(ShapeDataType.THINFACE, new UIArea(ShapeDataType.THINFACE, 0, 10, 6f));
        data.setUIArea(ShapeDataType.CHEEKBONES, new UIArea(ShapeDataType.CHEEKBONES, 0, 10, 6f));
        data.setUIArea(ShapeDataType.CHIN, new UIArea(ShapeDataType.CHIN, 0, 10, 6f));
        data.setUIArea(ShapeDataType.FOREHEAD, new UIArea(ShapeDataType.FOREHEAD, 0, 10, 6f));
        return data;
    }


}
