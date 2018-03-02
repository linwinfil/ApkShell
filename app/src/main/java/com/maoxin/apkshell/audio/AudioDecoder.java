package com.maoxin.apkshell.audio;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import com.maoxin.apkshell.BuildConfig;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Created by: fwc
 * Date: 2018/1/10
 */
public class AudioDecoder
{

    private static final String TAG = "AudioDecoder";

    private static final int TIMEOUT_USEC = 10000;

    private String mSourcePath;

    private boolean mRequestStop;

    private MediaExtractor mMediaExtractor;
    private MediaCodec mDecoder;

    private OnDecoderListener mOnDecoderListener;

    public AudioDecoder(String sourcePath)
    {
        mSourcePath = sourcePath;
    }

    public void setOnDecoderListener(OnDecoderListener listener)
    {
        mOnDecoderListener = listener;
    }

    private boolean prepare()
    {
        mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(mSourcePath);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        int trackIndex = -1;
        for (int i = 0; i < mMediaExtractor.getTrackCount(); i++)
        {
            MediaFormat format = mMediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/"))
            {
                trackIndex = i;
                break;
            }
        }

        if (trackIndex < 0)
        {
            releaseExtractor();
            if (BuildConfig.DEBUG)
            {
                throw new RuntimeException("has not found audio track");
            }
            return false;
        }

        MediaFormat mediaFormat = mMediaExtractor.getTrackFormat(trackIndex);

        int sampleRate = mediaFormat.containsKey(MediaFormat.KEY_SAMPLE_RATE) ? mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE) : 0;
        int channelCount = mediaFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT) ? mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT) : 0;
        long duration = mediaFormat.containsKey(MediaFormat.KEY_DURATION) ? mediaFormat.getLong(MediaFormat.KEY_DURATION) : 0;
        String mime = mediaFormat.containsKey(MediaFormat.KEY_MIME) ? mediaFormat.getString(MediaFormat.KEY_MIME) : "";
        if (mOnDecoderListener != null)
        {
            mOnDecoderListener.onInfo(mime, sampleRate, channelCount, duration / 1000L);
        }

        mMediaExtractor.selectTrack(trackIndex);

        String mediaMime = mediaFormat.getString(MediaFormat.KEY_MIME);
        try
        {
            mDecoder = MediaCodec.createDecoderByType(mediaMime);
        }
        catch (IOException e)
        {
            releaseExtractor();
            mDecoder = null;
            if (BuildConfig.DEBUG)
            {
                throw new RuntimeException("has not get the audio decoder");
            }
            return false;
        }
        mDecoder.configure(mediaFormat, null, null, 0);
        mDecoder.start();
        return true;
    }

    /**
     * 开始进行音频数据解码，比较耗时
     */
    public void start()
    {
        boolean prepare = prepare();
        if (!prepare) return;

        // ByteBuffer[] codecInputBuffers = mDecoder.getInputBuffers();
        // ByteBuffer[] codecOutputBuffers = mDecoder.getOutputBuffers();

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        boolean inputDone = false;
        boolean outputDone = false;

        while (!outputDone)
        {
            if (!inputDone)
            {
                int inputBufIndex = mDecoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputBufIndex >= 0)
                {
                    // ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                    ByteBuffer dstBuf;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        dstBuf = mDecoder.getInputBuffer(inputBufIndex);
                    } else {
                        dstBuf = mDecoder.getInputBuffers()[inputBufIndex];
                    }
                    assert dstBuf != null;
                    dstBuf.clear();
                    int sampleSize = mMediaExtractor.readSampleData(dstBuf, 0);

                    if (sampleSize < 0)
                    {
                        inputDone = true;
                        mDecoder.queueInputBuffer(inputBufIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    }
                    else
                    {
                        long presentationTimeUs = mMediaExtractor.getSampleTime();
                        mDecoder.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, 0);
                        mMediaExtractor.advance();//读取下一帧数据
                    }
                }
            }

            int decoderStatus = mDecoder.dequeueOutputBuffer(info, TIMEOUT_USEC);

            if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER)
            {
                // no output available yet
                Log.d(TAG, "no output from decoder available");
            }
            else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
            {
                // codecOutputBuffers = mDecoder.getOutputBuffers();
                Log.i(TAG, "output buffers have changed.");
            }
            else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
            {
                MediaFormat newFormat = mDecoder.getOutputFormat();
                Log.i(TAG, "output format has changed to " + newFormat);
            }
            else if (decoderStatus < 0)
            {
                throw new RuntimeException("unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
            }
            else
            { // decoderStatus >= 0

                // Simply ignore codec config buffers.
                if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0)
                {
                    Log.i(TAG, "audio decoder: codec config buffer");
                    info.size = 0;
                }

                if (info.size != 0)
                {
                    // ByteBuffer outBuf = codecOutputBuffers[decoderStatus];

                    ByteBuffer outBuf;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        outBuf = mDecoder.getOutputBuffer(decoderStatus);
                    } else {
                        outBuf = mDecoder.getOutputBuffers()[decoderStatus];
                    }
                    assert outBuf != null;
                    outBuf.clear();
                    outBuf.position(info.offset);
                    outBuf.limit(info.offset + info.size);
                    byte[] data = new byte[info.size];
                    outBuf.get(data);
                    if (mOnDecoderListener != null) {
                        mRequestStop = mOnDecoderListener.onDecoded(data, info.presentationTimeUs / 1000L);
                    }
                }

                mDecoder.releaseOutputBuffer(decoderStatus, false);

                if (mRequestStop || (info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                {
                    Log.i(TAG, "saw output EOS.");
                    outputDone = true;
                }
            }
        }

        releaseExtractor();
        releaseDecoder();

        if (mOnDecoderListener != null) mOnDecoderListener.onFinish();
    }

    private void releaseExtractor()
    {
        if (mMediaExtractor != null)
        {
            mMediaExtractor.release();
            mMediaExtractor = null;
        }
    }

    private void releaseDecoder()
    {
        if (mDecoder != null)
        {
            mDecoder.stop();
            mDecoder.release();
            mDecoder = null;
        }
    }

    public interface OnDecoderListener
    {

        void onInfo(String mime, int sampleRate, int channelCount, long duration);

        /**
         * 返回解码的音频原始数据
         *
         * @param data      音频原始数据
         * @param timestamp 时间戳，单位毫秒
         * @return true: 停止解码
         */
        boolean onDecoded(byte[] data, long timestamp);

        void onFinish();
    }
}
