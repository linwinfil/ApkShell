package com.example.opengl.decoder;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import com.example.opengl.utils.ThreadUtils;
import com.example.opengl.utils.Utils;

import java.nio.ByteBuffer;

/**
 * @author lmx
 * Created by lmx on 2019/5/21.
 */
public class VideoDecoder implements IDecoder, Runnable
{
    private static final String TAG = "VideoDecoder";

    private final Object obj_ready = new Object();

    private static final long TIMEOUT_USEC = 10 * 1000;
    private String mPath;
    private Surface mSurface;

    private MediaCodec mMediaCodec;
    private MediaExtractor mMediaExtractor;

    private volatile boolean mRelease;
    private volatile boolean mReady;

    private int mWidth;
    private int mHeight;
    private int mRotation;
    private long mDuration;
    private int mFrameRate;

    private OnVideoDecoderListener mListener;

    private Runnable onPreparedRunnable = new Runnable() {
        @Override
        public void run()
        {
            if (mListener != null) {
                mListener.onPrepared(VideoDecoder.this);
            }
        }
    };

    private Runnable onDrawRunnable = new Runnable() {
        @Override
        public void run()
        {
            if (mListener != null) {
                mListener.onDraw();
            }
        }
    };


    public VideoDecoder(String mPath, Surface mSurface)
    {
        this.mPath = mPath;
        this.mSurface = mSurface;
        ThreadUtils.init();
    }

    public void setVideoListener(OnVideoDecoderListener mListener)
    {
        this.mListener = mListener;
    }

    private void runPrepared() {
        ThreadUtils.runOnUiThread(onPreparedRunnable);
    }

    private void runDraw() {
        ThreadUtils.runOnUiThread(onDrawRunnable);
    }

    private void wait4Ready() {
        synchronized (obj_ready) {
            if (!mReady) {
                try {
                    obj_ready.wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void ready4Render() {
        mReady = true;
        obj_ready.notify();
    }


    @Override
    public void run()
    {
        if (initExtractor()) {
            wait4Ready();
            doExtract();
        }
    }

    public int getWidth()
    {
        return mWidth;
    }

    public int getHeight()
    {
        return mHeight;
    }

    public int getRotation()
    {
        return mRotation;
    }

    public long getDuration()
    {
        return mDuration;
    }

    @Override
    public void onRelease() {
        mRelease = true;
        mListener = null;
        if (mMediaCodec != null) {
            mMediaCodec.release();
            mMediaCodec = null;
        }
        if (mMediaExtractor != null) {
            mMediaExtractor.release();
            mMediaExtractor = null;
        }
    }

    private void doExtract()
    {
        if (mMediaExtractor == null || mMediaCodec == null) return;

        boolean outputDone = false;
        boolean inputDone = false;
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        while (!outputDone && !mRelease)
        {
            if (!inputDone)
            {
                //获取输入队列空闲数组下标
                int inputBufIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputBufIndex >= 0) {
                    //获取输入队列中的数据
                    ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufIndex);
                    if (inputBuffer != null)
                    {
                        int sampleSize = mMediaExtractor.readSampleData(inputBuffer, 0);
                        if (sampleSize >= 0)
                        {
                            long sampleTime = mMediaExtractor.getSampleTime();
                            mMediaCodec.queueInputBuffer(inputBufIndex, 0, sampleSize, sampleTime, 0);
                            mMediaExtractor.advance();
                        }
                        else
                        {
                            mMediaCodec.queueInputBuffer(inputBufIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            inputDone = true;
                        }
                    }

                }
            }

            if (!outputDone)
            {
                int outpytBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                if (outpytBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    Log.d(TAG, "VideoDecoder --> doExtract: info_try_again_later");
                } else if (outpytBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat outputFormat = mMediaCodec.getOutputFormat();
                    Log.d(TAG, "VideoDecoder --> doextract: info_output_format_changed " + outputFormat.toString());
                } else if (outpytBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    Log.d(TAG, "VideoDecoder --> doExtract: info_output_buffers_changed");
                } else if (outpytBufferIndex < 0) {
                    Log.e(TAG, "VideoDecoder --> doExtract: status < 0");
                } else {
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        outputDone = true;
                    }

                    boolean doRender = (bufferInfo.size != 0);
                    mMediaCodec.releaseOutputBuffer(outpytBufferIndex, doRender);
                    if (doRender) {
                        runDraw();
                    }
                }
            }
        }

    }

    private boolean initExtractor()
    {
        if (mMediaExtractor != null && mMediaCodec != null) return false;

        if (!Utils.isFileExists(mPath) || mSurface == null) return false;

        if (mReady) return true;

        mMediaExtractor = new MediaExtractor();
        try
        {
            mMediaExtractor.setDataSource(mPath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            mMediaExtractor.release();
            mMediaExtractor = null;
        }
        if (mMediaExtractor == null) return false;

        MediaFormat mediaFormat = null;
        int trackIndex = -1;
        int trackCount = mMediaExtractor.getTrackCount();
        for (int i = 0; i < trackCount; i++)
        {
            MediaFormat trackFormat = mMediaExtractor.getTrackFormat(i);
            String mime = trackFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                trackCount = i;
                mediaFormat = trackFormat;
                break;
            }
        }
        if (trackCount < 0 || mediaFormat == null) {
            throw new IllegalStateException("no found track in video path");
        }
        mMediaExtractor.selectTrack(trackIndex);
        mWidth = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
        mHeight = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
        mDuration = mediaFormat.getLong(MediaFormat.KEY_DURATION);
        mFrameRate = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRotation = mediaFormat.getInteger(MediaFormat.KEY_ROTATION);
        } else {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try
            {
                retriever.setDataSource(mPath);
                String s = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                mRotation = Integer.valueOf(s);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                retriever.release();
            }
        }

        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
        String decoderForFormat = mediaCodecList.findDecoderForFormat(mediaFormat);
        if (decoderForFormat != null)
        {
            try
            {
                mMediaCodec = MediaCodec.createByCodecName(decoderForFormat);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                mMediaCodec = null;
            }
            if (mMediaCodec == null)return false;

            mMediaCodec.configure(mediaFormat, mSurface, null, 0);
            mMediaCodec.start();
            runPrepared();
            return true;
        }
        return false;
    }
}
