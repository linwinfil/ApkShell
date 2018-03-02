package cn.poco.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cn.poco.audio.soundclip.WavClip;
import cn.poco.video.NativeUtils;

/**
 * Created by menghd on 2017/3/1
 * 音频工具类
 * 使用前要先用AudioConfig设置缓存目录，执行完每个方法后，要清空缓存目录下的缓存问文件
 */

public class AudioUtils {

    public static String getRandomFilePath(String suffix) {
        return AudioConfig.getTempFolderPath() + UUID.randomUUID() + "." + suffix;
    }

    /**
     * aac转wav
     */
    public static int aacToWav(String inputFilePath, String outputFilePath) {
        int result;
        String inputTempPCM = inputFilePath + "-TEMPPCM";
        result = AacEnDecoder.decodeAAC1(inputFilePath, inputTempPCM);
        if (result < 0) {
            FileUtils.deleteFile(inputTempPCM);
            return result;
        }
        long samplerate = AacEnDecoder.getSamplerate(inputFilePath);
        int channels = AacEnDecoder.getChannels(inputFilePath);

        if (samplerate < 0) {
            FileUtils.deleteFile(inputTempPCM);
            return -1;
        }
        boolean resultPW = PcmToWav.pcmToWav(inputTempPCM, outputFilePath, samplerate, channels);
        if (!resultPW) {
            FileUtils.deleteFile(inputTempPCM);
            FileUtils.deleteFile(outputFilePath);
            return result;
        }

        FileUtils.deleteFile(inputTempPCM);
        return result;
    }

    /**
     * wav转aac
     */
    public static int wavToAac(String inputFilePath, String outputFilePath) {
        int result = -1;
        String inputTempPCM = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".pcm";

        result = PcmWav.wavToPcm(inputFilePath, inputTempPCM);
        if (result < 0) {
            FileUtils.deleteFile(inputTempPCM);
            return result;
        }
        int[] wavInfo = SoundJoint.getWavHead(inputFilePath);
        if (wavInfo == null || wavInfo.length == 0) {
            return -1;
        }
        long samplerate = wavInfo[0];
        int channels = wavInfo[1];
        int bit = wavInfo[2];
        if (samplerate < 0 || channels < 0) {
            FileUtils.deleteFile(inputTempPCM);
            return -1;
        }

        //// TODO: 2017/3/2 0002  channels
        result = AacEnDecoder.encodeAAC(samplerate, channels, bit, inputTempPCM, outputFilePath);
        FileUtils.deleteFile(inputTempPCM);
        if (result < 0) {
            FileUtils.deleteFile(outputFilePath);
            return result;
        }

        return result;
    }

    public static boolean wavToMp3(String inputFilePath, String outputFilePath) {
        int[] head = SoundJoint.getWavHead(inputFilePath);
        if (head == null || head.length < 2) {
            MyLog.e(AudioUtils.class, "input wav file is bad" + MyLog.getLineNumber());
            return false;
        }
        int sampleRate = head[0];
        int channels = head[1];
        if (sampleRate < 1 || channels < 1) {
            MyLog.e(AudioUtils.class, "input wav file is bad" + MyLog.getLineNumber());
            return false;
        }
        int result = -1;
        String tempPcm = AudioUtils.getRandomFilePath("pcm");
        result = PcmWav.wavToPcm(inputFilePath, tempPcm);
        if (result < 0) {
            MyLog.e(AudioUtils.class, "input wav file is bad" + MyLog.getLineNumber());
            return false;
        }
        result = MP3DeEncode.encode(tempPcm, outputFilePath, sampleRate, channels);
        if (result < 0) {
            MyLog.e(AudioUtils.class, "encode mp3 fail" + MyLog.getLineNumber());
            return false;
        }
        return true;
    }

    public static boolean mp3ToWav(String inputFilePath, String outputFilePath) {
        int sampleRate = MP3DeEncode.getSamplerate(inputFilePath);
        int channels = NativeUtils.getAudioChannels(inputFilePath);//MP3DeEncode.getChannels(inputFilePath);
        if (sampleRate < 1 || channels < 1) {
            MyLog.e(AudioUtils.class, "input mp3 file is bad" + MyLog.getLineNumber());
            return false;
        }
        int result = -1;
        String tempPcm = AudioUtils.getRandomFilePath("pcm");
        result = NativeUtils.getAudioPCM(inputFilePath, tempPcm);//MP3DeEncode.decode(inputFilePath, tempPcm);
        if (result < 0) {
            MyLog.e(AudioUtils.class, "input mp3 file is bad" + MyLog.getLineNumber());
            return false;
        }
        boolean ret = PcmToWav.pcmToWav(tempPcm, outputFilePath, sampleRate, channels);
        if (!ret) {
            MyLog.e(AudioUtils.class, "encode wav fail" + MyLog.getLineNumber());
            return false;
        }
        return true;
    }


    /**
     * 音频片段拼接成一段音频（淡入淡出交叉）MP3 WAV AAC 任意格式输出输出
     *
     * @param outputFilePath
     * @param inputFileList
     * @param afadeTime      淡入淡出交叉部分时长 单位：秒
     * @return
     */
    public static boolean jointAuido(String outputFilePath, List<String> inputFileList, double afadeTime) {
        boolean resultB;

        int blockNum = inputFileList.size();

        String tempPath = AudioConfig.getTempFolderPath();
        int commonSampleRate = 44100;
        int commonChannels = 2;

        List<String> wavList = new ArrayList<String>();
        for (int i = 0; i < blockNum; i++) {
            String tempWav = tempPath + UUID.randomUUID() + ".wav";
            resultB = CommonUtils.audioToWav(inputFileList.get(i), tempWav, commonSampleRate, commonChannels);
            if (!resultB) {
                return false;
            }
            wavList.add(tempWav);
        }

        String tempOutWav = tempPath + UUID.randomUUID() + ".wav";
        resultB = CommonUtils.joinWavAuidoSegmentToWavCross(tempOutWav, wavList, afadeTime);
        if (!resultB) {
            return false;
        }

        resultB = CommonUtils.wavToAudio(tempOutWav, outputFilePath);
        if (!resultB) {
            return false;
        }
        return true;
    }

