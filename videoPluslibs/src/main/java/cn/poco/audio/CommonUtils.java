package cn.poco.audio;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.poco.audio.soundclip.WavClip;
import cn.poco.video.NativeUtils;

/**
 * Created by menghd on 2017/6/26 0026.
 */

public class CommonUtils {
    /**
     * 解码音频文件(文件名必须包含正确的后缀)  WAV MP3 AAC
     *
     * @param inputFile
     * @param outputFile
     * @return
     */
    public static boolean getAudioPcm(String inputFile, String outputFile) {
        String[] fomponents = inputFile.toLowerCase().split("\\.");
        if (fomponents.length < 2) {
            MyLog.e(CommonUtils.class, "input file no suffix" + MyLog.getLineNumber());
            return false;
        }

        int result = -1;

        if (fomponents[fomponents.length - 1].equals("aac")) {
            result = AacEnDecoder.decodeAAC1(inputFile, outputFile);
        }
        if (fomponents[fomponents.length - 1].equals("wav")) {
            result = PcmWav.wavToPcm(inputFile, outputFile);
        }
        if (fomponents[fomponents.length - 1].equals("mp3")) {
            result = NativeUtils.getAudioPCM(inputFile, outputFile);//MP3DeEncode.decode(inputFile, outputFile);
        }

        return result > -1;
    }

    /**
     * 将音频文件转成WAV格式(文件名必须包含正确的后缀)  WAV MP3 AAC
     *
     * @param inputFile  WAV MP3 AAC格式
     * @param outputFile WAV格式
     * @return
     */
    public static boolean audioToWav(String inputFile, String outputFile) {
        String[] fomponents = inputFile.toLowerCase().split("\\.");
        if (fomponents.length < 2) {
            MyLog.e(CommonUtils.class, "input file no suffix" + MyLog.getLineNumber());
            return false;
        }

        if (fomponents[fomponents.length - 1].equals("aac")) {
            int result = AudioUtils.aacToWav(inputFile, outputFile);
            return result > -1;
        }

        if (fomponents[fomponents.length - 1].equals("mp3")) {
            return AudioUtils.mp3ToWav(inputFile, outputFile);
        }

        if (fomponents[fomponents.length - 1].equals("wav")) {
            return FileUtils.copyFile(inputFile, outputFile, true);
        }

        return false;
    }

    /**
     * 将音频文件转成WAV格式(文件名必须包含正确的后缀)  WAV MP3 AAC
     *
     * @param inputFile        WAV MP3 AAC格式
     * @param outputFile       WAV格式
     * @param targetSampleRate 输出的采样率
     * @param targetChannels   输出的声道数
     * @return
     */
    public static boolean audioToWav(String inputFile, String outputFile, int targetSampleRate, int targetChannels) {
        String[] fomponents = inputFile.toLowerCase().split("\\.");
        if (fomponents.length < 2) {
            MyLog.e(CommonUtils.class, "input file no suffix" + MyLog.getLineNumber());
            return false;
        }
        int sampleRate = getAudioSampleRate(inputFile);
        int channels = getAudioChannels(inputFile);

        if (sampleRate < 1 || channels < 1) {
            MyLog.e(CommonUtils.class, "input file is bad" + MyLog.getLineNumber());
            return false;
        }
        String tempPath = AudioConfig.getTempFolderPath();
        String tempInPcm = tempPath + UUID.randomUUID() + ".pcm";
        String tempOutPcm = tempPath + UUID.randomUUID() + ".pcm";
        int result = -1;
        if (fomponents[fomponents.length - 1].equals("aac")) {
            result = AacEnDecoder.decodeAAC1(inputFile, tempInPcm);
        }

        if (fomponents[fomponents.length - 1].equals("mp3")) {
            result = NativeUtils.getAudioPCM(inputFile, tempInPcm);//MP3DeEncode.decode(inputFile, tempInPcm);
        }

        if (fomponents[fomponents.length - 1].equals("wav")) {
            if (sampleRate == targetSampleRate && channels == targetChannels) {
                return FileUtils.copyFile(inputFile, outputFile, true);
            }
            result = PcmWav.wavToPcm(inputFile, tempInPcm);
        }

        if (result < 0) {
            MyLog.e(CommonUtils.class, "input file decode fail" + MyLog.getLineNumber());
            return false;
        }
        boolean ret = Resample.reSamplePateChannels(tempInPcm, tempOutPcm, sampleRate, targetSampleRate, channels, targetChannels);
        if (!ret) {
            return false;
        }
        ret = PcmToWav.pcmToWav(tempOutPcm, outputFile, targetSampleRate, targetChannels);
        return ret;
    }

