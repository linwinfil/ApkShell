package com.maoxin.apkshell.beauty.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lmx
 *         Created by lmx on 2017-12-15.
 */

public class STag
{
    @IntDef({SeekBarType.SEEK_TAG_UNIDIRECTIONAL, SeekBarType.SEEK_TAG_BIDIRECTIONAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SeekBarType
    {
        int SEEK_TAG_UNIDIRECTIONAL = 0x11;    //单向Seek bar
        int SEEK_TAG_BIDIRECTIONAL = 0x12;     //双向Seek bar
    }

    public static String GetShapeTypeTag(@ShapeDataType int type)
    {
        String str = "";
        switch (type)
        {
            case ShapeDataType.THINFACE:
                str = "瘦脸";
                break;
            case ShapeDataType.BIGEYE:
                str = "椭眼";
                break;
            case ShapeDataType.CANTHUS:
                str = "眼角";//（眼角）
                break;
            case ShapeDataType.CHEEKBONES:
                str = "颧骨";
                break;
            case ShapeDataType.CHIN:
                str = "下巴";
                break;
            case ShapeDataType.EYESPAN:
                str = "眼距";
                break;
            case ShapeDataType.FOREHEAD:
                str = "额头";
                break;
            case ShapeDataType.LITTLEFACE:
                str = "小脸";
                break;
            case ShapeDataType.MOUSEHEIGHT:
                str = "嘴巴整体高度";
                break;
            case ShapeDataType.MOUTH:
                str = "嘴巴";
                break;
            case ShapeDataType.NOSEHEIGHT:
                str = "鼻高";
                break;
            case ShapeDataType.NOSEWING:
                str = "鼻翼";
                break;
            case ShapeDataType.SHAVEDFACE:
                str = "削脸";
                break;
            case ShapeDataType.SHRINKNOSE:
                str = "瘦鼻";
                break;
            case ShapeDataType.SMILE:
                str = "微笑";
                break;
            case ShapeDataType.EYEBRIGHT:
                str = "亮眼";
                break;
            case ShapeDataType.EYEBAGS:
                str = "祛眼袋";
                break;
            case ShapeDataType.NOSETIP:
                str = "鼻尖";
                break;
            case ShapeDataType.NOSEFACESHADOW:
                str = "鼻子立体";
                break;
            case ShapeDataType.MOUTHTHICKNESS:
                str = "丰唇";
                break;
            case ShapeDataType.MOUTHWIDTH:
                str = "嘴宽";
                break;
            case ShapeDataType.NOSERIDGE:
                str = "鼻梁";
                break;
            case ShapeDataType.CLARITYALPHA:
                str = "清晰";
                break;
            case ShapeDataType.SKINWHITENING:
                str = "肤色";
                break;
            case ShapeDataType.SMOOTHSKIN:
                str = "美肤";
                break;
            case ShapeDataType.TEETHWHITENING:
                str = "美牙";
                break;
            case ShapeDataType.MAKEUP_BLUSHER:
                str = "腮红";
                break;
            case ShapeDataType.MAKEUP_EYEBROW:
                str = "眉毛";
                break;
            case ShapeDataType.MAKEUP_LIP:
                str = "唇彩";
                break;
            case ShapeDataType.WHOLEFACE:
                str = "整体瘦脸";
                break;
            case ShapeDataType.MAKEUP_SHADOW_1:
            case ShapeDataType.MAKEUP_SHADOW_2:
            case ShapeDataType.MAKEUP_SHADOW_3:
            case ShapeDataType.MAKEUP_SHADOW_4:
            case ShapeDataType.MAKEUP_SHADOW_NATIVE:
            case ShapeDataType.MAKEUP_SHADOW_NONE:
                str = "修容款式";
                break;
            case ShapeDataType.UNSET:
                break;
            case ShapeDataType.FACE_CIRCLE:
                str = "圆脸专属";
                break;
            case ShapeDataType.FACE_NATURAL:
                str = "自然脸专属";
                break;
            case ShapeDataType.FACE_SLIM:
                str = "长脸专属";
                break;
            case ShapeDataType.MAKEUP_NONE:
                break;
            case ShapeDataType.MAKEUP_SHADOW_GROUP:
                break;
        }
        return str;
    }
}
