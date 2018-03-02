package cn.poco.audiovideo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.poco.audio.AacEnDecoder;
import cn.poco.audio.AudioConfig;
import cn.poco.audio.AudioUtils;
import cn.poco.audio.FileUtils;
import cn.poco.audio.MyLog;
import cn.poco.video.NativeUtils;

/**
 * Created by menghd on 2017/3/2 0002.
 * 音视频工具统一对外接口
 */

public class AudioVideoUtils {
    /**
     * 剪切视频文件
     * @param inputFilePath 原视频文件
     * @param outputAacFileList  剪切后生成的多个音频文件
     * @param outputMP4FileList 剪切后生成的多个图像文件
     * @param timestamps 每段音频、图像的开始/结束时间戳
     * @return
     */
    public static boolean clipAudioVideo(String inputFilePath, List<String> outputAacFileList, List<String> outputMP4FileList, List<double[]> timestamps) {

           //// TODO: 2017/3/3 0003 抽离音视频
           NativeUtils.getAACFromVideo(inputFilePath, outputAacFileList.get(0));
           //// TODO: 2017/3/3 0003 剪切音频

           //// TODO: 2017/3/3 0003 剪切图像
        for(int i=0; i<timestamps.size(); i++) {
           NativeUtils.mixVideoSegment(inputFilePath, outputMP4FileList.get(i), (int) (timestamps.get(i)[0] / 1000000), (int) (timestamps.get(i)[1] / 1000000));
            NativeUtils.endMixing();
       }
        return true;
    }

    /**
     * 合并音频、图像文件
     * @param outputFilePath  格式为H264
     * @param inputAacFileList  格式为aac
     * @param inputMP4FileList  格式为mp4
     * @return
     */

    public static boolean jointAudioVideo(String outputFilePath, List<String> inputAacFileList, List<String> inputMP4FileList) {
        // TODO: 2017/3/3 0003 合并音频
        String tempAAC = "/sdcard/android-ffmpeg-tutorial01/temp.aac";
        // TODO: 2017/3/3 0003 合并视频
        String tempH264 = "/sdcard/android-ffmpeg-tutorial01/temp.H264";
        for(int i=0; i<inputMP4FileList.size(); i++) {
            NativeUtils.mixVideoSegment(inputMP4FileList.get(i), "/sdcard/android-ffmpeg-tutorial01/temp"+i+".mp4", 0, (int) NativeUtils.getDurationFromFile(inputMP4FileList.get(i)));
            NativeUtils.endMixing();

            NativeUtils.getH264FromFile("/sdcard/android-ffmpeg-tutorial01/temp"+i+".mp4",  "/sdcard/android-ffmpeg-tutorial01/temp"+i+".h264");
        }
        // TODO: 2017/3/3 0003 音视频合并
        for(int j=0; j<inputMP4FileList.size(); j++)
            NativeUtils.mixH264("/sdcard/android-ffmpeg-tutorial01/temp"+j+".h264", outputFilePath);
        return true;
    }

    /**
     * 声音与图形位移
     * @param inputFilePath
     * @param outputFilePath
     * @param offset 以视频为基准，offset为音频于视频的位移，值为正负数
     * @return
     */
    public static boolean offsetAudioVideo(String inputFilePath, String outputFilePath, double offset) {
        //// TODO: 2017/3/3 0003 抽离音视频
    String tempaac = "/sdcard/android-ffmpeg-tutorial01/temp.aac";
        //// TODO: 2017/3/3 0003 根据误差补充或剪掉音频

        // TODO: 2017/3/3 0003 音视频合并
        NativeUtils.getAACFromVideo(inputFilePath, tempaac);
        return true;
    }

