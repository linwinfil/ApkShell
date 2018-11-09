package com.maoxin.apkshell.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 内置资源json通用bean
 *
 * @author lmx
 * Created by lmx on 2018/4/9.
 */
public class ThemeLocalResDetial
{
    /*
    {
            "thumb": "683501501567936694559bb479025a49.jpg",
            "icon": "971991501567937869959bb479e4478b.jpg",
            "art_id": "39000",
            "group": "0",
            "search_key": "39000",
            "theme_type": "filter",
            "stat_id": "1340703",
            "tag_color": "394566",
            "name": "街头潮流",
            "desc": "街头潮流",
            "unlock_title": "街头潮流",
            "unlock_url": "http://wap.adnonstop.com/app_source/prod/public/index.php?r=WapDetail/Boys_camera&id=39000",
            "unlock_str": "『街头潮流』——",
            "unlock_img": "683501501567936694559bb479025a49.jpg",
            "share_title": "街头潮流",
            "share_url": "http://wap.adnonstop.com/app_source/prod/public/index.php?r=WapDetail/Boys_camera&id=39000",
            "share_str": "『街头潮流』——",
            "share_img": "683501501567936694559bb479025a49.jpg",
            "unlock": "free",
            "content": [
            {
                "zip": "155131625201709191957art38914.zip",
                 "id": "38914"
            },
            {
                "zip": "155131625201709191959art38916.zip",
                "id": "38916"
            }
    ]
    }
    */


    @SerializedName("theme_type")
    public String themeType;

    @SerializedName("thumb")
    public String thumb;

    @SerializedName("icon")
    public String icon;

    @SerializedName("art_id")
    public String artId;

    @SerializedName("group")
    public String group;

    @SerializedName("name")
    public String name;

    @SerializedName("search_key")
    public String searchKey;

    @SerializedName("desc")
    public String desc;

    @SerializedName("unlock")
    public String unlock;

    @SerializedName("unlock_title")
    public String unlockTitle;

    @SerializedName("unlock_url")
    public String unlockUrl;

    @SerializedName("unlock_str")
    public String unlockStr;

    @SerializedName("unlock_img")
    public String unlockImg;

    @SerializedName("share_title")
    public String shareTitle;

    @SerializedName("share_url")
    public String shareUrl;

    @SerializedName("share_str")
    public String shareStr;

    @SerializedName("share_img")
    public String shareImg;

    @SerializedName("tag_color")
    public String tagColor;

    @SerializedName("stat_id")
    public String statId;

    @SerializedName("content")
    public List<ContentDetial> content;

    public static class ContentDetial
    {
        @SerializedName("zip")
        public String zip;

        @SerializedName("id")
        public String id;

        public String getZip()
        {
            return zip;
        }

        public String getId()
        {
            return id;
        }
    }
}

