package cn.poco.video;

import android.graphics.Bitmap;

import java.io.File;
import java.util.List;

/**
 * Created by admin on 2017/2/22.
 */
public class NativeUtils {

    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("SDL2");
        System.loadLibrary("SDL2main");
    }
    public static native int checkVideoResolution(String video1, String video2);
    public static native float getFPSFromFile(String video_in);
    public static native Bitmap getNextFrameFromFile(String video_in, int videoindex);
    public static native Bitmap decodeFrameBySeekTime(String video_in, int seektime, int videoindex);
    public static native int endDecodeFrameBySeekTime();
    public static native int getDurationFromFile(String mp4in);
    public static native int getFrameNumFromFile(String mp4in);
    public static native int mixVideoSegment(String in_video, String out_video, int StartTime, int EndTime);
    public static native int endMixing();
    public static native int getAACFromVideo(String video_in, String aac_out);
    public static native int muxerMp4(String MP4PATH, String AACPATH, String OUTPUTMP4);
    public static native int blendVideo(String MP4PATH1, String MP4PATH2, String OUTPUTH264, int type ,int time);
    public static native int getH264FromFile( String MP4PATH, String OUTH264PATH);
    public static native int mixH264( String H264, String DSTH264);
    public static native int speedFilter(String INPUTPATH, String OUTPUTMP4, int startTime, int endTime, float speedratio);
 //   demuxer(string mp4, string h264)
 //   muxerH264AndAAC(string h264, string aac, string mp4path)
 public static boolean blendMp4Video(List<String> inputMp4Path, String outputFilePath, String tempDicretory, boolean isGradient, double gradientTime)
 {
     String tempH264 = tempDicretory + "/temp.h264";
     String tempMp4 = tempDicretory + "/temp.mp4";

     NativeUtils.blend(inputMp4Path.get(0), inputMp4Path.get(1), tempH264, tempDicretory, (int)gradientTime);
     NativeUtils.muxerMp4(tempH264, "", tempMp4);
     for(int i=2; i<inputMp4Path.size(); i++)
     {
         NativeUtils.blend(tempMp4, inputMp4Path.get(i), tempH264, tempDicretory, (int)gradientTime);
         NativeUtils.muxerMp4(tempH264, "", tempMp4);
     }

     NativeUtils.getH264FromFile(tempMp4, outputFilePath);
     return true;
 }

    public static boolean blend(String inputMp4Path1, String inputMp4Path2,  String outputFilePath, String tempDicrectory, int gradientTime) {

        String temp1H264 =tempDicrectory+ "/temp1.h264", temp4H264 = tempDicrectory+ "/temp4.h264", rongheH264 =tempDicrectory+ "/ronghe.h264" ,tempH264 =tempDicrectory+ "/temp.h264";
        String mp4_1 = tempDicrectory + "/temp1.mp4", mp4_2 = tempDicrectory + "/temp2.mp4", mp4_3 = tempDicrectory + "/temp3.mp4",
                mp4_4 = tempDicrectory + "/temp4.mp4";
        //// TODO: 2017/3/3 0003 把视频分割成4个小视频，第二第三个视频为时间长度为time的视频
        NativeUtils.mixVideoSegment(inputMp4Path1, mp4_1, 0, NativeUtils.getDurationFromFile(inputMp4Path1) - gradientTime);
        NativeUtils.endMixing();
        NativeUtils.mixVideoSegment(inputMp4Path1, mp4_2, NativeUtils.getDurationFromFile(inputMp4Path1) - gradientTime, NativeUtils.getDurationFromFile(inputMp4Path1));
        NativeUtils.endMixing();
        NativeUtils.mixVideoSegment(inputMp4Path2, mp4_3, 0, gradientTime);
        NativeUtils.endMixing();
        NativeUtils.mixVideoSegment(inputMp4Path2, mp4_4, gradientTime, NativeUtils.getDurationFromFile(inputMp4Path2));
        NativeUtils.endMixing();

        //// TODO: 2017/3/3 0003 获取第一第四个视频的H264流，用于最终合成H264流
        NativeUtils.getH264FromFile(mp4_1, temp1H264);
        NativeUtils.getH264FromFile(mp4_4, temp4H264);


        //// TODO: 2017/3/3 0003 给第二第三个视频解编码，添加渐变特效，并生成为一个融合后的视频
        NativeUtils.blendVideo(mp4_2, mp4_3, rongheH264, 0, gradientTime);

        File f = new File(outputFilePath);
        if (f.exists()) {
            f.delete();
        }

        // TODO: 2017/3/3 0003 拼接第1个视频、融合后的视频、第4个视频
        NativeUtils.mixH264(temp1H264, outputFilePath);
        NativeUtils.mixH264(rongheH264, outputFilePath);
        NativeUtils.mixH264(temp4H264, outputFilePath);
        return true;
    }


}
