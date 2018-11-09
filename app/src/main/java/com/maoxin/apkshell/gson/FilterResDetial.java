package com.maoxin.apkshell.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author lmx
 * Created by lmx on 2018/4/9.
 */
public class FilterResDetial
{
    @SerializedName("id")
    public String id;

    @SerializedName("pushID")
    public String pushId;

    @SerializedName("name")
    public String name;

    @SerializedName("listThumb")
    public String listThumb;

    @SerializedName("alpha")
    public String alpha;

    @SerializedName("thumb")
    public String thumb;

    @SerializedName("resType")
    public String resType;

    @SerializedName("camera")
    public boolean camera;

    @SerializedName("watermark")
    public boolean watermark;

    @SerializedName("watermark_pic")
    public String watermark_pic;

    @SerializedName("vignette")
    public boolean vignette;

    @SerializedName("skipFace")
    public boolean skipFace;

    @SerializedName("res")
    public List<FilterParam> res;

    public static class FilterParam
    {
        @SerializedName("img")
        public String img;

        @SerializedName("skipFace")
        public boolean skipFace;

        @SerializedName("params")
        public int[] params;
    }
}