    /**
     * 合并多个视频，并给视频连接处过渡区添加声音、画面渐变特效
     * @param inputMp4Path
     * @param outputFilePath
     * @param outputFilePath
     * @param isGradient  是否渐变
      * @param gradientTime  第一个视频结束前第几秒开始发生渐变
     * @return
     */
    public static boolean blendMp4Video(List<String> inputMp4Path, String outputFilePath, boolean isGradient, double gradientTime) {
        FileUtils.deleteFile(new File(AudioConfig.getTempFolderPath()).listFiles());
        if (inputMp4Path == null || inputMp4Path.size() == 0){
            return false;
        }
        int result = -1;
        boolean resultB = false;
        //抽aac
        List<String> tempInputAacList = new ArrayList<String>();

        //合成后的AAC
        String tempOutputAac = AudioConfig.getTempFolderPath() + UUID.randomUUID()+".aac";

        for(int i = 0 ; i < inputMp4Path.size() ; i ++){
            String tempInputAac = AudioConfig.getTempFolderPath() + UUID.randomUUID()+".aac";
            tempInputAacList.add(tempInputAac);
            result = NativeUtils.getAACFromVideo(inputMp4Path.get(i), tempInputAac);
            if(result < 0){
                return false;
            }
        }

        resultB = AudioUtils.jointAuido(tempOutputAac,tempInputAacList,gradientTime);
        FileUtils.deleteFile(tempInputAacList);
        if(!resultB){
            FileUtils.deleteFile(tempOutputAac);
            return false;
        }

        String tempH264 = AudioConfig.getTempFolderPath() + UUID.randomUUID()+".h264";
        NativeUtils.blendMp4Video(inputMp4Path, tempH264, AudioConfig.getTempFolderPath(), isGradient, gradientTime);

        result = NativeUtils.muxerMp4(tempH264, tempOutputAac, outputFilePath, 0);
        FileUtils.deleteFile(tempH264);
        FileUtils.deleteFile(tempOutputAac);
        if(result < 0){
            FileUtils.deleteFile(outputFilePath);
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            return false;
        }
        return true;
    }

    /**
     * 给视频的声音和特效音频混合
     * @param inputFilePath 原始视频
     * @param outputFilePath 合成后的视频
     * @param bgMusicPathList 背景音频列表
     * @param bgMusicStartEnd 背景音频列表对应的起始、终止位置在主文件的百分百，如0.234.
     * @return
     */
    public static boolean mixBgAudio(String inputFilePath, String outputFilePath ,List<String> bgMusicPathList , List<double[]> bgMusicStartEnd ){
        File videoFile = new File(inputFilePath);
        if(!videoFile.exists()){
            return false;
        }

        int result;
        String tempPath = AudioConfig.getTempFolderPath();

        String tempAAC =tempPath + UUID.randomUUID() +".aac";
        result = NativeUtils.getAACFromVideo(inputFilePath, tempAAC);
        if(result < 0){
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            return false;
        }

        String tempPCM = tempPath + UUID.randomUUID() + ".pcm";

        result = AacEnDecoder.decodeAAC1(tempAAC,tempPCM);
        if(result < 0){
            FileUtils.deleteFile(tempAAC);
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            return false;
        }

        List<String> tempBgMusicPcmList = new ArrayList<>();
        for(int i = 0 ; i < bgMusicPathList.size() ; i ++){
            String tempBgMusicPcm = tempPath + UUID.randomUUID() + ".pcm";
            result = AacEnDecoder.decodeAAC1(bgMusicPathList.get(i),tempBgMusicPcm);
            if(result < 0){
                FileUtils.deleteFile(tempBgMusicPcmList);
                MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
                return false;
            }
            tempBgMusicPcmList.add(tempBgMusicPcm);
        }

        String tempMixPcm = tempPath + UUID.randomUUID() + ".pcm";

        boolean resultMix = AudioUtils.mixAudio(tempPCM,tempMixPcm,tempBgMusicPcmList,bgMusicStartEnd);
        FileUtils.deleteFile(tempPCM);
        FileUtils.deleteFile(tempBgMusicPcmList);
        if(!resultMix){
            FileUtils.deleteFile(tempMixPcm);
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            return false;
        }

        long sampleRate = AacEnDecoder.getSamplerate(tempAAC);
        int channels = AacEnDecoder.getChannels(tempAAC);
        result = AacEnDecoder.encodeAAC(sampleRate,channels,16,tempMixPcm,tempAAC);
        FileUtils.deleteFile(tempMixPcm);
        if(result < 0){
            FileUtils.deleteFile(tempAAC);
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            return false;
        }

        result = NativeUtils.muxerMp4(inputFilePath, tempAAC, outputFilePath, 0);
        FileUtils.deleteFile(tempAAC);
        if(result < 0){
            FileUtils.deleteFile(outputFilePath);
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            return false;
        }

        return true;
    }