    /**
     * 音频片段拼接成一段音频MP3 WAV AAC 任意格式输出输出
     *
     * @param outputFilePath
     * @param inputFileList  必须大于1
     * @return
     */
    public static boolean jointAuido(String outputFilePath, List<String> inputFileList) {
        boolean resultB;

        int blockNum = inputFileList.size();

        String tempPath = AudioConfig.getTempFolderPath();
        int commonSampleRate = 44100;
        int commonChannels = 2;

        List<String> wavList = new ArrayList<String>();
        for (int i = 0; i < blockNum; i++) {
            String tempWav = tempPath + UUID.randomUUID() + ".wav";
            resultB = CommonUtils.audioToWav(inputFileList.get(i), tempWav, commonSampleRate, commonChannels);
            if (!resultB) {
                return false;
            }
            wavList.add(tempWav);
        }

        String tempOutWav = tempPath + UUID.randomUUID() + ".wav";
        resultB = CommonUtils.joinWavAudioSegmentToWav(tempOutWav, wavList);
        if (!resultB) {
            return false;
        }

        resultB = CommonUtils.wavToAudio(tempOutWav, outputFilePath);
        if (!resultB) {
            return false;
        }
        return true;
    }


    /**
     * PCM文件混音
     *
     * @param inputFilePath   主输入PCM文件路径
     * @param outputFilePath  输出PCM文件路径
     * @param bgMusicPathList 要在主文件上混入声音的PCM文件列表
     * @param bgMusicStartEnd bgMusicPathList的基于主音频文件混入起始位置列表（基于主音频文件百分比位置）
     */
    @Deprecated
    public static boolean mixAudio(String inputFilePath, String outputFilePath, List<String> bgMusicPathList, List<double[]> bgMusicStartEnd) {
        if (bgMusicPathList == null || bgMusicPathList.size() == 0 || bgMusicStartEnd == null
                || bgMusicStartEnd.size() == 0 || bgMusicPathList.size() != bgMusicStartEnd.size()) {
            return false;
        }
        String tempPath = AudioConfig.getTempFolderPath();

        String tempMixPcm;

        List<String> tempMixPcmList = new ArrayList<String>();

        if (bgMusicPathList.size() == 1) {
            int reslut;
            tempMixPcm = outputFilePath;
            reslut = PcmMix.mixPcm(inputFilePath, bgMusicPathList.get(0), tempMixPcm, bgMusicStartEnd.get(0)[0], bgMusicStartEnd.get(0)[1]);
            if (reslut < 0) {
                FileUtils.deleteFile(tempMixPcm);
                MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
                return false;
            }
        } else {
            for (int i = 0; i < bgMusicPathList.size(); i++) {
                tempMixPcm = tempPath + UUID.randomUUID() + ".pcm";
                int reslut;
                if (i == 0) {
                    tempMixPcmList.add(tempMixPcm);
                    reslut = PcmMix.mixPcm(inputFilePath, bgMusicPathList.get(0), tempMixPcm, bgMusicStartEnd.get(0)[0], bgMusicStartEnd.get(0)[1]);
                    if (reslut < 0) {
                        MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
                        return false;
                    }
                } else {
                    if (i == bgMusicPathList.size() - 1) {
                        tempMixPcm = outputFilePath;
                    } else {
                        tempMixPcmList.add(tempMixPcm);
                    }
                    reslut = PcmMix.mixPcm(tempMixPcmList.get(i - 1), bgMusicPathList.get(i), tempMixPcm, bgMusicStartEnd.get(i)[0], bgMusicStartEnd.get(i)[1]);// TODO: 2017/3/7 0007  后面再加入结束时间
                    if (reslut < 0) {
                        FileUtils.deleteFile(tempMixPcmList.get(i - 1));
                        MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
                        return false;
                    }

                }
            }

            FileUtils.deleteFile(tempMixPcmList);
        }
        return true;
    }

