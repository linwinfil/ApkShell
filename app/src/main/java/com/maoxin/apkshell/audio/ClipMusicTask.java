package com.maoxin.apkshell.audio;

import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.poco.audio.AacEnDecoder;


/**
 * Created by: fwc
 * Date: 2018/1/10
 * 裁剪音频，最终生成aac文件
 */
public class ClipMusicTask implements Runnable
{
    private static final int SAMPLE_RATE = 44100;

    private static final int CHANNEL_COUNT = 1;


    /**
     * 1秒误差
     */
    private static final long ERROR = 1000;

    private final String mMusicPath;
    private final long mStartTime;
    private final long mEndTime;
    private final String mOutputPath;

    private String mPcmPath;
    private FileOutputStream mOutputStream;

    private AudioDecoder mAudioDecoder;

    private String mMimeType;
    private int mSampleRate;
    private int mChannelCount;
    private long mDuration;

    private boolean isRepeat;
    private int repeatCount = -1;


    private boolean isStart;
    private boolean mNeedEncode;

    private Handler mMainHandler;

    private OnProcessListener mListener;

    private String mErrorMessage;

    public ClipMusicTask(String musicPath, long startTime, long endTime, String outputPath)
    {
        // 不检查参数，需要在外面进行验证
        mMusicPath = musicPath;
        mStartTime = startTime;
        mEndTime = endTime;
        mOutputPath = outputPath;

        mNeedEncode = !mOutputPath.endsWith(FileUtils.PCM_FORMAT);

        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void setOnProcessListener(OnProcessListener listener)
    {
        mListener = listener;
    }

    private void prepare()
    {

        mPcmPath = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        try
        {
            mOutputStream = new FileOutputStream(mPcmPath);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            mErrorMessage = e.getMessage();
        }

        mAudioDecoder = new AudioDecoder(mMusicPath);
        mAudioDecoder.setOnDecoderListener(mOnDecoderListener);
    }

    @Override
    public void run()
    {
        Thread.currentThread().setName("ClipMusicTask");

        onStart();

        prepare();

        if (!TextUtils.isEmpty(mErrorMessage))
        {
            onError(mErrorMessage);
            return;
        }

        mAudioDecoder.start();

        // if (repeatCount > 0) {
        //     while (repeatCount != 0) {
        //         mAudioDecoder.start();
        //         repeatCount--;
        //     }
        // }

        try
        {
            if (mOutputStream != null) {
                mOutputStream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        mOutputStream = null;

        if (mNeedEncode)
        {
            encode();
        }

        onFinish();
    }

    private void encode()
    {
        if (mSampleRate == 0)
        {
            mSampleRate = SAMPLE_RATE;
        }

        if (mChannelCount == 0)
        {
            mChannelCount = CHANNEL_COUNT;
        }

        // 速度比硬编快
        int result = AacEnDecoder.encodeAAC(mSampleRate, mChannelCount, 16, mPcmPath, mOutputPath);
        if (result < 0)
        {
            onError("fail to encode the audio");
        } else {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try
            {
                mmr.setDataSource(mOutputPath);
                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                Log.d("bbb", "ClipMusicTask --> encode: "+duration);
            } finally
            {
                mmr.release();
            }
        }

        // 硬编
//		AudioEncoderCore.EncodeConfig config = new AudioEncoderCore.EncodeConfig(mPcmPath, mOutputPath);
//		config.sampleRate = mSampleRate;
//		config.channelCount = mChannelCount;
//
//		try {
//			AudioEncoderCore encoderCore = new AudioEncoderCore(config);
//			encoderCore.start();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

        FileUtils.delete(mPcmPath);
    }

    private AudioDecoder.OnDecoderListener mOnDecoderListener = new AudioDecoder.OnDecoderListener()
    {

        @Override
        public void onInfo(String mime, int sampleRate, int channelCount, long duration)
        {
            mMimeType = mime;
            mSampleRate = sampleRate;
            mChannelCount = channelCount;
            mDuration = duration;

            // long startOffsetTime = mDuration - mStartTime;
            // long clipOffsetTime = mEndTime - mStartTime;
            // if (clipOffsetTime > startOffsetTime) {
            //     isRepeat = true;
            // }
            // if (clipOffsetTime >= mDuration) {
            //     isRepeat = true;
            // }
            //
            // if (isRepeat && repeatCount == -1) {
            //     long excessDuration = clipOffsetTime & mDuration;
            //     if (excessDuration > 0) {
            //     }
            // }
        }

        @Override
        public boolean onDecoded(byte[] data, long timestamp)
        {
            if (!isStart && mStartTime - timestamp < ERROR) {
                isStart = true;
            }

            if (isStart) {
                try {
                    mOutputStream.write(data);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                if (timestamp >= mEndTime || timestamp >= mDuration) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public void onFinish()
        {

        }
    };

    private void onStart()
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mListener != null)
                {
                    mListener.onStart();
                }
            }
        });
    }

    private void onFinish()
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mListener != null)
                {
                    mListener.onFinish();
                }
            }
        });
    }

    private void onError(final String message)
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mListener != null)
                {
                    mListener.onError(message);
                }
            }
        });
    }
}
