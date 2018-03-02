package cn.poco.audio;

import java.util.UUID;

/**
 * Created by menghd on 2017/3/7 0007.
 *
 */

public class GenerateMuteAudio {
    static {
        System.loadLibrary("audiofactory");
    }
    public static native int generteMutePcm(long sampleRate, int channels , int  bit , double duration , String outputPath);

    public static boolean generteMuteWav(long sampleRate, int channels , int  bit , double duration , String outputPath){
        String tempMutePcm = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".pcm";
        int ret = generteMutePcm(sampleRate,channels,bit,duration,tempMutePcm);
        if(ret < 0){
            MyLog.i(GenerateMuteAudio.class," generteMutePcm fail! " +MyLog.getLineNumber());
            return false;
        }

        return PcmToWav.pcmToWav(tempMutePcm,outputPath,sampleRate,channels);
    }
}