    /**
     * 音频文件混音（格式WAV MP3 AAC）
     *
     * @param inputFilePath   主音频文件路径
     * @param volume          主音频的音量
     * @param outputFilePath  输出音频文件路径
     * @param bgMusicPathList 要在主文件上混入声音的音频文件列表
     * @param bgMusicStartEnd bgMusicPathList的基于主音频文件混入起始位置列表（基于主音频文件百分比位置）
     * @param volumeList      bgMusicPathList的音量 （音量从[0,1], 1是原始音量大小，小于1则使音量变小,0为无声）
     */
    public static boolean mixAudio(String inputFilePath, double volume, String outputFilePath,
                                   List<String> bgMusicPathList, List<double[]> bgMusicStartEnd, List<Double> volumeList) {
        if (bgMusicPathList == null || bgMusicPathList.size() == 0 || bgMusicStartEnd == null
                || bgMusicStartEnd.size() == 0 || bgMusicPathList.size() != bgMusicStartEnd.size()) {
            return false;
        }

        String tempPath = AudioConfig.getTempFolderPath();

        String outPcm = tempPath + UUID.randomUUID() + ".pcm";
        int commonSampleRate;
        int commonChannels;

        String inputFilePcm = tempPath + UUID.randomUUID() + ".pcm";
        int mainFileSampleRate = CommonUtils.getAudioSampleRate(inputFilePath);
        int mianFileChannels = CommonUtils.getAudioChannels(inputFilePath);
        if (mainFileSampleRate > 0) {
            commonSampleRate = mainFileSampleRate;
        } else {
            MyLog.e(AudioUtils.class, "input audio file analysis error !" + MyLog.getLineNumber());
            return false;
        }

        if (mianFileChannels > 0) {
            commonChannels = mianFileChannels;
        } else {
            MyLog.e(AudioUtils.class, "input audio file analysis error !" + MyLog.getLineNumber());
            return false;
        }

        if (!CommonUtils.getAudioPcm(inputFilePath, inputFilePcm)) {
            MyLog.e(AudioUtils.class, "input audio file decode error !" + MyLog.getLineNumber());
            return false;
        }

        List<String> bgListPcm = new ArrayList<String>();
        List<Integer> bgSampleRateList = new ArrayList<Integer>();
        List<Integer> bgChannelsList = new ArrayList<Integer>();
        for (int i = 0; i < bgMusicPathList.size(); i++) {
            int tempSR = CommonUtils.getAudioSampleRate(bgMusicPathList.get(i));
            int tempC = CommonUtils.getAudioChannels(bgMusicPathList.get(i));
            bgSampleRateList.add(tempSR);
            bgChannelsList.add(tempC);

            if (tempSR < 1) {
                MyLog.e(AudioUtils.class, "input audio file analysis error !" + MyLog.getLineNumber());
                return false;
            }

            if (tempC < 1) {
                MyLog.e(AudioUtils.class, "input audio file analysis error !" + MyLog.getLineNumber());
                return false;
            }

            String tempPcm = tempPath + UUID.randomUUID() + ".pcm";
            boolean isOk = CommonUtils.getAudioPcm(bgMusicPathList.get(i), tempPcm);
            if (!isOk) {
                MyLog.e(AudioUtils.class, "input audio file analysis error !" + MyLog.getLineNumber());
                return false;
            }

            //do resample
            if (tempSR == commonSampleRate) { // same sample rate
                if (tempC == commonChannels) {
                    bgListPcm.add(tempPcm);
                } else {
                    String temp = tempPath + UUID.randomUUID() + ".pcm";
                    Resample.doReChannels(tempPcm, temp, tempC, commonChannels);
                    bgListPcm.add(temp);
                }
            } else {                                    //diff sample rate
                if (tempC == commonChannels) {
                    String temp = tempPath + UUID.randomUUID() + ".pcm";
                    Resample.doResample(tempPcm, temp, tempSR, commonSampleRate);
                    bgListPcm.add(temp);
                } else {                              // diff sample rate  and channels
                    String temps = tempPath + UUID.randomUUID() + ".pcm";
                    Resample.doResample(tempPcm, temps, tempSR, commonSampleRate);
                    String tempc = tempPath + UUID.randomUUID() + ".pcm";
                    Resample.doReChannels(temps, tempc, tempC, commonChannels);
                    bgListPcm.add(tempc);
                }

            }
        }

        boolean misRet = mixPcmAudio(inputFilePcm, volume, outPcm, bgListPcm, bgMusicStartEnd, volumeList, false);
        if (!misRet) {
            MyLog.e(AudioUtils.class, "mix pcm error !" + MyLog.getLineNumber());
            return false;
        }

        return CommonUtils.encodeAudio(outPcm, outputFilePath, commonSampleRate, commonChannels);

    }

    /**
     * 音频文件混音,背景音乐重复播放（格式WAV MP3 AAC）
     *
     * @param inputFilePath   主音频文件路径
     * @param volume          主音频的音量
     * @param outputFilePath  输出音频文件路径
     * @param bgMusicPathList 要在主文件上混入声音的音频文件列表
     * @param bgMusicStartEnd bgMusicPathList的基于主音频文件混入起始位置列表（基于主音频文件百分比位置）
     * @param volumeList      bgMusicPathList的音量 （音量从[0,1], 1是原始音量大小，小于1则使音量变小,0为无声）
     */
    public static boolean mixAudioRepeat(String inputFilePath, double volume, String outputFilePath, List<String> bgMusicPathList,
                                         List<double[]> bgMusicStartEnd, List<Double> volumeList) {
        if (bgMusicPathList == null || bgMusicPathList.size() == 0 || bgMusicStartEnd == null
                || bgMusicStartEnd.size() == 0 || bgMusicPathList.size() != bgMusicStartEnd.size()) {
            return false;
        }

        String tempPath = AudioConfig.getTempFolderPath();

        String outPcm = tempPath + UUID.randomUUID() + ".pcm";
        int commonSampleRate;
        int commonChannels;

        String inputFilePcm = tempPath + UUID.randomUUID() + ".pcm";
        int mainFileSampleRate = CommonUtils.getAudioSampleRate(inputFilePath);
        int mianFileChannels = CommonUtils.getAudioChannels(inputFilePath);
        if (mainFileSampleRate > 0) {
            commonSampleRate = mainFileSampleRate;
        } else {
            MyLog.e(AudioUtils.class, "input audio file analysis error !" + MyLog.getLineNumber());
            return false;
        }

        if (mianFileChannels > 0) {
            commonChannels = mianFileChannels;
        } else {
            MyLog.e(AudioUtils.class, "input audio file analysis error !" + MyLog.getLineNumber());
            return false;
        }

        if (!CommonUtils.getAudioPcm(inputFilePath, inputFilePcm)) {
            MyLog.e(AudioUtils.class, "input audio file decode error !" + MyLog.getLineNumber());
            return false;
        }

        List<String> bgListPcm = new ArrayList<String>();
        List<Integer> bgSampleRateList = new ArrayList<Integer>();
        List<Integer> bgChannelsList = new ArrayList<Integer>();
        for (int i = 0; i < bgMusicPathList.size(); i++) {
            int tempSR = CommonUtils.getAudioSampleRate(bgMusicPathList.get(i));
            int tempC = CommonUtils.getAudioChannels(bgMusicPathList.get(i));
            bgSampleRateList.add(tempSR);
            bgChannelsList.add(tempC);

            if (tempSR < 1) {
                MyLog.e(AudioUtils.class, "input audio file analysis error !" + MyLog.getLineNumber());
                return false;
            }

            if (tempC < 1) {
                MyLog.e(AudioUtils.class, "input audio file analysis error !" + MyLog.getLineNumber());
                return false;
            }

            String tempPcm = tempPath + UUID.randomUUID() + ".pcm";
            boolean isOk = CommonUtils.getAudioPcm(bgMusicPathList.get(i), tempPcm);
            if (!isOk) {
                MyLog.e(AudioUtils.class, "input audio file analysis error !" + MyLog.getLineNumber());
                return false;
            }

            //do resample
            if (tempSR == commonSampleRate) { // same sample rate
                if (tempC == commonChannels) {
                    bgListPcm.add(tempPcm);
                } else {
                    String temp = tempPath + UUID.randomUUID() + ".pcm";
                    Resample.doReChannels(tempPcm, temp, tempC, commonChannels);
                    bgListPcm.add(temp);
                }
            } else {                                    //diff sample rate
                if (tempC == commonChannels) {
                    String temp = tempPath + UUID.randomUUID() + ".pcm";
                    Resample.doResample(tempPcm, temp, tempSR, commonSampleRate);
                    bgListPcm.add(temp);
                } else {                              // diff sample rate  and channels
                    String temps = tempPath + UUID.randomUUID() + ".pcm";
                    Resample.doResample(tempPcm, temps, tempSR, commonSampleRate);
                    String tempc = tempPath + UUID.randomUUID() + ".pcm";
                    Resample.doReChannels(temps, tempc, tempC, commonChannels);
                    bgListPcm.add(tempc);
                }

            }
        }

        boolean misRet = mixPcmAudio(inputFilePcm, volume, outPcm, bgListPcm, bgMusicStartEnd, volumeList, true);
        if (!misRet) {
            MyLog.e(AudioUtils.class, "mix pcm error !" + MyLog.getLineNumber());
            return false;
        }

        return CommonUtils.encodeAudio(outPcm, outputFilePath, commonSampleRate, commonChannels);

    }

