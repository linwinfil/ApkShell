package cn.poco.audio;

import java.util.List;
import java.util.UUID;

/**
 * Created by menghd on 2017/2/15 0015.
 * wav 文件拼接
 */

public class SoundJoint {
    static {
        System.loadLibrary("audiofactory");
    }

    /**
     * wav 文件拼接(注意：wave头信息必须相同)
     * @param inputFile1
     * @param inputFile2
     * @param outputFile
     * @return
     */
    public static native int joint(String inputFile1, String inputFile2, String outputFile);

    /**
     * 不会为空
     * @param inputFile
     * @return [samplerate][cahnnels][bit]
     */
    public static native int[] getWavHead(String inputFile);

    public static int joint(String outputPath , List<String> wavList){
        int result ;
        if(wavList == null || wavList.size() < 2 ){
            MyLog.e(SoundJoint.class,"error " + MyLog.getLineNumber());
            return -1;
        }

        if(wavList.size() == 2){
            result = joint(wavList.get(0),wavList.get(1),outputPath);
            if(result < 0){
                FileUtils.deleteFile(outputPath);
                MyLog.e(SoundJoint.class,"error " + MyLog.getLineNumber());
                return -1;
            }
        }else {
            String tempOutputWav = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav";
            for (int i = 0 ; i < wavList.size() -1; i ++){
                if(i == 0){
                    result = joint(wavList.get(0), wavList.get(1),tempOutputWav);
                    if(result < 0){
                        FileUtils.deleteFile(tempOutputWav);
                        MyLog.e(SoundJoint.class,"error " + MyLog.getLineNumber());
                        return -1;
                    }
                } else if(i == wavList.size() - 2) {
                    result = joint(tempOutputWav , wavList.get(i + 1),outputPath);
                    FileUtils.deleteFile(tempOutputWav);
                    if(result < 0){
                        FileUtils.deleteFile(outputPath);
                        MyLog.e(SoundJoint.class,"error " + MyLog.getLineNumber());
                        return -1;
                    }
                } else {
                    String tempWav = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav";
                    result = joint(tempOutputWav,wavList.get(i + 1),tempWav);
                    FileUtils.deleteFile(tempOutputWav);
                    if(result < 0){
                        FileUtils.deleteFile(tempWav);
                        MyLog.e(SoundJoint.class,"error " + MyLog.getLineNumber());
                        return -1;
                    }
                    tempOutputWav = tempWav;
                }
            }
        }
        return 1;
    }
}