    public static boolean wavToAudio(String inputFile, String outputFile) {
        String[] fomponents = outputFile.toLowerCase().split("\\.");
        if (fomponents.length < 2) {
            MyLog.e(CommonUtils.class, "out file no suffix" + MyLog.getLineNumber());
            return false;
        }
        int result = -1;

        if (fomponents[fomponents.length - 1].equals("aac")) {
            result = AudioUtils.wavToAac(inputFile, outputFile);
        }

        if (fomponents[fomponents.length - 1].equals("mp3")) {
            return AudioUtils.wavToMp3(inputFile, outputFile);
        }

        if (fomponents[fomponents.length - 1].equals("wav")) {
            return FileUtils.copyFile(inputFile, outputFile, true);
        }

        return result > -1;
    }

    public static int getAudioSampleRate(String inputFile) {
        String[] fomponents = inputFile.toLowerCase().split("\\.");
        if (fomponents.length < 2) {
            MyLog.e(CommonUtils.class, "input file no suffix");
            return 0;
        }

        int result = -1;

        if (fomponents[fomponents.length - 1].equals("aac")) {
            result = (int) AacEnDecoder.getSamplerate(inputFile);
        }
        if (fomponents[fomponents.length - 1].equals("wav")) {
            int[] ret = SoundJoint.getWavHead(inputFile);
            if (ret != null && ret.length > 0) {
                result = ret[0];
            } else {
                return 0;
            }
        }
        if (fomponents[fomponents.length - 1].equals("mp3")) {
            result = MP3DeEncode.getSamplerate(inputFile);
        }

        return result;
    }

    public static int getAudioChannels(String inputFile) {
        String[] fomponents = inputFile.toLowerCase().split("\\.");
        if (fomponents.length < 2) {
            MyLog.e(CommonUtils.class, "input file no suffix");
            return 0;
        }

        int result = -1;

        if (fomponents[fomponents.length - 1].equals("aac")) {
            result = AacEnDecoder.getChannels(inputFile);
        }
        if (fomponents[fomponents.length - 1].equals("wav")) {
            int[] ret = SoundJoint.getWavHead(inputFile);
            if (ret != null && ret.length > 1) {
                result = ret[1];
            } else {
                return 0;
            }
        }
        if (fomponents[fomponents.length - 1].equals("mp3")) {
            //result = MP3DeEncode.getChannels(inputFile);
            result = NativeUtils.getAudioChannels(inputFile);
        }

        return result;
    }

    /**
     * 编码音频文件(文件名必须包含正确的后缀)  WAV MP3 AAC
     *
     * @param inputFile
     * @param outputFile
     * @param sampleRate
     * @param channels
     * @return
     */
    public static boolean encodeAudio(String inputFile, String outputFile, int sampleRate, int channels) {
        String[] fomponents = outputFile.toLowerCase().split("\\.");
        if (fomponents.length < 2) {
            MyLog.e(CommonUtils.class, "input file no suffix" + MyLog.getLineNumber());
            return false;
        }

        int result = -1;

        if (fomponents[fomponents.length - 1].equals("aac")) {
            result = AacEnDecoder.encodeAAC(sampleRate, channels, 16, inputFile, outputFile);
        }
        if (fomponents[fomponents.length - 1].equals("wav")) {
            return PcmToWav.pcmToWav(inputFile, outputFile, sampleRate, channels);
        }
        if (fomponents[fomponents.length - 1].equals("mp3")) {
            result = MP3DeEncode.encode(inputFile, outputFile, sampleRate, channels);
        }

        return result > -1;

    }