    /**
     * PCM文件混音（音量从[0,1], 1是原始音量大小，小于1则使音量变小,0为无声）
     *
     * @param inputFilePath   主输入PCM文件路径
     * @param volume          主输入PCM的音量
     * @param outputFilePath  输出PCM文件路径
     * @param bgMusicPathList 要在主文件上混入声音的PCM文件列表
     * @param bgMusicStartEnd bgMusicPathList的基于主PCM文件混入起始位置列表 （基于主音频文件百分比位置）
     * @param volumeList      bgMusicPathList的音量
     * @param isRepeat        背景音乐是否重复播放
     */
    public static boolean mixPcmAudio(String inputFilePath, double volume, String outputFilePath, List<String> bgMusicPathList,
                                      List<double[]> bgMusicStartEnd, List<Double> volumeList, boolean isRepeat) {
        if (bgMusicPathList == null || bgMusicPathList.size() == 0 || bgMusicStartEnd == null
                || bgMusicStartEnd.size() == 0 || bgMusicPathList.size() != bgMusicStartEnd.size()) {
            return false;
        }
        String tempPath = AudioConfig.getTempFolderPath();

        String tempMixPcm;

        List<String> tempMixPcmList = new ArrayList<String>();

        if (bgMusicPathList.size() == 1) {
            int reslut;
            tempMixPcm = outputFilePath;
            if (isRepeat) {
                reslut = PcmMix.mixPcmVloAdjustRepeat(inputFilePath, bgMusicPathList.get(0), tempMixPcm, bgMusicStartEnd.get(0)[0], bgMusicStartEnd.get(0)[1], volume, volumeList.get(0));
            } else {
                reslut = PcmMix.mixPcmVloAdjust(inputFilePath, bgMusicPathList.get(0), tempMixPcm, bgMusicStartEnd.get(0)[0], bgMusicStartEnd.get(0)[1], volume, volumeList.get(0));
            }
            if (reslut < 0) {
                FileUtils.deleteFile(tempMixPcm);
                MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
                return false;
            }
        } else {
            for (int i = 0; i < bgMusicPathList.size(); i++) {
                tempMixPcm = tempPath + UUID.randomUUID() + ".pcm";
                int reslut;
                if (i == 0) {
                    tempMixPcmList.add(tempMixPcm);
                    if (isRepeat) {
                        reslut = PcmMix.mixPcmVloAdjustRepeat(inputFilePath, bgMusicPathList.get(0), tempMixPcm, bgMusicStartEnd.get(0)[0], bgMusicStartEnd.get(0)[1], volume, volumeList.get(0));
                    } else {
                        reslut = PcmMix.mixPcmVloAdjust(inputFilePath, bgMusicPathList.get(0), tempMixPcm, bgMusicStartEnd.get(0)[0], bgMusicStartEnd.get(0)[1], volume, volumeList.get(0));
                    }
                    if (reslut < 0) {
                        MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
                        return false;
                    }
                } else {
                    if (i == bgMusicPathList.size() - 1) {
                        tempMixPcm = outputFilePath;
                    } else {
                        tempMixPcmList.add(tempMixPcm);
                    }
                    if (isRepeat) {
                        reslut = PcmMix.mixPcmVloAdjustRepeat(tempMixPcmList.get(i - 1), bgMusicPathList.get(i), tempMixPcm, bgMusicStartEnd.get(i)[0], bgMusicStartEnd.get(i)[1], 1, volumeList.get(i));
                    } else {
                        reslut = PcmMix.mixPcmVloAdjust(tempMixPcmList.get(i - 1), bgMusicPathList.get(i), tempMixPcm, bgMusicStartEnd.get(i)[0], bgMusicStartEnd.get(i)[1], 1, volumeList.get(i));
                    }
                    if (reslut < 0) {
                        FileUtils.deleteFile(tempMixPcmList.get(i - 1));
                        MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
                        return false;
                    }
                }
            }

            FileUtils.deleteFile(tempMixPcmList);
        }
        return true;
    }