    /**
     * 改变视频播放的速度
     * @param video_in 输入的视频
     * @param video_out 输出的视频
     * @param startTime 改变速度的开始时间
     * @param endTime 改变速度的结束时间
     * @param speedratio 速度系数   1为原来的速度， 2为原来时间的两倍  0.5为原来视频的0.5倍
     * @return
     */
    public static boolean changeVideoSpeedRadio(String video_in, String video_out, int startTime, int endTime, float speedratio){
        NativeUtils.speedFilter(video_in, video_out, startTime, endTime, speedratio);
        return true;
    }

    /**
     * 给视频某段的声音变调
     * @param inputVideoPath
     * @param outVideoPath
     * @param pitch 不改变设置为0，负数变为低沉，正数变为尖锐
     * @return
     */
    public static  boolean changeVideoSound(String inputVideoPath , String outVideoPath , float pitch){
        int result;
        boolean bResult;

        String tempInputAAC =AudioConfig.getTempFolderPath() + UUID.randomUUID()+".aac";
        String tempOutputAAC =AudioConfig.getTempFolderPath() + UUID.randomUUID()+".aac";

        result = NativeUtils.getAACFromVideo(inputVideoPath, tempInputAAC);
        if(result < 0){
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            FileUtils.deleteFile(tempInputAAC);
            return false;
        }

        bResult = AudioUtils.changeAacSound(tempInputAAC,tempOutputAAC,1,pitch,1,AudioConfig.AUDIO_TYPE_AAC);
        FileUtils.deleteFile(tempInputAAC);
        if(!bResult){
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            FileUtils.deleteFile(tempOutputAAC);
            return false;
        }

        result = NativeUtils.muxerMp4(inputVideoPath, tempOutputAAC, outVideoPath, 0);
        FileUtils.deleteFile(tempOutputAAC);
        if(result < 0){
            FileUtils.deleteFile(outVideoPath);
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            return false;
        }
        return true;
    }


