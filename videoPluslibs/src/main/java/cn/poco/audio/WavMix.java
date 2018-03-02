package cn.poco.audio;

import android.media.AudioFormat;

import java.util.UUID;

/**
 * Created by menghd on 2017/3/9 0009.
 * Wav格式音频混音
 */

public class WavMix {
    public static boolean mix(String inputFilePath1 , String inputFilePath2, String outputFile,double startPosPercent,double endPosPercent){
        int result = -1;
        String tempPath = AudioConfig.getTempFolderPath();
        String tempInputPcm1 = tempPath + UUID.randomUUID() + ".pcm";
        String tempInputPcm2 = tempPath + UUID.randomUUID() + ".pcm";
        String tempInputPcmMix = tempPath + UUID.randomUUID() + ".pcm";
        int[] headInfo1 = SoundJoint.getWavHead(inputFilePath1);
        int[] headInfo2 = SoundJoint.getWavHead(inputFilePath2);
        result = PcmWav.wavToPcm(inputFilePath1,tempInputPcm1);
        if(result < 0 ){
            FileUtils.deleteFile(tempInputPcm1);
            return false;
        }
        result =  PcmWav.wavToPcm(inputFilePath2,tempInputPcm2);
        if(result < 0 ){
            FileUtils.deleteFile(tempInputPcm1);
            FileUtils.deleteFile(tempInputPcm2);
            return false;
        }

        result = PcmMix.mixPcm(tempInputPcm1,tempInputPcm2,tempInputPcmMix,startPosPercent,endPosPercent);
        FileUtils.deleteFile(tempInputPcm1);
        FileUtils.deleteFile(tempInputPcm2);
        if(result < 0 ){
            FileUtils.deleteFile(tempInputPcmMix);
            return false;
        }

        // TODO: 2017/3/9 0009 sample rate、channels 、bit have to the same
        PcmToWav.pcmToWav(tempInputPcmMix,outputFile,headInfo1[0],(int)headInfo1[1]);
        FileUtils.deleteFile(tempInputPcmMix);
        return true;
    }
}