    /**
     * 填充空白延长音频文件,WAV MP3 AAC
     *
     * @param inputPath  WAV MP3 AAC 任意格式，必须包含正确后缀
     * @param outputPath WAV MP3 AAC 任意格式，必须包含正确后缀
     * @param duration   延长后音频文件总时长 （单位：秒）
     * @param start      原有声音频文件在延长后的文件的某一开始位置 （单位：秒）
     * @param end        原有声音频文件在延长后的文件的某一结束位置 （单位：秒）
     * @return
     */
    public static boolean expandAudioDuration(String inputPath, String outputPath, double duration, double start, double end) {
        int sampleRate = CommonUtils.getAudioSampleRate(inputPath);
        int channels = CommonUtils.getAudioChannels(inputPath);
        double statDuration = start;
        double endDuration = duration - end;

        if (sampleRate < 1 || channels < 1) {
            MyLog.e(AudioUtils.class, "input audio file is  bad !" + MyLog.getLineNumber());
            return false;
        }

        if (start == 0 && end == duration) {
            MyLog.e(AudioUtils.class, "take a superfluous action ,you are nothing to do?" + MyLog.getLineNumber());
            return false;
        }

        //在后面扩大
        if (start == 0) {
            String endPartWav = getRandomFilePath("wav");
            boolean ret = GenerateMuteAudio.generteMuteWav(sampleRate, channels, 16, endDuration, endPartWav);
            if (!ret) {
                return false;
            }

            String inputPartWav = getRandomFilePath("wav");
            ret = CommonUtils.audioToWav(inputPath, inputPartWav);
            if (!ret) {
                MyLog.e(AudioUtils.class, "input audio file is  bad !" + MyLog.getLineNumber());
                return false;
            }

            String outputWav = getRandomFilePath("wav");
            int result = SoundJoint.joint(inputPartWav, endPartWav, outputWav);
            if (result < 0) {
                MyLog.e(AudioUtils.class, "joint wav file fail !" + MyLog.getLineNumber());
                return false;
            }

            ret = CommonUtils.wavToAudio(outputWav, outputPath);
            if (!ret) {
                MyLog.e(AudioUtils.class, "encode audio fail !" + MyLog.getLineNumber());
                return false;
            }

            //在前面扩大
        } else if (end == duration) {
            String startPartWav = getRandomFilePath("wav");
            boolean ret = GenerateMuteAudio.generteMuteWav(sampleRate, channels, 16, statDuration, startPartWav);
            if (!ret) {
                return false;
            }

            String inputPartWav = getRandomFilePath("wav");
            ret = CommonUtils.audioToWav(inputPath, inputPartWav);
            if (!ret) {
                MyLog.e(AudioUtils.class, "input audio file is  bad !" + MyLog.getLineNumber());
                return false;
            }

            String outputWav = getRandomFilePath("wav");
            int result = SoundJoint.joint(startPartWav, inputPartWav, outputWav);
            if (result < 0) {
                MyLog.e(AudioUtils.class, "joint wav file fail !" + MyLog.getLineNumber());
                return false;
            }

            ret = CommonUtils.wavToAudio(outputWav, outputPath);
            if (!ret) {
                MyLog.e(AudioUtils.class, "encode audio fail !" + MyLog.getLineNumber());
                return false;
            }

            //两头扩大
        } else {
            String startPartWav = getRandomFilePath("wav");
            boolean ret = GenerateMuteAudio.generteMuteWav(sampleRate, channels, 16, statDuration, startPartWav);
            if (!ret) {
                return false;
            }
            String endPartWav = getRandomFilePath("wav");
            ret = GenerateMuteAudio.generteMuteWav(sampleRate, channels, 16, endDuration, endPartWav);
            if (!ret) {
                return false;
            }

            String inputPartWav = getRandomFilePath("wav");
            ret = CommonUtils.audioToWav(inputPath, inputPartWav);
            if (!ret) {
                MyLog.e(AudioUtils.class, "input audio file is  bad !" + MyLog.getLineNumber());
                return false;
            }

            String outputWav = getRandomFilePath("wav");
            List<String> inputWavList = new ArrayList<String>();
            inputWavList.add(startPartWav);
            inputWavList.add(inputPartWav);
            inputWavList.add(endPartWav);
            int result = SoundJoint.joint(outputWav, inputWavList);
            if (result < 0) {
                MyLog.e(AudioUtils.class, "joint wav file fail !" + MyLog.getLineNumber());
                return false;
            }

            ret = CommonUtils.wavToAudio(outputWav, outputPath);
            if (!ret) {
                MyLog.e(AudioUtils.class, "encode audio fail !" + MyLog.getLineNumber());
                return false;
            }

        }

        return true;
    }

