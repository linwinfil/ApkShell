package cn.poco.video;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.util.List;

import cn.poco.audio.AacEnDecoder;
import cn.poco.audio.AudioUtils;
import cn.poco.audio.MP3DeEncode;

/**
 * Created by admin on 2017/2/22.
 */
public class NativeUtils {

    static {
        System.loadLibrary("ffmpeg");
//        System.loadLibrary("avcodec-56");
//        System.loadLibrary("avformat-56");
//        System.loadLibrary("avdevice-56");
//        System.loadLibrary("avfilter-5");
//        System.loadLibrary("avutil-54");
//        System.loadLibrary("swresample-1");
//        System.loadLibrary("swscale-3");
        System.loadLibrary("SDL2main");
      //  System.loadLibrary("OpenCV");
       // System.loadLibrary("PocoVideo");  //测试添加水印功能
    }


    public static native int opencvtest2(int[] pointinfoX, int[] pointinfoY, Bitmap bitmap, int width, int height);
    public static native int[] opencvtest(int[] buffer , int w, int h);  //改变为灰度图

    /**
     * 改变视频的分辨率并取一帧YUV数据
     * @param dstwidth  目标的宽
     * @param dstheight 目标的高
     * @param pFileName 输入视频的路径
     * @param tempdicretory 临时文件夹路径，最后要有/
     * @param yuvOutput 输出YUV文件的名称
     * @return 成功返回>=0的数
     */
    public static native int changeVideoResolutionAndGetYUV(int dstwidth, int dstheight, String pFileName, String tempdicretory, String yuvOutput); //改变分辨率并获取一帧YUV420SP格式的数据

    /**
     * 根据视频获取RGB24格式的BUFFER大小
     * @param videopath 输入视频的路径
     * @return 返回视频一帧内容所需要的内存
     */
    public static native int getVideoBufferSize(String videopath);  //根据视频获取BUFFER大小


    /**
     * 根据视频获取RGBA格式的BUFFER大小
     * @param videopath 输入视频的路径
     * @return 返回视频一帧内容所需要的内存
     */
    public static native int getVideoRGBABufferSize(String videopath);  //根据视频获取BUFFER大小

    /**
     * 根据宽高获取buffer大小, 此函数会修正宽高为能被16整除的数
     * @param width 宽
     * @param height 高
     * @return 返回此宽高的视频一帧内容所需要的内存
     */
    public static native int getBufferSizeBySelf(Integer width, Integer height);  //根据宽高获取buffer大小, 此函数会修正宽高为能被16整除的数

    /**
     * 获取视频的宽高
     * @param videopath 输入视频路径
     * @param width 存放宽的变量，Integer类型，需要new才可以  例如：Integer width = new Integer(-1)
     * @param height 存放高的变量，Integer类型，需要new才可以  例如：Integer height = new Integer(-1)
     * @return 成功返回>=0的数
     */
    public static native int getVideoWidthAndHeight(String videopath, Integer width, Integer height);

    /**
     * 把视频数据解码后写到YUV文件
     * @param width 存放宽的变量，Integer类型，需要new才可以  例如：Integer width = new Integer(-1)
     * @param height 存放高的变量，Integer类型，需要new才可以  例如：Integer height = new Integer(-1)
     * @param pFileName 输入视频路径
     * @param tempdicretory 临时文件夹，最后要有/
     * @param yuvOutput 输出YUV的名称
     * @return
     */
    public static native int getVideoToYUVFILE(Integer width, Integer height, String pFileName, String tempdicretory, String yuvOutput); //把视频数据解码后写到YUV文件

    public static native byte[] getBlendVideoFrameYUV(String MP4PATH1, String MP4PATH2, int type, int time, Integer width, Integer height, Float pts, Integer len);  //获取一张与水印融合的帧，YUV420SP数据

    /**
     *
     * @param video_in
     * @param videoindex 用来标识视频的索引，范围【0,5】  与video_in一一对应
     * @param width 存放宽的变量，Integer类型，需要new才可以  例如：Integer width = new Integer(-1)
     * @param height 存放高的变量，Integer类型，需要new才可以  例如：Integer height = new Integer(-1)
     * @param databuffer 存放数据的buffer，  要事先申请内存，  内存大小用getVideoBufferSize或者getBufferSizeBySelf获取
     * @return 成功返回视频的旋转角度，  失败返回小于0的数
     */
    public static native int getNextFrameYUVFromFile(String video_in, int videoindex, int width, int height, byte[] databuffer);  //获取一帧YUV420SP的数据
    public static native int getNextFrameYUVFromFileByTime(String video_in, int videoindex, int width, int height,float startTime, float endTime, byte[] databuffer);  //获取一帧YUV420SP的数据
    public static native int getNextFrameYUV420PFromFile(String video_in, int videoindex, int width, int height, byte[] databuffer);  //获取一帧YUV420P的数据
    public static native int getNextFrameYUV420PFromFileByTime(String video_in, int videoindex, int width, int height, float startTime, float endTime, byte[] databuffer);  //获取一帧YUV420P的数据
    public static native int checkVideoResolution(String video1, String video2);  //检查两个视频的宽高是否相同
    public static native float getFPSFromFile(String video_in);  //获取视频的帧率

