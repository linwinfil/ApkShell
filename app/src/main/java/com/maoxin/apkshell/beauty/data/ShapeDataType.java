package com.maoxin.apkshell.beauty.data;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * @author lmx
 *         Created by lmx on 2017-12-14.
 */
@IntDef({
        ShapeDataType.UNSET,
        ShapeDataType.THINFACE,
        ShapeDataType.LITTLEFACE,
        ShapeDataType.SHAVEDFACE,
        ShapeDataType.BIGEYE,
        ShapeDataType.SHRINKNOSE,
        ShapeDataType.CHIN,
        ShapeDataType.MOUTH,
        ShapeDataType.FOREHEAD,
        ShapeDataType.CHEEKBONES,
        ShapeDataType.CANTHUS,
        ShapeDataType.EYESPAN,
        ShapeDataType.NOSEWING,
        ShapeDataType.NOSEHEIGHT,
        ShapeDataType.MOUSEHEIGHT,
        ShapeDataType.SMILE,
        ShapeDataType.SMOOTHSKIN,
        ShapeDataType.TEETHWHITENING,
        ShapeDataType.SKINWHITENING,
        ShapeDataType.CLARITYALPHA,
        ShapeDataType.EYEBRIGHT,
        ShapeDataType.EYEBAGS,
        ShapeDataType.NOSETIP,
        ShapeDataType.NOSEFACESHADOW,
        ShapeDataType.MOUTHTHICKNESS,
        ShapeDataType.MOUTHWIDTH,
        ShapeDataType.NOSERIDGE,
        ShapeDataType.MAKEUP_LIP,
        ShapeDataType.MAKEUP_BLUSHER,
        ShapeDataType.MAKEUP_EYEBROW,
        ShapeDataType.MAKEUP_SHADOW_GROUP,
        ShapeDataType.MAKEUP_SHADOW_1,
        ShapeDataType.MAKEUP_SHADOW_2,
        ShapeDataType.MAKEUP_SHADOW_3,
        ShapeDataType.MAKEUP_SHADOW_4,
        ShapeDataType.MAKEUP_SHADOW_NATIVE,
        ShapeDataType.MAKEUP_SHADOW_NONE,
        ShapeDataType.MAKEUP_NONE,
        ShapeDataType.WHOLEFACE,
        ShapeDataType.FACE_NATURAL,
        ShapeDataType.FACE_CIRCLE,
        ShapeDataType.FACE_SLIM,
        ShapeDataType.SHAPE_GROUP_THIN_FACE,
        ShapeDataType.SHAPE_GROUP_EYE,
        ShapeDataType.SHAPE_GROUP_NOSE,
        ShapeDataType.SHAPE_GROUP_MOUTH,
})
@Retention(RetentionPolicy.SOURCE)
public @interface ShapeDataType
{

    //NOTE 以下顺序不可变动

    int UNSET = -1;

    //以下脸型
    int THINFACE = 0;       //瘦脸
    int LITTLEFACE = 1;     //小脸
    int SHAVEDFACE = 2;     //削脸
    int BIGEYE = 3;         //大眼
    int SHRINKNOSE = 4;     //瘦鼻
    int CHIN = 5;           //下巴
    int MOUTH = 6;          //嘴巴
    int FOREHEAD = 7;       //额头
    int CHEEKBONES = 8;     //颧骨
    int CANTHUS = 9;        //眼角
    int EYESPAN = 10;       //眼距
    int NOSEWING = 11;      //鼻翼
    int NOSEHEIGHT = 12;    //鼻高
    int MOUSEHEIGHT = 13;   //嘴巴整体高度
    int SMILE = 14;         //微笑

    //以下美颜
    int SMOOTHSKIN = 15;     //肤质、美肤（磨皮）
    int TEETHWHITENING = 16; //美牙
    int SKINWHITENING = 17;  //肤色
    int CLARITYALPHA = 18;   //锐化（清晰）

    //以下 新增加 20180621
    int EYEBRIGHT = 19;      //亮眼（美颜模块）
    int EYEBAGS = 20;        //祛眼袋（美颜模块）
    int NOSETIP = 21;        //鼻尖（双向）
    int NOSEFACESHADOW = 22;  //鼻子立体（美颜模块）
    int MOUTHTHICKNESS = 23; //丰唇（双向）
    int MOUTHWIDTH = 24;     //嘴宽（双向）
    int NOSERIDGE = 25;      //鼻梁（双向）

    //以下 新增加 20180718
    int MAKEUP_LIP = 26;            //补妆 - 唇彩（渐变彩色 双向）
    int MAKEUP_BLUSHER = 27;        //补妆 - 腮红（渐变彩色 双向）
    int MAKEUP_EYEBROW = 28;        //补妆 - 眉毛（双向）
    int MAKEUP_SHADOW_GROUP = 29;   //补妆 - 修容组
    int MAKEUP_SHADOW_1 = 30;       //补妆 - 修容1（单向）
    int MAKEUP_SHADOW_2 = 31;       //补妆 - 修容2（单向）
    int MAKEUP_SHADOW_3 = 32;       //补妆 - 修容3（单向）
    int MAKEUP_SHADOW_4 = 33;       //补妆 - 修容4（单向）
    int MAKEUP_SHADOW_NATIVE = 34;  //补妆 - 默认底层（单向）
    int MAKEUP_SHADOW_NONE = 35;    //补妆 - 无修容款（）

    //以下 新增加 20181022
    int MAKEUP_NONE = 36;           //补妆 - 全补妆无效果

    //以下 新增加 20181122
    int WHOLEFACE = 37;             //整体瘦脸

    //以下 专属脸型 新增加 20181122
    int FACE_NATURAL = 38;          //自然脸专属
    int FACE_CIRCLE = 39;           //圆脸专属
    int FACE_SLIM = 40;             //长脸专属


    int SHAPE_GROUP_THIN_FACE = 100;    //瘦脸组
    int SHAPE_GROUP_EYE =       101;    //眼镜组
    int SHAPE_GROUP_NOSE = 102;         //鼻子组
    int SHAPE_GROUP_MOUTH = 103;         //嘴巴组
}
