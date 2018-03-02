package com.maoxin.apkshell.audio;

import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.poco.audio.AacEnDecoder;

/**
 * @author lmx
 *         Created by lmx on 2018-02-28.
 */

public class JointMultiAudioTask implements Runnable
{
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_COUNT = 2;

    private int sampleRate;
    private int channelCount;

    private String outputPath;
    private ArrayList<String> jointPath;


    private FileOutputStream mOutputStream;
    private String pcmPath;

    private boolean needEncode;
    private OnProcessListener listener;
    private Handler mainHandler;

    /**
     * 拼接多段音频（必须统一采样率，声道）
     * @param jointPath
     * @param outputPath
     */
    public JointMultiAudioTask(ArrayList<String> jointPath, String outputPath)
    {
        if (jointPath == null || jointPath.size() == 0) {
            onError("joint paths is null");
            return;
        }
        if (outputPath == null) {
            onError("output path is null");
            return;

        }
        this.jointPath = jointPath;
        this.outputPath = outputPath;

        needEncode = !this.outputPath.endsWith(FileUtils.PCM_FORMAT);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setListener(OnProcessListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void run()
    {
        Thread.currentThread().setName("JointMultiAudioTask");

        onStart();

        prepare();

        runDecode();

        closeStream();

        runEncode();

        onFinish();
    }

    private void closeStream() {
        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mOutputStream = null;
    }

    private void runDecode() {
        for (String audioPath : jointPath) {
            if (FileUtils.isFileExists(audioPath)) {
                AudioDecoder audioDecoder = new AudioDecoder(audioPath);
                audioDecoder.setOnDecoderListener(mOnDecoderListener);
                audioDecoder.start();
            }
        }
    }

    private void runEncode() {
        if (!needEncode) return;

        if (sampleRate == 0) {
            sampleRate = SAMPLE_RATE;
        }

        if (channelCount == 0) {
            channelCount = CHANNEL_COUNT;
        }

        // 速度比硬编快
        int result = AacEnDecoder.encodeAAC(sampleRate, channelCount, 16, pcmPath, outputPath);
        if (result < 0) {
            onError("fail to encode the audio");
        } else {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                mmr.setDataSource(outputPath);
                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                Log.d("bbb", "JointMultiAudioTask --> encode: "+duration);
            } finally {
                mmr.release();
            }
        }
        FileUtils.delete(pcmPath);
    }

    private AudioDecoder.OnDecoderListener mOnDecoderListener = new AudioDecoder.OnDecoderListener()
    {
        @Override
        public void onInfo(String mime, int sampleRate, int channelCount, long duration)
        {
            JointMultiAudioTask.this.sampleRate = sampleRate;
            JointMultiAudioTask.this.channelCount = channelCount;
        }

        @Override
        public boolean onDecoded(byte[] data, long timestamp)
        {
            try {
                mOutputStream.write(data);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void onFinish() {

        }
    };


    private void prepare()
    {
        pcmPath = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        try {
            mOutputStream = new FileOutputStream(pcmPath);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onStart()
    {
        mainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (listener != null)
                {
                    listener.onStart();
                }
            }
        });
    }
    private void onFinish()
    {
        mainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (listener != null)
                {
                    listener.onFinish();
                }
            }
        });
    }

    private void onError(final String message)
    {
        mainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (listener != null)
                {
                    listener.onError(message);
                }
            }
        });
    }
}
