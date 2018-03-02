package cn.poco.audio;

import android.os.Environment;

import java.io.File;

/**
 * Created by menghd on 2017/3/9 0009.
 *
 */

public class AudioConfig {
    public final static int AUDIO_TYPE_AAC = 0x10000;
    public final static int AUDIO_TYPE_PCM = 0x10001;
    public final static int AUDIO_TYPE_WAV = 0x10002;

    private static String tempFolderPath = Environment.getExternalStorageDirectory() + "/audiofactory/data/";
    public static void setTempFolderPath(String folderPath){
        tempFolderPath = folderPath;
    }
    public static String getTempFolderPath(){
        File folder = new File(tempFolderPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        return tempFolderPath;
    }
}