    /**
     * 获取一帧RGB数据，并存到到buffer中
     * @param video_in  输入视频路径
     * @param videoindex  视频编号
     * @param width  Integer width = new Integer(-1)   函数运行后返回视频宽高存放的变量
     * @param height Integer height = new Integer(-1)   函数运行后返回视频宽高存放的变量
     * @param buffer
     * @return  当前帧的索引（第几帧）
     */
    public static native int getNextFrameFromFile(String video_in, int videoindex, Integer width, Integer height, byte[] buffer);  //获取一帧RGB24格式的数据

    /**
     * 获取一帧RGB24数据，并存到到buffer中
     * @param video_in  输入视频路径
     * @param videoindex  视频编号
     * @param width  Integer width = new Integer(-1)   函数运行后返回视频宽高存放的变量
     * @param height  Integer height = new Integer(-1)   函数运行后返回视频宽高存放的变量
     * @param buffer
     * @return 返回当前帧的时间，单位为秒
     */
    public static native float getNextFrameWithTimeFromFile(String video_in, int videoindex, Integer width, Integer height, byte[] buffer);  //获取一帧RGB24格式的数据

    /**
     * 获取一帧RGBA数据，并存到到buffer中
     * @param video_in  输入视频路径
     * @param videoindex  视频编号
     * @param width  Integer width = new Integer(-1)   函数运行后返回视频宽高存放的变量
     * @param height  Integer height = new Integer(-1)   函数运行后返回视频宽高存放的变量
     * @param buffer
     * @return 返回当前帧的时间，单位为秒
     */
    public static native float getNextFrameRGBAWithTimeFromFile(String video_in, int videoindex, Integer width, Integer height, byte[] buffer);  //获取一帧RGBA格式的数据

    public static native Bitmap decodeFrameBySeekTime(String video_in, int seektime, int videoindex);  //获取一帧数据，返回BITMAP
    public static native Bitmap decodeFrameBySeekTime2(String video_in, int startTime, int endTime, int videoindex);  //根据时间范围获取一帧数据，返回BITMAP
    public static native int getVideoGroupStatus(int videoindex);  //获取一帧数据，返回BITMAP
    public static native int cleanVideoGroupByIndex(int index); //关闭组内index视频的组件   //清理组内某个视频的资源
    public static native int endDecodeFrameBySeekTime();      //关闭组内所有视频的组件
    public static native float getDurationFromFile(String mp4in);  //获取视频的时长
    public static native float getDurationFromFile2(String mp4in, byte[] videobuffer);  //获取视频的时长
    public static native int getFrameNumFromFile(String mp4in);  //获取视频的帧数
    public static native int getFrameNumFromFile2(String mp4in);  //获取视频的帧数(新方法 ，获取准确）
    public static native int setIfDebug(int isdebug);


    /**
     * 截取视频/音频的某一段，追加到新文件中，当对新文件追加结束想保存为文件后，要调用endMixing()
     * @param in_video 输入视频路径
     * @param out_video 输出视频路径
     * @param StartTime 开始截取的时间，单位S
     * @param EndTime 结束截取的时间，单位S
     * @return 成功返回非负数
     */
    public static native float mixVideoSegment(String in_video, String out_video, float StartTime, float EndTime);  //根据时间截取视频，并融合到out_video
    public static native int endMixing();  //结束截取视频，生成最终out_video
    public static native int getAACFromVideo(String video_in, String aac_out);   //获取视频文件的音频流保存为AAC

    /** 把H264/MP4文件与AAC/MP3融合
     *
     * @param MP4PATH mp4/h264路径
     * @param AACPATH AAC/mp3路径，  如果不想添加音频，可以直接用空字符""传入
     * @param OUTPUTMP4
     * @param framerate 融合后视频的帧率，如果想依照原来视频的帧率，可以直接填0
     * @param degree 视频的角度信息"0,90,180,270"
     * @return 成功返回非负数
     */
    public static native int muxerMp4WithRotation(String MP4PATH, String AACPATH, String OUTPUTMP4, int framerate, String degree);   //把H264或者MP4文件与AAC融合,如果framerate为0，则按照原本MP4帧率