    /**
     * AAC音频文件剪切(wav介质)
     *
     * @param inputAacFilePath
     * @param outputAacFileList
     * @param timestamps
     * @return
     */
    public static boolean clipAacToAac(String inputAacFilePath, List<String> outputAacFileList, List<double[]> timestamps) {
        boolean result;
        if (outputAacFileList.size() != timestamps.size()) {
            return false;
        }

        int blockNum = outputAacFileList.size();

        String tempWavFilePath = inputAacFilePath + "-TEMPWAV";

        int aacWavResult = AudioUtils.aacToWav(inputAacFilePath, tempWavFilePath);
        if (aacWavResult < 0) {
            return false;
        }

        List<String> tempWavFileList = new ArrayList<String>();
        for (int i = 0; i < blockNum; i++) {
            tempWavFileList.add(outputAacFileList.get(i) + "-TEMPWAV");
        }

        result = WavClip.clip(tempWavFilePath, tempWavFileList, timestamps);

        for (int i = 0; i < blockNum; i++) {
            AudioUtils.wavToAac(tempWavFileList.get(i), outputAacFileList.get(i));
            FileUtils.deleteFile(tempWavFileList.get(i));
        }

        FileUtils.deleteFile(tempWavFilePath);

        return result;
    }

    /**
     * Wav音频文件剪切
     *
     * @param inputWavFilePath
     * @param outputWavFileList
     * @param timestamps
     * @return
     */
    public static boolean clipWavToWav(String inputWavFilePath, List<String> outputWavFileList, List<double[]> timestamps) {
        return WavClip.clip(inputWavFilePath, outputWavFileList, timestamps);
    }


    /**
     * 获取pcm音频时长
     *
     * @param sample_rate
     * @param fileSize
     * @param bit
     * @param channel
     * @return
     */
    public static double getPcmDuration(long sample_rate, long fileSize, int bit, int channel) {
        if (bit != 16 && bit != 8) {
            return -1;
        }
        return (fileSize * 1f) / (sample_rate * (bit / 8) * channel);
    }

    /**
     * AAC片段拼接成一段音频（wav做中间介质）
     *
     * @param outputFilePath
     * @param inputFileList
     * @return
     */
    //wav
    public static boolean jointAacAuidoSegmentToAac(String outputFilePath, List<String> inputFileList) {
        boolean result = false;

        int blockNum = inputFileList.size();

        List<String> tempWavFileList = new ArrayList<String>();
        for (int i = 0; i < blockNum; i++) {
            String tempWav = inputFileList.get(i) + "-TEMPWAV";
            tempWavFileList.add(tempWav);
            int exResult = AudioUtils.aacToWav(inputFileList.get(i), tempWav);
            if (exResult < 0) {
                return false;
            }
        }


        long startTime = System.currentTimeMillis();
        String outTempWavFile = outputFilePath + "-TEMPWAV";
        for (int i = 0; i < blockNum - 1; i++) {
            if (i == 0) {
                SoundJoint.joint(tempWavFileList.get(0), tempWavFileList.get(1), outTempWavFile);
                FileUtils.deleteFile(tempWavFileList.get(0));
                FileUtils.deleteFile(tempWavFileList.get(1));
            } else {
                SoundJoint.joint(outTempWavFile, tempWavFileList.get(i + 1), outTempWavFile);
                FileUtils.deleteFile(tempWavFileList.get(i + 1));
            }
        }
        Log.i("time wav joint", (System.currentTimeMillis() - startTime) / 1000f + "");

        int exResult = AudioUtils.wavToAac(outTempWavFile, outputFilePath);
        if (exResult < 0) {
            FileUtils.deleteFile(outTempWavFile);
            return false;
        }
        FileUtils.deleteFile(outTempWavFile);
        return result;
    }

