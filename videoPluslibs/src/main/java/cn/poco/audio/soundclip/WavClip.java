package cn.poco.audio.soundclip;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by menghd on 2017/2/23 0023.
 * wav格式音频裁剪
 */

public class WavClip {
    private static  final String TAG = WavClip.class.getName();

    public static boolean clip(String inputFilePath , List<String> ouputFile , List<double[]> timestamps){
        CheapSoundFile cheapSoundFile;
        if(ouputFile.size()  != timestamps.size()){
            Log.i(TAG,"input file arg illegal! ");
            return false;
        }
        int blockNum = ouputFile.size();

        try {

            cheapSoundFile = CheapSoundFile.create(new File(inputFilePath));
            if(cheapSoundFile != null){
                for(int i = 0 ; i < blockNum ; i ++ ){
                    long totalFrame = cheapSoundFile.getNumFrames();
                    int startFrame = CheapSoundFile.secondsToFrames(cheapSoundFile,timestamps.get(i)[0]);
                    int frameNum = CheapSoundFile.secondsToFrames(cheapSoundFile,timestamps.get(i)[1])  - startFrame;
                    cheapSoundFile.WriteFile(new File(ouputFile.get(i)),startFrame,frameNum);
                }

            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean clip(File inputFile,File outputFile ,double startTime,double endTime){
        if(inputFile == null){
            Log.i(TAG,"input file is null");
            return false;
        }

        CheapSoundFile cheapSoundFile;
        try {
            cheapSoundFile = CheapSoundFile.create(inputFile);
            if(cheapSoundFile != null){
                int startFrame = CheapSoundFile.secondsToFrames(cheapSoundFile,startTime);
                int frameNum = CheapSoundFile.secondsToFrames(cheapSoundFile,endTime)  - startFrame;
                cheapSoundFile.WriteFile(outputFile,startFrame,frameNum);
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean clip(String inputFilePath,String outputFilePath ,double startTime,double endTime){
        if(inputFilePath == null){
            Log.i(TAG,"input file is null");
            return false;
        }

        File inputFile = new File(inputFilePath);
        if(!inputFile.exists()){
            Log.i(TAG,"input file is null");
        }

        File outputFile = new File(outputFilePath);

        CheapSoundFile cheapSoundFile;
        try {
            cheapSoundFile = CheapSoundFile.create(inputFile);
            if(cheapSoundFile != null){
                int startFrame = CheapSoundFile.secondsToFrames(cheapSoundFile,startTime);
                int frameNum = CheapSoundFile.secondsToFrames(cheapSoundFile,endTime)  - startFrame;
                cheapSoundFile.WriteFile(outputFile,startFrame,frameNum);
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param inputFilePath
     * @param outputFilePath
     * @param isFromStart true:从开裁剪timeLen的长度；false：裁剪timeLen的长度到尾
     * @param timeLen 裁剪的长度
     * @return
     */
    public static boolean clip(String inputFilePath,String outputFilePath ,boolean isFromStart,double timeLen){
        if(inputFilePath == null){
            Log.i(TAG,"input file is null");
            return false;
        }

        File inputFile = new File(inputFilePath);
        if(!inputFile.exists()){
            Log.i(TAG,"input file is null");
        }

        File outputFile = new File(outputFilePath);

        CheapSoundFile cheapSoundFile;
        try {
            cheapSoundFile = CheapSoundFile.create(inputFile);
            if(cheapSoundFile != null){
                int startFrame ;
                int frameNum;
                if (isFromStart){
                    startFrame = CheapSoundFile.secondsToFrames(cheapSoundFile,0);
                    frameNum = CheapSoundFile.secondsToFrames(cheapSoundFile,timeLen)  - startFrame;
                } else {
                    startFrame = cheapSoundFile.getNumFrames() - 1 - CheapSoundFile.secondsToFrames(cheapSoundFile,timeLen);
                    frameNum = CheapSoundFile.secondsToFrames(cheapSoundFile,timeLen);
                }
                cheapSoundFile.WriteFile(outputFile,startFrame,frameNum);
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param inputFilePath
     * @param outputFilePath
     * @param isFromStart true:从开裁剪；false：从中间裁剪到尾
     * @param timeLen 丢掉的长度
     * @return
     */
    public static boolean clip2(String inputFilePath,String outputFilePath ,boolean isFromStart,double timeLen){
        if(inputFilePath == null){
            Log.i(TAG,"input file is null");
            return false;
        }

        File inputFile = new File(inputFilePath);
        if(!inputFile.exists()){
            Log.i(TAG,"input file is null");
        }

        File outputFile = new File(outputFilePath);

        CheapSoundFile cheapSoundFile;
        try {
            cheapSoundFile = CheapSoundFile.create(inputFile);
            if(cheapSoundFile != null){
                int startFrame ;
                int frameNum;
                if (isFromStart){
                    startFrame = CheapSoundFile.secondsToFrames(cheapSoundFile,0);
                    frameNum = cheapSoundFile.getNumFrames() - 1 - CheapSoundFile.secondsToFrames(cheapSoundFile,timeLen);
                } else {
                    startFrame = CheapSoundFile.secondsToFrames(cheapSoundFile,timeLen);
                    frameNum = cheapSoundFile.getNumFrames() - 1 - CheapSoundFile.secondsToFrames(cheapSoundFile,timeLen);
                }
                cheapSoundFile.WriteFile(outputFile,startFrame,frameNum);
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 去头去尾裁剪
     * @param inputFilePath
     * @param outputFilePath
     * @param discardHeadTime
     * @param discardTailTime
     * @return
     */
    public static boolean clip2(String inputFilePath,String outputFilePath ,double discardHeadTime,double discardTailTime){
        if(inputFilePath == null){
            Log.i(TAG,"input file is null");
            return false;
        }

        File inputFile = new File(inputFilePath);
        if(!inputFile.exists()){
            Log.i(TAG,"input file is null");
        }

        File outputFile = new File(outputFilePath);

        CheapSoundFile cheapSoundFile;
        try {
            cheapSoundFile = CheapSoundFile.create(inputFile);
            if(cheapSoundFile != null){
                int startFrame ;
                int frameNum;
                startFrame = CheapSoundFile.secondsToFrames(cheapSoundFile, discardTailTime);
                frameNum = cheapSoundFile.getNumFrames() - 1 -
                        CheapSoundFile.secondsToFrames(cheapSoundFile,discardHeadTime) -
                        CheapSoundFile.secondsToFrames(cheapSoundFile,discardHeadTime);
                cheapSoundFile.WriteFile(outputFile,startFrame,frameNum);
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
