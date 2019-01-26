package com.maoxin.apkshell.beauty.data;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

/**
 * @author lmx
 * Created by lmx on 2017-12-15.
 */

public class BeautyShapeDataUtils
{
    private static final String TAG = "bbb";

    //美颜模块
    public static final float COVER_UI_RATE_MAX = 10f;

    //补妆模块
    public static final float COVER_UI_RATE_MAX_MAKEUPS = 5f;


    /**
     * 根据{@link ShapeDataType} 判断对应的seek类型{@link STag.SeekBarType}
     *
     * @param type
     * @return
     */
    @STag.SeekBarType
    public static int GetSeekbarType(@ShapeDataType int type)
    {
        switch (type)
        {
            //瘦脸、小脸、削脸、颧骨、大眼、瘦鼻单向seek
            //额头、眼角、眼距、鼻高、下巴、嘴型、鼻翼、嘴巴整体高度 双向seek

            //新增加
            //鼻尖、鼻梁，丰唇、嘴宽 双向seek
            //亮眼、祛眼袋、鼻子立体 单向seek

            //新增加 补妆
            //唇彩（双向）、腮红（双向）、眉毛（双向）、修容（单向）（多款）

            //新增加 整体瘦脸 20181122
            //整体瘦脸 单向

            //美颜类均为单向seek
            case ShapeDataType.THINFACE:
            case ShapeDataType.LITTLEFACE:
            case ShapeDataType.SHAVEDFACE:
            case ShapeDataType.CHEEKBONES:
            case ShapeDataType.BIGEYE:
            case ShapeDataType.SHRINKNOSE:
            case ShapeDataType.EYEBRIGHT:
            case ShapeDataType.EYEBAGS:
            case ShapeDataType.NOSEFACESHADOW:
            case ShapeDataType.SMILE:
            case ShapeDataType.WHOLEFACE:
            case ShapeDataType.MAKEUP_SHADOW_GROUP:
            case ShapeDataType.MAKEUP_SHADOW_1:
            case ShapeDataType.MAKEUP_SHADOW_2:
            case ShapeDataType.MAKEUP_SHADOW_3:
            case ShapeDataType.MAKEUP_SHADOW_4:
            case ShapeDataType.MAKEUP_SHADOW_NATIVE:
            case ShapeDataType.MAKEUP_SHADOW_NONE:
                return STag.SeekBarType.SEEK_TAG_UNIDIRECTIONAL;
            case ShapeDataType.CHIN:
            case ShapeDataType.MOUTH:
            case ShapeDataType.FOREHEAD:
            case ShapeDataType.CANTHUS:
            case ShapeDataType.EYESPAN:
            case ShapeDataType.NOSEHEIGHT:
            case ShapeDataType.MOUSEHEIGHT:
            case ShapeDataType.NOSEWING:
            case ShapeDataType.NOSETIP:
            case ShapeDataType.NOSERIDGE:
            case ShapeDataType.MOUTHTHICKNESS:
            case ShapeDataType.MOUTHWIDTH:
            case ShapeDataType.MAKEUP_LIP:
            case ShapeDataType.MAKEUP_BLUSHER:
            case ShapeDataType.MAKEUP_EYEBROW:
                return STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL;
            case ShapeDataType.UNSET:
            case ShapeDataType.SMOOTHSKIN:
            case ShapeDataType.TEETHWHITENING:
            case ShapeDataType.SKINWHITENING:
            case ShapeDataType.CLARITYALPHA:
            default:
                return STag.SeekBarType.SEEK_TAG_UNIDIRECTIONAL;
        }
    }

    private static float GetUIRate4Real(float cof, float real, boolean isBidirectional)
    {
        float value;
        if (isBidirectional)
        {
            value = real / 100 * cof - cof / 2;
        }
        else
        {
            value = real / 100f * cof;
        }
        return formatFloat(value, 1);
    }

    /**
     * 底层[0，100]或[-100，100]换算成所需UI值，保留一位小数
     *
     * @param real
     */
    @Deprecated
    public static float GetUIRate4Real(float real, @ShapeDataType int type)
    {
        boolean isBid = GetSeekbarType(type) == STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL;
        float out;
        float[] floats = GetCovertArea(type);
        if (floats != null)
        {
            out = CoverUIRate4Real(floats, real, isBid);
            //Log.d(TAG, String.format("BeautyShapeDataUtils --> GetUIRate4Real: UI值：%s，底层值：%s", out, real));
        }
        else
        {
            out = GetUIRate4Real(COVER_UI_RATE_MAX, real, isBid);
        }

        //TODO 额头的UI反过来换算（等比）
        if (type == ShapeDataType.FOREHEAD) {
            if (out != 0) {
                out *= -1;
            }
        }
        return out;
    }