    /** 把H264/MP4文件与AAC/MP3融合
     *
     * @param MP4PATH mp4/h264路径
     * @param AACPATH AAC/mp3路径，  如果不想添加音频，可以直接用空字符""传入
     * @param OUTPUTMP4
     * @param framerate 融合后视频的帧率，如果想依照原来视频的帧率，可以直接填0
     * @return 成功返回非负数
     */
    public static native int muxerMp4(String MP4PATH, String AACPATH, String OUTPUTMP4, int framerate);   //把H264或者MP4文件与AAC融合,如果framerate为0，则按照原本MP4帧率

    /**
     * 获取视频的旋转角度
     * @param videoin 输入视频的路径
     * @return 成功返回>=0的数
     */
    public static native int getRotateAngleFromFile(String videoin);  //获取视频的旋转角度

    /** 修改视频旋转角度信息，如果framerate为0，则按照原本MP4帧率
     *
     * @param MP4PATH
     * @param AACPATH AACPATH AAC/mp3路径，  如果不想添加音频，可以直接用空字符""传入
     * @param OUTPUTMP4
     * @param degree 旋转角度
     * @param framerate 融合后视频的帧率，如果想依照原来视频的帧率，可以直接填0
     * @return
     */
    public static native int setMp4Rotation(String MP4PATH, String AACPATH, String OUTPUTMP4, String degree, int framerate);   //修改视频旋转角度信息，如果framerate为0，则按照原本MP4帧率
    public static native int blendVideo(String MP4PATH1, String MP4PATH2, String OUTPUTH264, int type ,int time);  //两个视频融合。中间加渐变效果
    public static native int getH264FromFile( String MP4PATH, String OUTH264PATH);   //获取视频流的H264文件
    public static native int mixH264( String H264, String DSTH264);  //融合两个H264文件
    public static native int speedFilter(String INPUTPATH, String OUTPUTMP4, int startTime, int endTime, float speedratio);  //改变视频封装格式的帧率

    /**
     * 获取视频下一帧的数据并返回Bitmap
     * @param video_in
     * @param videoindex  用于标识视频
     * @return 成功返回Bitmap ， 失败返回null
     */
    public static native Bitmap getNextFrameBitmapFromFile(String video_in, int videoindex);


    public static int muxerMp4AndDelayAudio(String video_in, String aac_in, String video_out, String tempaac, int framerate, int delay)
    {
        int duration = (int)(NativeUtils.getDurationFromFile(video_in));
        AudioUtils.expandAudioDuration(aac_in, tempaac, duration+delay, delay, duration+delay);
        muxerMp4(video_in, tempaac, video_out, framerate);

        return 1;
    }

    /**          MP3转AAC
     *
     * @param mp3path
     * @param aacpath
     * @param tempdirectory  临时文件夹  路径必须存在，最后加/
     * @return
     */
    public static boolean MP3ToAAC(String mp3path, String aacpath, String tempdirectory)
    {
        int samplerate = MP3DeEncode.getSamplerate(mp3path);
        int channel = NativeUtils.getAudioChannels(mp3path);//MP3DeEncode.getChannels(mp3path);
        int ret = -1;
        ret = NativeUtils.getAudioPCM(mp3path, tempdirectory+"temp.pcm");//MP3DeEncode.decode(mp3path, tempdirectory+"temp.pcm");
        ret = AacEnDecoder.encodeAAC(samplerate, channel, 16, tempdirectory+"temp.pcm", aacpath);
        if(ret<0)
            return false;
        return true;
    }

    /**                AAC转MP3
     *
     * @param mp3path
     * @param aacpath
     * @param tempdirectory  临时文件夹  路径必须存在，最后加/
     * @return
     */
    public static boolean AACToMP3(String mp3path, String aacpath, String tempdirectory)
    {
        long samplerate = AacEnDecoder.getSamplerate(aacpath);
        int channels = AacEnDecoder.getChannels(aacpath);
        int ret = -1;
        ret = AacEnDecoder.decodeAAC1(aacpath, tempdirectory+"temp.pcm");
        ret = MP3DeEncode.encode(tempdirectory+"temp.pcm", mp3path, (int)samplerate, channels);
        if(ret<0)
            return false;
        return true;
    }
 public static boolean blendMp4Video(List<String> inputMp4Path, String outputFilePath, String tempDicretory, boolean isGradient, double gradientTime)
    {
        String tempH264 = tempDicretory + "/temp.h264";
        String tempMp4 = tempDicretory + "/temp.mp4";

        NativeUtils.blend(inputMp4Path.get(0), inputMp4Path.get(1), tempH264, tempDicretory, (int)gradientTime);
        NativeUtils.muxerMp4(tempH264, "", tempMp4, 0);
        for(int i=2; i<inputMp4Path.size(); i++)
        {
            NativeUtils.blend(tempMp4, inputMp4Path.get(i), tempH264, tempDicretory, (int)gradientTime);
            NativeUtils.muxerMp4(tempH264, "", tempMp4, 0);
        }

        NativeUtils.getH264FromFile(tempMp4, outputFilePath);
        return true;
    }