    /**
     * AAC片段拼接成一段音频（wav做中间介质）
     *
     * @param outputFilePath
     * @param inputFileList
     * @param afadeTime
     * @return
     */
    public static boolean jointAacAuidoSegmentToAac(String outputFilePath, List<String> inputFileList, double afadeTime) {
        boolean result;

        int blockNum = inputFileList.size();

        //aac to pcm,and do afade
        List<String> tempAfadePcmFileList = new ArrayList<String>();
        for (int i = 0; i < blockNum; i++) {
            String tempPcm = inputFileList.get(i) + "-TEMP-PCM";
            String tempPcmAfade = inputFileList.get(i) + "-TEMP-PCM-AFADE";

            int ex = AacEnDecoder.decodeAAC1(inputFileList.get(i), tempPcm);
            tempAfadePcmFileList.add(tempPcmAfade);
            if (ex < 0) {
                FileUtils.deleteFile(tempAfadePcmFileList);
                return false;
            }

            //do afade
            if (i == 0) {
                ex = PcmAfade.afadeout(
                        tempPcm,
                        tempPcmAfade,
                        afadeTime,
                        AacEnDecoder.getSamplerate(inputFileList.get(i)),
                        16,
                        AacEnDecoder.getChannels(inputFileList.get(i)));
            } else if (i == blockNum - 1) {
                ex = PcmAfade.afadein(
                        tempPcm,
                        tempPcmAfade,
                        afadeTime,
                        AacEnDecoder.getSamplerate(inputFileList.get(i)),
                        16,
                        AacEnDecoder.getChannels(inputFileList.get(i)));
            } else {
                String tempIn = tempPcm + "AFADEIN";
                ex = PcmAfade.afadein(
                        tempPcm,
                        tempIn,
                        afadeTime,
                        AacEnDecoder.getSamplerate(inputFileList.get(i)),
                        16,
                        AacEnDecoder.getChannels(inputFileList.get(i)));
                ex = PcmAfade.afadeout(
                        tempIn,
                        tempPcmAfade,
                        afadeTime,
                        AacEnDecoder.getSamplerate(inputFileList.get(i)),
                        16,
                        AacEnDecoder.getChannels(inputFileList.get(i)));
                FileUtils.deleteFile(tempPcm);
                FileUtils.deleteFile(tempIn);
            }
            if (ex < 0) {
                FileUtils.deleteFile(tempAfadePcmFileList);
                FileUtils.deleteFile(tempPcmAfade);
                return false;
            }
        }

        //pcm to wav
        long startTime = System.currentTimeMillis();
        List<String> tempWavFileList = new ArrayList<String>();
        for (int i = 0; i < blockNum; i++) {
            String tempWav = tempAfadePcmFileList.get(i) + "-TEMPWAV";
            tempWavFileList.add(tempWav);
            boolean exResult = PcmToWav.pcmToWav(
                    tempAfadePcmFileList.get(i),
                    tempWav,
                    AacEnDecoder.getSamplerate(inputFileList.get(i)),
                    AacEnDecoder.getChannels(inputFileList.get(i)));
            if (!exResult) {
                FileUtils.deleteFile(tempAfadePcmFileList);
                FileUtils.deleteFile(tempWavFileList);
                return false;
            }
        }
        Log.i("time pcm to wav", (System.currentTimeMillis() - startTime) / 1000f + "");


        startTime = System.currentTimeMillis();
        String outTempWavFile = outputFilePath + "-TEMPWAV";
        for (int i = 0; i < blockNum - 1; i++) {
            if (i == 0) {
                SoundJoint.joint(tempWavFileList.get(0), tempWavFileList.get(1), outTempWavFile);
                FileUtils.deleteFile(tempWavFileList.get(0));
                FileUtils.deleteFile(tempWavFileList.get(1));
            } else {
                SoundJoint.joint(outTempWavFile, tempWavFileList.get(i + 1), outTempWavFile);
                FileUtils.deleteFile(tempWavFileList.get(i + 1));
            }
        }
        Log.i("time wav joint", (System.currentTimeMillis() - startTime) / 1000f + "");

        int exResult = AudioUtils.wavToAac(outTempWavFile, outputFilePath);
        if (exResult < 0) {
            FileUtils.deleteFile(outTempWavFile);
            return false;
        }
        FileUtils.deleteFile(outTempWavFile);
        return true;
    }