    /**
     * 彩妆特殊判断
     * 彩妆底层[0，100]，根据区间索引 换算成所需UI值，保留一位小数<br/>
     *
     * @param real
     * @param areaIndex 区间索引<p>
     *                  0：左区间<br/>
     *                  1：右区间<br/>
     */
    @Deprecated
    public static float GetUIRate4Real_MakeUps(float real, int areaIndex, @ShapeDataType int type)
    {
        if (type == ShapeDataType.MAKEUP_LIP
                || type == ShapeDataType.MAKEUP_EYEBROW
                || type == ShapeDataType.MAKEUP_BLUSHER)
        {
            // float out = real / 100f * COVER_UI_RATE_MAX_MAKEUPS;
            // if (type == ShapeDataType.MAKEUP_LIP) {
            //     out = ((real - 0f) / (80f - 0f)) * COVER_UI_RATE_MAX_MAKEUPS;
            // }
            float[] area = GetCovertArea(type);
            float out = (real - area[0]) / (area[1] - area[0]) * COVER_UI_RATE_MAX_MAKEUPS;

            if (areaIndex == 0) {
                //左区间 0-100 UI 反过来换算
                if (out != 0) {
                    out *= -1;
                }
            }
            out = formatFloat(out, 1);
            // LogUtils.i(TAG, "BeautyShapeDataUtils --> GetUIRate4Real_MakeUps: type:" + type + ", value:" + real + ", ui:" + out);
            return out;
        }
        else if (type == ShapeDataType.MAKEUP_SHADOW_1
                || type == ShapeDataType.MAKEUP_SHADOW_2
                || type == ShapeDataType.MAKEUP_SHADOW_3
                || type == ShapeDataType.MAKEUP_SHADOW_4
                || type == ShapeDataType.MAKEUP_SHADOW_NATIVE
                || type == ShapeDataType.MAKEUP_SHADOW_NONE)
        {
            return formatFloat(real / 100f * COVER_UI_RATE_MAX, 1);
        }

        return 0f;
    }

    /**
     * UI值换算成底层[0,100]或[-100，100]的值，保留一位小数
     *
     * @param rate
     * @param type
     * @return
     */
    public static float GetReal4UIRate(float rate, @ShapeDataType int type)
    {
        boolean isBid = GetSeekbarType(type) == STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL;
        float out;
        float[] floats = GetCovertArea(type);

        //TODO 额头的UI反过来换算（等比）
        if (type == ShapeDataType.FOREHEAD) {
            if (rate != 0) {
                rate *= -1;
            }
        }

        if (floats != null)
        {
            out = CoverReal4UIRate(floats, rate, isBid);
            //Log.d(TAG, String.format("BeautyShapeDataUtils --> GetReal4UIRate: UI值：%s，底层值：%s", rate, out));
        }
        else
        {
            out = GetReal4UIRate(COVER_UI_RATE_MAX, rate, isBid);
        }
        return out;
    }

    /**
     * 彩妆特殊判断
     * UI值换算成底层[0,100]或[-100，100]的值，保留一位小数<br/>
     * 区间索引<p>
     * 0：左区间<br/>
     * 1：右区间<br/>
     *
     * @param rate
     * @param type
     * @param
     * @return
     */
    public static float GetReal4UIRate_MakeUps(float rate, @ShapeDataType int type)
    {
        if (type == ShapeDataType.MAKEUP_LIP
                || type == ShapeDataType.MAKEUP_EYEBROW
                || type == ShapeDataType.MAKEUP_BLUSHER)
        {
            if (rate < 0) {
                //左区间
                rate *= -1;
            }

            // float value = rate / COVER_UI_RATE_MAX_MAKEUPS * 100.0f;
            // if (type == ShapeDataType.MAKEUP_LIP) {
            //     value = (rate / COVER_UI_RATE_MAX_MAKEUPS * (80f - 0f)) + 0f;
            // }
            float[] area = GetCovertArea(type);
            float value = ((rate / COVER_UI_RATE_MAX_MAKEUPS) * (area[1] - area[0])) + area[0];

            // LogUtils.i(TAG, "BeautyShapeDataUtils --> GetReal4UIRate_MakeUps: type:" + type + ", value:" + value + ", ui:" + rate);
            return formatFloat(value, 1);
        }
        else if (type == ShapeDataType.MAKEUP_SHADOW_1
                || type == ShapeDataType.MAKEUP_SHADOW_2
                || type == ShapeDataType.MAKEUP_SHADOW_3
                || type == ShapeDataType.MAKEUP_SHADOW_4
                || type == ShapeDataType.MAKEUP_SHADOW_NATIVE
                || type == ShapeDataType.MAKEUP_SHADOW_NONE)
        {
            boolean isBid = GetSeekbarType(type) == STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL;
            return GetReal4UIRate(COVER_UI_RATE_MAX, rate, isBid);
        }
        return 0f;
    }