    public static boolean blend(String inputMp4Path1, String inputMp4Path2,  String outputFilePath, String tempDicrectory, int gradientTime) {

        String temp1H264 =tempDicrectory+ "/temp1.h264", temp4H264 = tempDicrectory+ "/temp4.h264", rongheH264 =tempDicrectory+ "/ronghe.h264" ,tempH264 =tempDicrectory+ "/temp.h264";
        String mp4_1 = tempDicrectory + "/temp1.mp4", mp4_2 = tempDicrectory + "/temp2.mp4", mp4_3 = tempDicrectory + "/temp3.mp4",
                mp4_4 = tempDicrectory + "/temp4.mp4";
        //// TODO: 2017/3/3 0003 把视频分割成4个小视频，第二第三个视频为时间长度为time的视频
        NativeUtils.mixVideoSegment(inputMp4Path1, mp4_1, 0, (int)(NativeUtils.getDurationFromFile(inputMp4Path1) - gradientTime));
        NativeUtils.endMixing();
        NativeUtils.mixVideoSegment(inputMp4Path1, mp4_2, (int)(NativeUtils.getDurationFromFile(inputMp4Path1) - gradientTime), (int)(NativeUtils.getDurationFromFile(inputMp4Path1)));
        NativeUtils.endMixing();
        NativeUtils.mixVideoSegment(inputMp4Path2, mp4_3, 0, gradientTime);
        NativeUtils.endMixing();
        NativeUtils.mixVideoSegment(inputMp4Path2, mp4_4, gradientTime, (int)(NativeUtils.getDurationFromFile(inputMp4Path2)));
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

    public static void backVideo(String videopath, String tempdiretory, String videoout, int FrameRate)
    {
        new BackVideo().backVideo(videopath, tempdiretory, videoout, FrameRate);
    }

    //videopath  :  输入mp4
    //tempdiretory  :  输出路径的临时文件夹
    //videoout    :   不含路径的文件名，   默认输出在临时文件夹
    //dstWidth,dstHeight   目标宽高
    //videoindex      视频标识
    //callback        状态回调
    public static void changeVideoResolution(String videopath, String tempdiretory, String videoout, int dstWidth, int dstHeight,int bitrate, int videoindex, TaskFinishCallback callback)
    {
        new ChangeVideoResolution().changeVideoResolution(videopath, tempdiretory, videoout, dstWidth, dstHeight,bitrate, videoindex, callback);
    }

    public static void changeVideoResolutionForJane(Context context, String videopath, String tempdiretory, String
            videoout, int dstWidth, int dstHeight, int bitrate, int videoindex, TaskFinishCallback callback,
                                                    VideoResolutionCallBack videoResolutionCallBack)
    {
        new ChangeVideoResolution().changeVideoResolutionForJane(context, videopath, tempdiretory, videoout, dstWidth,
                dstHeight, bitrate, videoindex, callback, videoResolutionCallBack);
    }

    public static void changeVideoResolutionAndFramerate(String videopath, String tempdiretory, String videoout, int dstWidth, int dstHeight,int bitrate, int framerate, int videoindex, TaskFinishCallback callback)
    {
        new ChangeVideoResolutionAndFramerate().changeVideoResolution(videopath, tempdiretory, videoout, dstWidth, dstHeight,bitrate, framerate, videoindex, callback);
    }

    public static void clipVideoAndChangeFramerateResolution(String videopath, String tempdiretory, String videoout, int dstWidth, int dstHeight,int bitrate, int framerate, int videoindex, float startTime, float endTime, TaskFinishCallback callback)
    {
        new ChangeVideoResolutionAndFramerate().clipVideoAndChangeResolution(videopath, tempdiretory, videoout, dstWidth, dstHeight,bitrate, framerate, videoindex, startTime, endTime, callback);
    }
//    public static native int getBlendVideoYUV(String videopath, String pngpath, byte[] buffer, Integer width, Integer height, Float pts);
//    public static native int releaseVideo();
//    public static native int getVideoBufferSize2(String videopath);


//音频区
    public static native int getAudioChannels(String pFileName); //获取音频声道数
    public static native int getAudioPCM(String pFileName, String outFileName);
}