    /**
     * 变速、变调、既变调又变速
     *
     * @param inputAacPath
     * @param outputPath
     * @param spend        变调变速 （不改变设置为1）
     * @param pitch        变调 （不改变设置为0）
     * @param tempo        变速 （不改变设置为1）
     * @param outputType   输出文件类型  AudioConfig.AUDIO_TYPE_WAV    AudioConfig.AUDIO_TYPE_PCM    AudioConfig.AUDIO_TYPE_AAC
     * @return
     */
    public static boolean changeAacSound(String inputAacPath, String outputPath, float spend, float pitch, float tempo, int outputType) {
        int result;
        String tempInputWavFile = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav";

        long sampleRate = AacEnDecoder.getSamplerate(inputAacPath);
        int channels = AacEnDecoder.getChannels(inputAacPath);


        result = aacToWav(inputAacPath, tempInputWavFile);
        if (result < 0) {
            FileUtils.deleteFile(tempInputWavFile);
            MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
            return false;
        }

        FileUtils.deleteFile(outputPath);
        boolean resultB;
        //wav
        if (outputType == AudioConfig.AUDIO_TYPE_WAV) {
            resultB = changeWavSound(tempInputWavFile, outputPath, spend, pitch, tempo, AudioConfig.AUDIO_TYPE_WAV);
            FileUtils.deleteFile(tempInputWavFile);
            if (!resultB) {
                FileUtils.deleteFile(outputPath);
                return false;
            }
            return true;
        }

        //pcm
        if (outputType == AudioConfig.AUDIO_TYPE_PCM) {
            String tempOutputWavFile = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav";
            resultB = changeWavSound(tempInputWavFile, tempOutputWavFile, spend, pitch, tempo, AudioConfig.AUDIO_TYPE_WAV);
            FileUtils.deleteFile(tempInputWavFile);
            if (!resultB) {
                FileUtils.deleteFile(tempOutputWavFile);
                return false;
            }

            result = PcmWav.wavToPcm(tempOutputWavFile, outputPath);
            FileUtils.deleteFile(tempOutputWavFile);
            if (result < 0) {
                FileUtils.deleteFile(outputPath);
                return false;
            }
            return true;
        }

        //aac
        if (outputType == AudioConfig.AUDIO_TYPE_AAC) {
            String tempOutputWavFile = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav";
            resultB = changeWavSound(tempInputWavFile, tempOutputWavFile, spend, pitch, tempo, AudioConfig.AUDIO_TYPE_WAV);
            FileUtils.deleteFile(tempInputWavFile);
            if (!resultB) {
                FileUtils.deleteFile(tempOutputWavFile);
                return false;
            }

            result = wavToAac(tempOutputWavFile, outputPath);
            FileUtils.deleteFile(tempOutputWavFile);
            if (result < 0) {
                FileUtils.deleteFile(outputPath);
                return false;
            }
            return true;
        }

        FileUtils.deleteFile(tempInputWavFile);
        FileUtils.deleteFile(outputPath);
        MyLog.e(AudioUtils.class, "please set output file type !");
        return false;
    }