    /**
     * WAV片段拼接成一段音频
     *
     * @param outputFilePath
     * @param inputFileList
     * @return
     */
    //wav
    public static boolean joinWavAudioSegmentToWav(String outputFilePath, List<String> inputFileList) {
        int blockNum = inputFileList.size();

        long startTime = System.currentTimeMillis();
        String outTempWavFile = outputFilePath + "-TEMPWAV";
        for (int i = 0; i < blockNum - 1; i++) {
            if (i == 0) {
                SoundJoint.joint(inputFileList.get(0), inputFileList.get(1), outTempWavFile);
                FileUtils.deleteFile(inputFileList.get(0));
                FileUtils.deleteFile(inputFileList.get(1));
            } else {
                SoundJoint.joint(outTempWavFile, inputFileList.get(i + 1), outTempWavFile);
                FileUtils.deleteFile(inputFileList.get(i + 1));
            }
        }
        Log.i("time wav joint", (System.currentTimeMillis() - startTime) / 1000f + "");

        boolean exResult = FileUtils.copyFile(outTempWavFile, outputFilePath, true);
        if (exResult) {
            FileUtils.deleteFile(outTempWavFile);
            return true;
        }
        FileUtils.deleteFile(outTempWavFile);
        return false;
    }


    /**
     * AAC片段拼接成一段音频（wav做中间介质,淡入淡出交叉）
     *
     * @param outputFilePath
     * @param inputFileList
     * @param afadeTime
     * @return
     */
    public static boolean jointAacAuidoSegmentToAacCross(String outputFilePath, List<String> inputFileList, double afadeTime) {
        long startTime;
        boolean result;

        int blockNum = inputFileList.size();

        String tempPath = AudioConfig.getTempFolderPath();

        //aac to pcm,and do afade
        startTime = System.currentTimeMillis();
        List<String> tempAfadePcmFileList = new ArrayList<String>();
        for (int i = 0; i < blockNum; i++) {
            String tempPcm = tempPath + UUID.randomUUID() + ".pcm";
            String tempPcmAfade = tempPath + UUID.randomUUID() + ".pcm";

            int ex = AacEnDecoder.decodeAAC1(inputFileList.get(i), tempPcm);
            tempAfadePcmFileList.add(tempPcmAfade);
            if (ex < 0) {
                FileUtils.deleteFile(tempPcm);
                FileUtils.deleteFile(tempAfadePcmFileList);
                return false;
            }

            //do afade
            if (i == 0) {
                ex = PcmAfade.afadeout(
                        tempPcm,
                        tempPcmAfade,
                        afadeTime,
                        AacEnDecoder.getSamplerate(inputFileList.get(i)),
                        16,
                        AacEnDecoder.getChannels(inputFileList.get(i)));
            } else if (i == blockNum - 1) {
                ex = PcmAfade.afadein(
                        tempPcm,
                        tempPcmAfade,
                        afadeTime,
                        AacEnDecoder.getSamplerate(inputFileList.get(i)),
                        16,
                        AacEnDecoder.getChannels(inputFileList.get(i)));
            } else {
                String tempIn = tempPath + UUID.randomUUID() + ".pcm";
                ex = PcmAfade.afadein(
                        tempPcm,
                        tempIn,
                        afadeTime,
                        AacEnDecoder.getSamplerate(inputFileList.get(i)),
                        16,
                        AacEnDecoder.getChannels(inputFileList.get(i)));
                ex = PcmAfade.afadeout(
                        tempIn,
                        tempPcmAfade,
                        afadeTime,
                        AacEnDecoder.getSamplerate(inputFileList.get(i)),
                        16,
                        AacEnDecoder.getChannels(inputFileList.get(i)));
                FileUtils.deleteFile(tempIn);
            }
            FileUtils.deleteFile(tempPcm);
            if (ex < 0) {
                FileUtils.deleteFile(tempAfadePcmFileList);
                return false;
            }
        }
        MyLog.i(CommonUtils.class, "aac to pcm,and do afade:" + (System.currentTimeMillis() - startTime) / 1000f + "s");


        //pcm to wav
        startTime = System.currentTimeMillis();
        List<String> tempWavFileList = new ArrayList<String>();
        for (int i = 0; i < blockNum; i++) {
            String tempWav = tempPath + UUID.randomUUID() + ".wav";
            tempWavFileList.add(tempWav);
            boolean exResult = PcmToWav.pcmToWav(
                    tempAfadePcmFileList.get(i),
                    tempWav,
                    AacEnDecoder.getSamplerate(inputFileList.get(i)),
                    AacEnDecoder.getChannels(inputFileList.get(i)));
            if (!exResult) {
                FileUtils.deleteFile(tempAfadePcmFileList);
                FileUtils.deleteFile(tempWavFileList);
                return false;
            }
        }
        FileUtils.deleteFile(tempAfadePcmFileList);
        MyLog.i(CommonUtils.class, "pcm to wav:" + (System.currentTimeMillis() - startTime) / 1000f + "s");

        //get uncross segment
        startTime = System.currentTimeMillis();
        List<String> corssSegmentWavList = new ArrayList<String>();
        for (int i = 0; i < tempWavFileList.size(); i++) {
            if (i == 0) {
                String tempCross = tempPath + UUID.randomUUID() + ".wav";
                WavClip.clip(tempWavFileList.get(i), tempCross, false, afadeTime);
                corssSegmentWavList.add(tempCross);
            } else if (i == tempWavFileList.size() - 1) {
                String tempCross = tempPath + UUID.randomUUID() + ".wav";
                WavClip.clip(tempWavFileList.get(i), tempCross, true, afadeTime);
                corssSegmentWavList.add(tempCross);
            } else {
                String tempCrossHead = tempPath + UUID.randomUUID() + ".wav";
                String tempCrossTail = tempPath + UUID.randomUUID() + ".wav";
                WavClip.clip(tempWavFileList.get(i), tempCrossHead, true, afadeTime);
                WavClip.clip(tempWavFileList.get(i), tempCrossTail, false, afadeTime);
                corssSegmentWavList.add(tempCrossHead);
                corssSegmentWavList.add(tempCrossTail);
            }
        }
        MyLog.i(CommonUtils.class, "get uncross segment:" + (System.currentTimeMillis() - startTime) / 1000f + "s");

        // get no need corss segment
        startTime = System.currentTimeMillis();
        List<String> noCorssSegmentWavList = new ArrayList<String>();
        for (int i = 0; i < tempWavFileList.size(); i++) {
            String tempNoCross = tempPath + UUID.randomUUID() + ".wav";
            if (i == 0) {
                WavClip.clip2(tempWavFileList.get(i), tempNoCross, true, afadeTime);
            } else if (i == tempWavFileList.size() - 1) {
                WavClip.clip2(tempWavFileList.get(i), tempNoCross, false, afadeTime);
            } else {
                WavClip.clip2(tempWavFileList.get(i), tempNoCross, afadeTime, afadeTime);
            }
            noCorssSegmentWavList.add(tempNoCross);
        }
        FileUtils.deleteFile(tempWavFileList);
        MyLog.i(CommonUtils.class, "get no need corss segment:" + (System.currentTimeMillis() - startTime) / 1000f + "s");

        if (corssSegmentWavList.size() % 2 != 0) {
            FileUtils.deleteFile(corssSegmentWavList);
            return false;
        }

        // mix cross segment
        startTime = System.currentTimeMillis();
        List<String> mixSegmentWavList = new ArrayList<String>();
        for (int i = 0; i < corssSegmentWavList.size(); i += 2) {
            String tempMix = tempPath + UUID.randomUUID() + ".wav";
            WavMix.mix(corssSegmentWavList.get(i), corssSegmentWavList.get(i + 1), tempMix, 0, 1);
            mixSegmentWavList.add(tempMix);
        }
        FileUtils.deleteFile(corssSegmentWavList);
        MyLog.i(CommonUtils.class, "mix cross segment:" + (System.currentTimeMillis() - startTime) / 1000f + "s");

        //joint no-cross and cross-mix segment
        startTime = System.currentTimeMillis();
        for (int i = 0; i < mixSegmentWavList.size(); i++) {
            noCorssSegmentWavList.add(1 + i * 2, mixSegmentWavList.get(i));
        }
        MyLog.i(CommonUtils.class, "joint no-cross and cross-mix segment:" + (System.currentTimeMillis() - startTime) / 1000f + "s");


        startTime = System.currentTimeMillis();
        String outTempWavFile = tempPath + UUID.randomUUID() + ".wav";
        for (int i = 0; i < noCorssSegmentWavList.size() - 1; i++) {
            if (i == 0) {
                SoundJoint.joint(noCorssSegmentWavList.get(0), noCorssSegmentWavList.get(1), outTempWavFile);
            } else {
                SoundJoint.joint(outTempWavFile, noCorssSegmentWavList.get(i + 1), outTempWavFile);
            }
        }
        FileUtils.deleteFile(noCorssSegmentWavList);
        MyLog.i(CommonUtils.class, "wav joint: " + (System.currentTimeMillis() - startTime) / 1000f + "s");

        //wav to aac
        startTime = System.currentTimeMillis();
        int exResult = AudioUtils.wavToAac(outTempWavFile, outputFilePath);
        FileUtils.deleteFile(outTempWavFile);
        if (exResult < 0) {
            return false;
        }
        MyLog.i(CommonUtils.class, "wav to aac: " + (System.currentTimeMillis() - startTime) / 1000f + "s");
        return true;
    }