    /**
     * 变速、变调、既变调又变速
     * @param inputAudioPath
     * @param outAudioPath
     * @param spend 变调变速 （不改变设置为1）
     * @param pitch  变调 （不改变设置为0）
     * @param tempo 变速 （不改变设置为1）
     * @param audioType 输出文件类型  AudioConfig.AUDIO_TYPE_WAV   AudioConfig.AUDIO_TYPE_AAC
     * @return
     */
    public static  boolean changeAudioSound(String inputAudioPath , String outAudioPath ,float spend ,float pitch , float tempo , int audioType){
        if(audioType == AudioConfig.AUDIO_TYPE_AAC){
            return AudioUtils.changeAacSound(inputAudioPath,outAudioPath,spend,pitch,tempo,AudioConfig.AUDIO_TYPE_AAC);
        }

        if(audioType == AudioConfig.AUDIO_TYPE_WAV){
            return AudioUtils.changeWavSound(inputAudioPath,outAudioPath,spend,pitch,tempo,AudioConfig.AUDIO_TYPE_WAV);
        }

        return false;
    }


//    /**
//     * 给视频某段的声音变调、变速
//     * @param inputVideoPath
//     * @param outVideoPath
//     * @param startTime
//     * @param endTime
//     * @param spend 变调变速的倍数 （不改变设置为1）
//     * @param pitch  变调 （不改变设置为0，负数变为低沉，正数变为尖锐）
//     * @param tempo 变速的倍数  （不改变设置为1）
//     * @return
//     */
//    public static  boolean changeVideoSound(String inputVideoPath , String outVideoPath ,double videoDuration ,double startTime, double endTime,float spend ,float pitch , float tempo){
//        int result;
//        boolean bResult;
//
//        String tempInputAac =AudioConfig.getTempFolderPath() + UUID.randomUUID()+".aac";
//        String tempOutputAac =AudioConfig.getTempFolderPath() + UUID.randomUUID()+".aac";
//
//        result = NativeUtils.getAACFromVideo(inputVideoPath, tempInputAac);
//        if(result < 0){
//            FileUtils.deleteFile(tempInputAac);
//            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
//            return false;
//        }
//
//        bResult = AudioUtils.changeAacSound(tempInputAac,tempOutputAac,videoDuration,startTime,endTime,spend,pitch,tempo,AudioConfig.AUDIO_TYPE_AAC);
//        FileUtils.deleteFile(tempInputAac);
//        if(!bResult){
//            FileUtils.deleteFile(tempOutputAac);
//            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
//            return false;
//        }
//
//        result = NativeUtils.muxerMp4(inputVideoPath, tempOutputAac, outVideoPath);
//        FileUtils.deleteFile(tempInputAac);
//        if(result < 0){
//            FileUtils.deleteFile(outVideoPath);
//            return false;
//        }
//        return true;
//    }

    /**
     * 替换视频声音
     * @param inputVideoPath
     * @param outVideoPath
     * @param inputBgSoundPath
     * @return
     */
    public static  boolean replaceVideoBgSound(String inputVideoPath  , String outVideoPath, String inputBgSoundPath){
        int result;
        result = NativeUtils.muxerMp4(inputVideoPath, inputBgSoundPath, outVideoPath, 0);
        if(result < 0){
            FileUtils.deleteFile(outVideoPath);
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            return false;
        }

        return true;
    }

    /**
     * 从mp4中抽离出h264、WAV
     * @param inputVideoPath
     * @param outWavPath
     * @return
     */
    public static boolean separateVideoToH264Wav(String inputVideoPath  , String outWavPath){
        int result;
        boolean bResult;

        String tempOutputAAC =AudioConfig.getTempFolderPath() + UUID.randomUUID()+".aac";

        result = NativeUtils.getAACFromVideo(inputVideoPath, tempOutputAAC);
        if(result < 0){
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            FileUtils.deleteFile(tempOutputAAC);
            return false;
        }

        result = AudioUtils.aacToWav(tempOutputAAC,outWavPath);
        FileUtils.deleteFile(tempOutputAAC);
        if(result < 0){
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            FileUtils.deleteFile(outWavPath);
            return false;
        }
        return true;
    }

    /**
     * mp4和wav合成视频
     * @param inputMp4Path
     * @param inputWavPath
     * @param outputMp4
     * @return
     */
    public static boolean compoundMp4Wav(String inputMp4Path  , String inputWavPath,String outputMp4){
        int result;
        String tempOutputAAC =AudioConfig.getTempFolderPath() + UUID.randomUUID()+".aac";
        result = AudioUtils.wavToAac(inputWavPath,tempOutputAAC);
        if(result < 0){
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            FileUtils.deleteFile(tempOutputAAC);
            return false;
        }

        result = NativeUtils.muxerMp4(inputMp4Path,tempOutputAAC,outputMp4, 0);
        FileUtils.deleteFile(tempOutputAAC);
        if(result < 0){
            MyLog.e(AudioVideoUtils.class,"error !"+MyLog.getLineNumber());
            FileUtils.deleteFile(outputMp4);
            return false;
        }
        return true;
    }

}