    /**
     * 改变AAC的音调、速度
     *
     * @param inputAacPath
     * @param outpuPath
     * @param duration     AAC总时长
     * @param startTime
     * @param endTime
     * @param spend        变调变速 （不改变设置为1）
     * @param pitch        变调 （不改变设置为0）
     * @param tempo        变速 （不改变设置为1）
     * @param outputType   输出文件类型  AudioConfig.AUDIO_TYPE_WAV    AudioConfig.AUDIO_TYPE_PCM    AudioConfig.AUDIO_TYPE_AAC
     * @return
     */
    public static boolean changeAacSound(String inputAacPath, String outpuPath, double duration, double startTime, double endTime,
                                         float spend, float pitch, float tempo, int outputType) {
        int result;
        boolean bResult;

        String tempInputWav = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav";
        String tempOutputWav = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav";
        List<String> tempOutputWavList = new ArrayList<String>();
        tempOutputWavList.add(AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav");
        tempOutputWavList.add(AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav");
        tempOutputWavList.add(AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav");

        int sampleRate = (int) AacEnDecoder.getSamplerate(inputAacPath);
        int channels = AacEnDecoder.getChannels(inputAacPath);

        result = aacToWav(inputAacPath, tempInputWav);
        if (result < 0) {
            FileUtils.deleteFile(tempInputWav);
            MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
            return false;
        }

        List<double[]> timestampList = new ArrayList<double[]>();
        timestampList.add(new double[]{0, startTime});
        timestampList.add(new double[]{startTime, endTime});
        timestampList.add(new double[]{endTime, duration});
        bResult = WavClip.clip(tempInputWav, tempOutputWavList, timestampList);
        FileUtils.deleteFile(tempInputWav);
        if (!bResult) {
            FileUtils.deleteFile(tempOutputWavList);
            MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
            return false;
        }

        String tempChangeWav = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav";
        bResult = changeWavSound(tempOutputWavList.remove(1), tempChangeWav, spend, pitch, tempo, outputType);
        if (!bResult) {
            MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
            FileUtils.deleteFile(tempChangeWav);
            FileUtils.deleteFile(tempOutputWavList);
            return false;
        }

        tempOutputWavList.add(1, tempChangeWav);

        result = SoundJoint.joint(tempOutputWav, tempOutputWavList);
        FileUtils.deleteFile(tempOutputWavList);
        if (result < 0) {
            FileUtils.deleteFile(tempOutputWav);
            MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
            return false;
        }

        result = wavToAac(tempOutputWav, outpuPath);
        FileUtils.deleteFile(tempOutputWav);
        if (result < 0) {
            FileUtils.deleteFile(outpuPath);
            MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
            return false;
        }

        return true;
    }


    /**
     * 变速、变调、既变调又变速
     *
     * @param inputWavPath
     * @param outputPath
     * @param spend        变调变速 （不改变设置为1）
     * @param pitch        变调 （不改变设置为0）
     * @param tempo        变速 （不改变设置为1）
     * @param outputType   输出文件类型  AudioConfig.AUDIO_TYPE_WAV    AudioConfig.AUDIO_TYPE_PCM    AudioConfig.AUDIO_TYPE_AAC
     * @return
     */
    public static boolean changeWavSound(String inputWavPath, String outputPath, float spend, float pitch, float tempo, int outputType) {
        int result;
        String tempOutputWavFile = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".wav";

        HashMap<String, Integer> wavInfoMap = FileUtils.getWavInfo(inputWavPath);
        long sampleRate = wavInfoMap.get("samplerate") > 0 ? wavInfoMap.get("samplerate") : 44100;
        int channels = wavInfoMap.get("channels") > 0 ? wavInfoMap.get("channels") : 2;


        SoundFactory soundFactory = new SoundFactory();
        soundFactory.setSpeed(spend);
        soundFactory.setPitchSemiTones(pitch);
        soundFactory.setTempo(tempo);
        result = soundFactory.processFile(inputWavPath, tempOutputWavFile);
        if (result < 0) {
            FileUtils.deleteFile(tempOutputWavFile);
            MyLog.e(AudioUtils.class, "error !" + MyLog.getLineNumber());
            return false;
        }

        FileUtils.deleteFile(outputPath);

        //wav
        if (outputType == AudioConfig.AUDIO_TYPE_WAV) {
            new File(tempOutputWavFile).renameTo(new File(outputPath));
            return true;
        }

        //pcm
        if (outputType == AudioConfig.AUDIO_TYPE_PCM) {
            result = PcmWav.wavToPcm(tempOutputWavFile, outputPath);
            FileUtils.deleteFile(tempOutputWavFile);
            if (result < 0) {
                FileUtils.deleteFile(outputPath);
                return false;
            }
            return true;
        }

        //aac
        if (outputType == AudioConfig.AUDIO_TYPE_AAC) {
            result = wavToAac(tempOutputWavFile, outputPath);
            FileUtils.deleteFile(tempOutputWavFile);
            if (result < 0) {
                FileUtils.deleteFile(outputPath);
                return false;
            }
            return true;
        }

        FileUtils.deleteFile(tempOutputWavFile);
        FileUtils.deleteFile(outputPath);
        MyLog.e(AudioUtils.class, "please set output file type !");
        return false;
    }

    /**
     * 延长重复音乐 （WAV MP3 AAC 任意格式输入输出）
     *
     * @param inputFile
     * @param outputFile
     * @param duration   延长后的总时长 单位：秒
     * @return
     */
    public static boolean repeatAudio(String inputFile, String outputFile, double duration) {
        double rawFileDuration = getAudioDuration(inputFile);
        if (duration <= rawFileDuration) {
            MyLog.e(AudioUtils.class, "output duraion < file dration" + MyLog.getLineNumber());
            return false;
        }
        boolean result;
        int ret;

        double excessDuration = duration % rawFileDuration;

        String tempPath = AudioConfig.getTempFolderPath();
        String rawAudioWav = tempPath + UUID.randomUUID() + ".wav";
        String endBlockWav = tempPath + UUID.randomUUID() + ".wav";
        String finalWav = tempPath + UUID.randomUUID() + ".wav";
        result = CommonUtils.audioToWav(inputFile, rawAudioWav);
        if (!result) {
            MyLog.e(AudioUtils.class, "CommonUtils.audioToWav" + MyLog.getLineNumber());
            return false;
        }

        //不是整数倍
        if (excessDuration > 0) {
            result = WavClip.clip(new File(rawAudioWav), new File(endBlockWav), 0, excessDuration);
            if (!result) {
                MyLog.e(AudioUtils.class, "WavClip.clip" + MyLog.getLineNumber());
                return false;
            }
        }

        int block;
        if (excessDuration == 0) {
            block = (int) (duration / rawFileDuration);
        } else {
            block = (int) (duration / rawFileDuration) + 1;
        }

        if (block == 2) {
            if (excessDuration > 0) {
                ret = SoundJoint.joint(rawAudioWav, endBlockWav, finalWav);
                if (ret < 0) {
                    MyLog.e(AudioUtils.class, "SoundJoint.joint" + MyLog.getLineNumber());
                    return false;
                }
            } else {
                ret = SoundJoint.joint(rawAudioWav, rawAudioWav, finalWav);
                if (ret < 0) {
                    MyLog.e(AudioUtils.class, "SoundJoint.joint" + MyLog.getLineNumber());
                    return false;
                }
            }
        }

        if (block > 2) {
            List<String> fileList = new ArrayList<String>();
            if (excessDuration > 0) {
                for (int i = 0; i < block - 1; i++) {
                    fileList.add(rawAudioWav);
                }
                fileList.add(endBlockWav);
                ret = SoundJoint.joint(finalWav, fileList);
                if (ret < 0) {
                    MyLog.e(AudioUtils.class, "SoundJoint.joint" + MyLog.getLineNumber());
                    return false;
                }
            } else {
                for (int i = 0; i < block; i++) {
                    fileList.add(rawAudioWav);
                }
                ret = SoundJoint.joint(finalWav, fileList);
                if (ret < 0) {
                    MyLog.e(AudioUtils.class, "SoundJoint.joint" + MyLog.getLineNumber());
                    return false;
                }
            }
        }

        result = CommonUtils.wavToAudio(finalWav, outputFile);
        if (!result) {
            MyLog.e(AudioUtils.class, "CommonUtils.wavToAudio" + MyLog.getLineNumber());
            return false;
        }
        return true;
    }


    /**
     * 裁剪音频 （支持WAV AAC MP3任意输入输出格式）
     *
     * @param inputFilePath
     * @param outputFileList
     * @param timestamps     裁剪时间起始点[起点][终点] 单位:秒
     * @return
     */
    public static boolean clipAudio(String inputFilePath, List<String> outputFileList, List<double[]> timestamps) {
        String tempPath = AudioConfig.getTempFolderPath();
        boolean result;
        if (outputFileList.size() != timestamps.size()) {
            return false;
        }

        int blockNum = outputFileList.size();

        String inputWavFilePath = tempPath + UUID.randomUUID() + ".wav";

        result = CommonUtils.audioToWav(inputFilePath, inputWavFilePath);
        if (!result) {
            MyLog.e(AudioUtils.class, "audioToWav fail" + MyLog.getLineNumber());
            return false;
        }

        List<String> tempWavFileList = new ArrayList<String>();
        for (int i = 0; i < blockNum; i++) {
            tempWavFileList.add(tempPath + UUID.randomUUID() + ".wav");
        }

        result = WavClip.clip(inputWavFilePath, tempWavFileList, timestamps);
        if (!result) {
            MyLog.e(AudioUtils.class, "WavClip.clip fail" + MyLog.getLineNumber());
            return false;
        }
        for (int i = 0; i < blockNum; i++) {
            result = CommonUtils.wavToAudio(tempWavFileList.get(i), outputFileList.get(i));
            if (!result) {
                MyLog.e(AudioUtils.class, "CommonUtils.wavToAudio fail" + MyLog.getLineNumber());
                return false;
            }
        }

        return true;
    }

    /**
     * 获取WAV MP3 AAC音频文件时长  （单位：秒）
     *
     * @param inputFilePath
     * @return
     */
    public static double getAudioDuration(String inputFilePath) {
        int sampleRate = CommonUtils.getAudioSampleRate(inputFilePath);
        int chanels = CommonUtils.getAudioChannels(inputFilePath);

        String tempPcm = AudioConfig.getTempFolderPath() + UUID.randomUUID() + ".pcm";
        boolean result = CommonUtils.getAudioPcm(inputFilePath, tempPcm);
        if (!result) {
            MyLog.e(AudioUtils.class, "CommonUtils.getAudioPcm fail" + MyLog.getLineNumber());
            return -1;
        }
        double duration = CommonUtils.getPcmDuration(sampleRate, new File(tempPcm).length(), 16, chanels);
        FileUtils.deleteFile(tempPcm);
        return duration;
    }


    /**
     * 音频转码 支持格式WAV MP3 AAC（文件名必须包含正确的后缀名）
     *
     * @param inputFilePath
     * @param outputFilePath
     * @return
     */
    public static boolean transcode(String inputFilePath, String outputFilePath) {
        String tempPcm = getRandomFilePath("pcm");
        String[] inputFomponents = inputFilePath.toLowerCase().split("\\.");
        String[] outputFomponents = outputFilePath.toLowerCase().split("\\.");
        if (inputFomponents.length < 2) {
            MyLog.e(AudioUtils.class, "input file no suffix" + MyLog.getLineNumber());
            return false;
        }

        if (outputFomponents.length < 2) {
            MyLog.e(AudioUtils.class, "output file no suffix" + MyLog.getLineNumber());
            return false;
        }

        int sampleRate = -1;
        int channels = -1;
        int result = -1;
        if (inputFomponents[inputFomponents.length - 1].equals("aac")) {
            sampleRate = AacEnDecoder.getSamplerate(inputFilePath);
            channels = AacEnDecoder.getChannels(inputFilePath);
            result = AacEnDecoder.decodeAAC1(inputFilePath, tempPcm);
            if (sampleRate < 1 || channels < 1 || result < 0) {
                MyLog.e(AudioUtils.class, "decode input file fail" + MyLog.getLineNumber());
                return false;
            }
        }

        if (inputFomponents[inputFomponents.length - 1].equals("mp3")) {
            sampleRate = MP3DeEncode.getSamplerate(inputFilePath);
            channels = NativeUtils.getAudioChannels(inputFilePath);//MP3DeEncode.getChannels(inputFilePath); 由于MP3库有问题，暂时用FFMPEG替代解码和获取频道数功能
            result = NativeUtils.getAudioPCM(inputFilePath, tempPcm);//MP3DeEncode.decode(inputFilePath, tempPcm);
            if (sampleRate < 1 || channels < 1 || result < 0) {
                MyLog.e(AudioUtils.class, "decode input file fail" + MyLog.getLineNumber());
                return false;
            }
        }

        if (inputFomponents[inputFomponents.length - 1].equals("wav")) {
            int[] head = SoundJoint.getWavHead(inputFilePath);
            if (head == null || head.length < 2) {
                return false;
            }
            sampleRate = head[0];
            channels = head[1];
            result = PcmWav.wavToPcm(inputFilePath, tempPcm);
            if (sampleRate < 1 || channels < 1 || result < 0) {
                MyLog.e(AudioUtils.class, "decode input file fail" + MyLog.getLineNumber());
                return false;
            }
        }


        if (outputFomponents[outputFomponents.length - 1].equals("aac")) {
            result = AacEnDecoder.encodeAAC(sampleRate, channels, 16, tempPcm, outputFilePath);
            if (result < 0) {
                MyLog.e(AudioUtils.class, "encode pcm file fail" + MyLog.getLineNumber());
                return false;
            }
        }

        if (outputFomponents[outputFomponents.length - 1].equals("mp3")) {
            result = MP3DeEncode.encode(tempPcm, outputFilePath, sampleRate, channels);
            if (result < 0) {
                MyLog.e(AudioUtils.class, "encode pcm file fail" + MyLog.getLineNumber());
                return false;
            }
        }

        if (outputFomponents[outputFomponents.length - 1].equals("wav")) {
            boolean ret = PcmToWav.pcmToWav(tempPcm, outputFilePath, sampleRate, channels);
            if (!ret) {
                MyLog.e(AudioUtils.class, "encode pcm file fail" + MyLog.getLineNumber());
                return false;
            }
        }

        return true;
    }
}