    /**
     * WAV片段拼接成一段音频（淡入淡出交叉）
     *
     * @param outputFilePath
     * @param inputFileList
     * @param afadeTime
     * @return
     */
    public static boolean joinWavAuidoSegmentToWavCross(String outputFilePath, List<String> inputFileList, double afadeTime) {
        long startTime;
        int result;
        boolean resultB;

        int blockNum = inputFileList.size();

        String tempPath = AudioConfig.getTempFolderPath();

        //获取准备用来做渐变交融部分
        startTime = System.currentTimeMillis();
        List<String> corssSegmentWavList = new ArrayList<String>();
        for (int i = 0; i < inputFileList.size(); i++) {
            if (i == 0) {
                String tempCross = tempPath + UUID.randomUUID() + ".wav";
                WavClip.clip(inputFileList.get(i), tempCross, false, afadeTime);
                corssSegmentWavList.add(tempCross);
            } else if (i == inputFileList.size() - 1) {
                String tempCross = tempPath + UUID.randomUUID() + ".wav";
                WavClip.clip(inputFileList.get(i), tempCross, true, afadeTime);
                corssSegmentWavList.add(tempCross);
            } else {
                String tempCrossHead = tempPath + UUID.randomUUID() + ".wav";
                String tempCrossTail = tempPath + UUID.randomUUID() + ".wav";
                WavClip.clip(inputFileList.get(i), tempCrossHead, true, afadeTime);
                WavClip.clip(inputFileList.get(i), tempCrossTail, false, afadeTime);
                corssSegmentWavList.add(tempCrossHead);
                corssSegmentWavList.add(tempCrossTail);
            }
        }
        MyLog.i(CommonUtils.class, "get going to cross segment:" + (System.currentTimeMillis() - startTime) / 1000f + "s");


        // get no need corss segment
        startTime = System.currentTimeMillis();
        List<String> noCorssSegmentWavList = new ArrayList<String>();
        for (int i = 0; i < inputFileList.size(); i++) {
            String tempNoCross = tempPath + UUID.randomUUID() + ".wav";
            if (i == 0) {
                WavClip.clip2(inputFileList.get(i), tempNoCross, true, afadeTime);
            } else if (i == inputFileList.size() - 1) {
                WavClip.clip2(inputFileList.get(i), tempNoCross, false, afadeTime);
            } else {
                WavClip.clip2(inputFileList.get(i), tempNoCross, afadeTime, afadeTime);
            }
            noCorssSegmentWavList.add(tempNoCross);
        }
        MyLog.i(CommonUtils.class, "get no need corss segment:" + (System.currentTimeMillis() - startTime) / 1000f + "s");

        if (corssSegmentWavList.size() % 2 != 0) {
            FileUtils.deleteFile(corssSegmentWavList);
            return false;
        }

        //给corss加fade特效
        List<String> fadeCrossSegmentWavList = new ArrayList<String>();
        for (int i = 0; i < corssSegmentWavList.size(); i++) {
            String tempPcm = tempPath + UUID.randomUUID() + ".pcm";
            String tempPcmAfade = tempPath + UUID.randomUUID() + ".pcm";
            String tempWavAfade = tempPath + UUID.randomUUID() + ".wav";
            PcmWav.wavToPcm(corssSegmentWavList.get(i), tempPcm);

            //do afade
            int[] wavHead = SoundJoint.getWavHead(corssSegmentWavList.get(i));
            long sampleRate = wavHead[0] > 0 ? wavHead[0] : 44100;
            int channels = wavHead[1] > 0 ? wavHead[1] : 2;
            if (i % 2 == 0) {
                result = PcmAfade.afadeout(tempPcm, tempPcmAfade, afadeTime, sampleRate, 16, channels);
            } else {
                result = PcmAfade.afadein(tempPcm, tempPcmAfade, afadeTime, sampleRate, 16, channels);
            }
            FileUtils.deleteFile(tempPcm);
            if (result < 0) {
                FileUtils.deleteFile(fadeCrossSegmentWavList);
                FileUtils.deleteFile(corssSegmentWavList);
                return false;
            }

            resultB = PcmToWav.pcmToWav(tempPcmAfade, tempWavAfade, sampleRate, channels);
            if (!resultB) {
                FileUtils.deleteFile(tempPcm);
                FileUtils.deleteFile(tempPcmAfade);
                FileUtils.deleteFile(tempWavAfade);
                FileUtils.deleteFile(fadeCrossSegmentWavList);
                FileUtils.deleteFile(corssSegmentWavList);
                return false;
            }

            fadeCrossSegmentWavList.add(tempWavAfade);
        }
        FileUtils.deleteFile(corssSegmentWavList);


        // mix cross segment
        startTime = System.currentTimeMillis();
        List<String> mixSegmentWavList = new ArrayList<String>();
        for (int i = 0; i < fadeCrossSegmentWavList.size(); i += 2) {
            String tempMix = tempPath + UUID.randomUUID() + ".wav";
            WavMix.mix(fadeCrossSegmentWavList.get(i), fadeCrossSegmentWavList.get(i + 1), tempMix, 0, 1);
            mixSegmentWavList.add(tempMix);
        }
        FileUtils.deleteFile(fadeCrossSegmentWavList);
        MyLog.i(CommonUtils.class, "mix cross segment:" + (System.currentTimeMillis() - startTime) / 1000f + "s");


        //joint no-cross and cross-mix segment
        startTime = System.currentTimeMillis();
        for (int i = 0; i < mixSegmentWavList.size(); i++) {
            noCorssSegmentWavList.add(1 + i * 2, mixSegmentWavList.get(i));
        }
        MyLog.i(CommonUtils.class, "joint no-cross and cross-mix segment:" + (System.currentTimeMillis() - startTime) / 1000f + "s");


        startTime = System.currentTimeMillis();
        String outTempWavFile = tempPath + UUID.randomUUID() + ".wav";
        for (int i = 0; i < noCorssSegmentWavList.size() - 1; i++) {
            if (i == 0) {
                SoundJoint.joint(noCorssSegmentWavList.get(0), noCorssSegmentWavList.get(1), outTempWavFile);
            } else if (i == noCorssSegmentWavList.size() - 2) {
                SoundJoint.joint(outTempWavFile, noCorssSegmentWavList.get(i + 1), outputFilePath);
            } else {
                String temp = tempPath + UUID.randomUUID() + ".wav";
                SoundJoint.joint(outTempWavFile, noCorssSegmentWavList.get(i + 1), temp);
                FileUtils.deleteFile(outTempWavFile);
                outTempWavFile = temp;
            }
        }
        FileUtils.deleteFile(noCorssSegmentWavList);
        MyLog.i(CommonUtils.class, "wav joint: " + (System.currentTimeMillis() - startTime) / 1000f + "s");
        return true;
    }

}