    private static float GetReal4UIRate(float cof, float rate, boolean isBidirectional)
    {
        float value;
        if (isBidirectional)
        {
            value = (rate + cof / 2) / cof * 100.0f;
        }
        else
        {
            value = rate / cof * 100.0f;
        }
        return formatFloat(value, 1);
    }

    public static float formatFloat(float value, int newScale)
    {
        BigDecimal bg = new BigDecimal(value);
        return bg.setScale(newScale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static float formatHalfUp(float value)
    {
        return Math.round(value) * 1.0f;
    }


    protected static float[] GetCovertArea(@ShapeDataType int type)
    {
        float[] covertArea = new float[2];
        String str = "";
        switch (type)
        {
            case ShapeDataType.THINFACE:
                str = "瘦脸";
                covertArea[0] = 0f;
                covertArea[1] = 65f;
                break;
            case ShapeDataType.BIGEYE:
                str = "椭眼";
                covertArea[0] = 0f;
                covertArea[1] = 70f;
                break;
            case ShapeDataType.CANTHUS:
                str = "眼角";//（眼角）
                covertArea[0] = 20f;
                covertArea[1] = 80f;
                break;
            case ShapeDataType.CHEEKBONES:
                str = "颧骨";
                covertArea[0] = 0f;
                covertArea[1] = 70f;
                break;
            case ShapeDataType.CHIN:
                str = "下巴";
                covertArea[0] = 20f;
                covertArea[1] = 80f;
                break;
            case ShapeDataType.EYESPAN:
                str = "眼距";
                covertArea[0] = 20f;
                covertArea[1] = 80f;
                break;
            case ShapeDataType.FOREHEAD:
                str = "额头";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.LITTLEFACE:
                str = "小脸";
                covertArea[0] = 0f;
                covertArea[1] = 80f;
                break;
            case ShapeDataType.MOUSEHEIGHT:
                str = "嘴巴整体高度";
                covertArea[0] = 20f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.MOUTH:
                str = "嘴巴";
                covertArea[0] = 20f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.NOSEHEIGHT:
                str = "鼻高";
                covertArea[0] = 20f;
                covertArea[1] = 80f;
                break;
            case ShapeDataType.NOSEWING:
                str = "鼻翼";
                covertArea[0] = 30f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.SHAVEDFACE:
                str = "削脸";
                covertArea[0] = 0f;
                covertArea[1] = 80f;
                break;
            case ShapeDataType.SHRINKNOSE:
                str = "瘦鼻";
                covertArea[0] = 0f;
                covertArea[1] = 80f;
                break;
            case ShapeDataType.SMILE:
                str = "微笑";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.EYEBRIGHT:
                str = "亮眼";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
             case ShapeDataType.EYEBAGS:
                 str = "祛眼袋";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
             case ShapeDataType.NOSETIP:
                 str = "鼻尖";
                covertArea[0] = 20f;
                covertArea[1] = 100f;
                break;
             case ShapeDataType.NOSEFACESHADOW:
                 str = "鼻子立体";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
             case ShapeDataType.MOUTHTHICKNESS:
                 str = "丰唇";
                covertArea[0] = 20f;
                covertArea[1] = 80f;
                break;
             case ShapeDataType.MOUTHWIDTH:
                 str = "嘴宽";
                covertArea[0] = 20f;
                covertArea[1] = 80f;
                break;
             case ShapeDataType.NOSERIDGE:
                 str = "鼻梁";
                covertArea[0] = 30f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.CLARITYALPHA:
                str = "清晰";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.SKINWHITENING:
                str = "肤色";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.SMOOTHSKIN:
                str = "美肤";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.TEETHWHITENING:
                str = "美牙";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.MAKEUP_BLUSHER:
                str = "腮红";
                covertArea[0] = 0f;
                covertArea[1] = 35f;
                break;
            case ShapeDataType.MAKEUP_EYEBROW:
                str = "眉毛";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.MAKEUP_LIP:
                str = "唇彩";
                covertArea[0] = 0f;
                covertArea[1] = 25f;
                break;
            case ShapeDataType.WHOLEFACE:
                str = "整体瘦脸";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.MAKEUP_SHADOW_1:
            case ShapeDataType.MAKEUP_SHADOW_2:
            case ShapeDataType.MAKEUP_SHADOW_3:
            case ShapeDataType.MAKEUP_SHADOW_4:
            case ShapeDataType.MAKEUP_SHADOW_NATIVE:
            case ShapeDataType.MAKEUP_SHADOW_NONE:
                str = "修容款式";
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
            case ShapeDataType.UNSET:
            default:
                covertArea[0] = 0f;
                covertArea[1] = 100f;
                break;
        }
        //Log.d("bbb", String.format("BeautyShapeDataUtils --> GetCovertArea: 部位：%s，最大：%s, 最小：%s", str, covertArea[1], covertArea[0]));
        return covertArea;
    }

    /**
     * @param coverArea       coverArea[0]最小值，coverArea[1]最大值
     * @param value           [0，100] 取值
     * @param isBidirectional 是否双向
     * @return
     */
    public static float CoverShapeData(@NonNull @Size(2) float[] coverArea, float value, boolean isBidirectional)
    {
        float out = 0f;
        float min = coverArea[0];
        float max = coverArea[1];
        if (isBidirectional)
        {
            //50为原点
            if (value == 50f)
            {
                return 50f;
            }
            //区间范围值以原点50起点
            if (max >= 50f && min <= 50f)
            {
                if (value > 50f)
                {
                    out = (value - 50f) / 50f * (max - 50f) + 50f;
                }
                else if (value < 50)
                {
                    out = 50f - (50 - value) / 50f * (50f - min);
                }
            }
        }
        else
        {
            if (value == 0f)
            {
                return min;
            }

            if (value > 0)
            {
                out = (value / 100f) * (max - min);
            }
        }
        return out;
    }

    @Deprecated
    public static float CoverReal4UIRate(@NonNull @Size(2) float[] coverArea, float rate, boolean isBidirectional)
    {
        float out = 0;
        float min = coverArea[0];
        float max = coverArea[1];
        if (isBidirectional)
        {
            //非等比区间换算
            if (rate == 0f) return 50f;
            if (rate == -(COVER_UI_RATE_MAX * 1f / 2f)) return min;
            if (rate == (COVER_UI_RATE_MAX * 1f / 2f)) return max;

            if (rate > 0)
            {
                out = rate / (COVER_UI_RATE_MAX * 1f / 2) * (max - 50f) + 50f;
                out = formatFloat(out, 1);
            }
            if (rate < 0)
            {
                out = (rate + (COVER_UI_RATE_MAX * 1f / 2f)) / (COVER_UI_RATE_MAX * 1f / 2f) * (50 - min) + min;
                out = formatFloat(out, 1);
            }

            //等比计算
            // out = (rate + COVER_UI_RATE_MAX * 1f / 2f) / COVER_UI_RATE_MAX * (max - min) + min;
            // out = formatFloat(out, 1);
        }
        else
        {
            if (rate == 0f) return min;
            if (rate == COVER_UI_RATE_MAX) return max;
            out = rate / COVER_UI_RATE_MAX * (max - min) + min;
            out = formatFloat(out, 1);
        }
        return out;
    }

    /**
     * @param coverArea       coverArea[0]最小值，coverArea[1]最大值
     * @param value           [0，100] float取值
     * @param isBidirectional 是否双向
     * @return
     */
    @Deprecated
    public static float CoverUIRate4Real(@NonNull @Size(2) float[] coverArea, float value, boolean isBidirectional)
    {
        float out = 0f;
        float min = coverArea[0];
        float max = coverArea[1];
        if (isBidirectional)
        {
            if (max == min) return COVER_UI_RATE_MAX;
            if (max == 50f || min == 50f) return 0f;

            if (value >= max)
            {
                return COVER_UI_RATE_MAX * 1f / 2f;
            }
            if (value <= min)
            {
                return COVER_UI_RATE_MAX * 1f / 2f - COVER_UI_RATE_MAX;
            }

            //50原点（非等比计算）
            if (value == 50f) return 0f;

            if (value > 50f)
            {
                out = (value - 50f) / (max - 50f) * (COVER_UI_RATE_MAX * 1f / 2f);
                out = formatFloat(out, 1);
            }
            else if (value < 50)
            {
                out = ((50f - value) / (50f - min) * ((COVER_UI_RATE_MAX * 1f / 2f) - COVER_UI_RATE_MAX));
                out = formatFloat(out, 1);
            }

            //等比计算
            // out = (value - min) / (max - min) * COVER_UI_RATE_MAX - COVER_UI_RATE_MAX / 2;
            // out = formatFloat(out, 1);
        }
        else
        {
            if (max == min) return COVER_UI_RATE_MAX;
            if (value >= max)
            {
                return COVER_UI_RATE_MAX;
            }
            if (value <= min)
            {
                return 0;
            }

            out = (value - min) / (max - min) * COVER_UI_RATE_MAX;
            out = formatFloat(out, 1);
        }
        return out;
    }

}
