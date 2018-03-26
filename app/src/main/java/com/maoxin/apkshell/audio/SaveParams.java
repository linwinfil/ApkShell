package com.maoxin.apkshell.audio;

import java.util.ArrayList;

/**
 * @author lmx
 *         Created by lmx on 2018-02-23.
 */

public class SaveParams
{
    // ** 音乐 **
    public String musicPath;
    public long musicStart;
    public float musicVolume;
    public float videoVolume;

    // ** 视频 **
    public long videoDuration;

    public ArrayList<VideoInfo> videoInfos;


    public static class VideoInfo
    {
        public String videoPath;
        public long videoDuration;
    }
}
