package cn.poco.video;

/**
 * Created by cbw on 2017/3/20.
 */

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


public class VideoEncode {

    private static final int TEST_Y = 120;                  // YUV values for colored rect
    private static final int TEST_U = 160;
    private static final int TEST_V = 200;
    public boolean isTailer = false;  //告诉编码器，解码已经到了视频末尾

    public int mWidth = 1920;
    public int mHeight = 1080;
    public boolean isSupportNV12 = false; //标志是否支持NV12格式
    public boolean isSupportYUV420Plannar = false; //标志是否支持YUV420Plannar
    private static int BIT_RATE = 1024 * 1024 * 2;            // 2Mbps
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int IFRAME_INTERVAL = 1;          // 10 seconds between I-frames
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int TIMEOUT_USEC = 10000;

    private Integer width = new Integer(1);
    private Integer height = new Integer(1);
    private Float pts = new Float(1);
    private Integer length = new Integer(-1);

    public int NUMFRAMES = 300;

    private static final String TAG = "EncodeDecodeTest";
    private static final boolean VERBOSE = true;           // lots of logging
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding


    private MediaCodec.BufferInfo mBufferInfo;

    public MediaCodec mediaCodec;

    public MediaMuxer mMuxer;
    public int mTrackIndex;
    public boolean mMuxerStarted;
    private String mfileDir = "";
    private String mVideoOutName = "";
   // private HandlerThread mHandlerThread;
    private Handler mThreadHandler;
    private Handler mMainHandler;

    private EncodeVideoCallback m_callback;
    private static int encoderColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;

    public VideoEncode(String dir, String name) {
        mfileDir = dir;
        mVideoOutName = name;

        File file = new File(mfileDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        File filemp4 = new File(mfileDir + mVideoOutName);
        if (filemp4.exists()) {
            filemp4.delete();
        }

//        mHandlerThread = new HandlerThread("VideoEncode");
//        mHandlerThread.start();
//        mThreadHandler = new Handler(mHandlerThread.getLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                int what = msg.what;
//                if (what == 0) {
////                    try {
////                        //VideoEncodePrepare();
////                        doEncodeVideoFromBuffer(mediaCodec, encoderColorFormat);
////                    } catch (Exception ex) {
////                        ex.printStackTrace();
////                        Log.i("bbb", "Exception ");
//////                        stopRecord();
////                    }
//                }
//            }
//        };

        mMainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                if (what == 0) {
                    m_callback.onEncodeOneFrame();
                }

            }
        };

    }

    public void setVideoInfo(int width, int height, int framecount, int bitrate)
    {
        this.mWidth = width;
        this.mHeight = height;
        this.NUMFRAMES = framecount;
        this.BIT_RATE = bitrate;
    }

    public void excuate(EncodeVideoCallback callback) {
        this.m_callback = callback;
        try {
            //VideoEncodePrepare();
            doEncodeVideoFromBuffer(mediaCodec, encoderColorFormat);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("bbb", "Exception ");
//                        stopRecord();
        }
        //mThreadHandler.sendEmptyMessage(0);
    }

    public void stopRecord() {

        mediaCodec.stop();
        mediaCodec.release();

        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }

    private static int yuvqueuesize = 10;

    public ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(yuvqueuesize);

    public LinkedBlockingDeque<Long> ptsQueue = new LinkedBlockingDeque<>();

    final Object LOCK = new Object();

    public void putYUVData(byte[] buffer, float pts) {
        if(buffer != null)
        {
            if (YUVQueue.size() >= 10) {
                ptsQueue.poll();
                YUVQueue.poll();
                this.NUMFRAMES--;
            }

            long ptsTemp = (long) (pts * 1000 * 1000);
            ptsQueue.add(ptsTemp);
            YUVQueue.add(buffer);
        }
    }

    public void VideoEncodePrepare() {
        String outputPath = mfileDir + mVideoOutName;

        mBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.

        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

        mediaCodec = null;

        try {
            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);

            MediaCodecInfo info = mediaCodec.getCodecInfo();
            String[] supportTypeinfo = info.getSupportedTypes();
            isSupportNV12 = false;
            for(int i=0 ; i<supportTypeinfo.length; i++)
            {
                int[] supportColorFormat = info.getCapabilitiesForType(supportTypeinfo[i]).colorFormats;
                for(int j=0; j<supportColorFormat.length; j++)
                {
                    if (supportColorFormat[j] == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar) {
                        isSupportNV12 = true;
                    }
                    if (supportColorFormat[j] == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
                        isSupportYUV420Plannar = true;
                    }
                }
            }
            if(isSupportNV12 == true)
                encoderColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
            else if(isSupportYUV420Plannar == true)
                encoderColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
            else
                encoderColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar;

            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, encoderColorFormat);  //设置编码接收的格式

            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();

            mMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        } catch (IOException ioe) {
            throw new RuntimeException("failed init encoder", ioe);
        }

        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    public void close() {

//        mHandlerThread.quit();
//        mHandlerThread.interrupt();
    }


    private void generateFrame(int frameIndex, int colorFormat, byte[] frameData) {
        final int HALF_WIDTH = mWidth / 2;
        boolean semiPlanar = isSemiPlanarYUV(colorFormat);
        // Set to zero.  In YUV this is a dull green.
        Arrays.fill(frameData, (byte) 0);
        int startX, startY, countX, countY;
        frameIndex %= 8;
        //frameIndex = (frameIndex / 8) % 8;    // use this instead for debug -- easier to see
        if (frameIndex < 4) {
            startX = frameIndex * (mWidth / 4);
            startY = 0;
        } else {
            startX = (7 - frameIndex) * (mWidth / 4);
            startY = mHeight / 2;
        }
        for (int y = startY + (mHeight / 2) - 1; y >= startY; --y) {
            for (int x = startX + (mWidth / 4) - 1; x >= startX; --x) {
                if (semiPlanar) {
                    // full-size Y, followed by UV pairs at half resolution
                    // e.g. Nexus 4 OMX.qcom.video.encoder.avc COLOR_FormatYUV420SemiPlanar
                    // e.g. Galaxy Nexus OMX.TI.DUCATI1.VIDEO.H264E
                    //        OMX_TI_COLOR_FormatYUV420PackedSemiPlanar
                    frameData[y * mWidth + x] = (byte) TEST_Y;
                    if ((x & 0x01) == 0 && (y & 0x01) == 0) {
                        frameData[mWidth * mHeight + y * HALF_WIDTH + x] = (byte) TEST_U;
                        frameData[mWidth * mHeight + y * HALF_WIDTH + x + 1] = (byte) TEST_V;
                    }
                } else {
                    // full-size Y, followed by quarter-size U and quarter-size V
                    // e.g. Nexus 10 OMX.Exynos.AVC.Encoder COLOR_FormatYUV420Planar
                    // e.g. Nexus 7 OMX.Nvidia.h264.encoder COLOR_FormatYUV420Planar
                    frameData[y * mWidth + x] = (byte) TEST_Y;
                    if ((x & 0x01) == 0 && (y & 0x01) == 0) {
                        frameData[mWidth * mHeight + (y / 2) * HALF_WIDTH + (x / 2)] = (byte) TEST_U;
                        frameData[mWidth * mHeight + HALF_WIDTH * (mHeight / 2) +
                                (y / 2) * HALF_WIDTH + (x / 2)] = (byte) TEST_V;
                    }
                }
            }
        }
    }


    private static boolean isSemiPlanarYUV(int colorFormat) {
        switch (colorFormat) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                return false;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                return true;
            default:
                throw new RuntimeException("unknown format " + colorFormat);
        }
    }

    public byte[] swapYV12toI420(byte[] yv12bytes, int width, int height) {
        byte[] i420bytes = new byte[yv12bytes.length];
        for (int i = 0; i < width * height; i++)
            i420bytes[i] = yv12bytes[i];
        for (int i = width * height; i < width * height + (width / 2 * height / 2); i++)
            i420bytes[i] = yv12bytes[i + (width / 2 * height / 2)];
        for (int i = width * height + (width / 2 * height / 2); i < width * height + 2 * (width / 2 * height / 2); i++)
            i420bytes[i] = yv12bytes[i - (width / 2 * height / 2)];
        return i420bytes;
    }

    void swapYV12toNV21(byte[] yv12bytes, byte[] nv12bytes, int width, int height) {

        int nLenY = width * height;

        int nLenU = nLenY / 4;

        System.arraycopy(yv12bytes, 0, nv12bytes, 0, width * height);

        for (int i = 0; i < nLenU; i++) {

            nv12bytes[nLenY + 2 * i + 1] = yv12bytes[nLenY + i];

            nv12bytes[nLenY + 2 * i] = yv12bytes[nLenY + nLenU + i];

        }

    }

    private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
        if (nv21 == null || nv12 == null) return;
        int framesize = width * height;
        int i = 0, j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (i = 0; i < framesize; i++) {
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j - 1] = nv21[j + framesize];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j] = nv21[j + framesize - 1];
        }
    }

    private void doEncodeVideoFromBuffer(MediaCodec encoder, int encoderColorFormat) {

        ByteBuffer[] encoderInputBuffers = encoder.getInputBuffers();
        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        int generateIndex = 0;
        int checkIndex = 0;
        int badFrames = 0;


        // The size of a frame of video data, in the formats we handle, is stride*sliceHeight
        // for Y, and (stride/2)*(sliceHeight/2) for each of the Cb and Cr channels.  Application
        // of algebra and assuming that stride==width and sliceHeight==height yields:
        byte[] frameData = new byte[mWidth * mHeight * 3 / 2];
        byte[] frameDataYV12 = new byte[mWidth * mHeight * 3 / 2];
        // Just out of curiosity.
        long rawSize = 0;
        long encodedSize = 0;
        // Save a copy to disk.  Useful for debugging the test.  Note this is a raw elementary
        // stream, not a .mp4 file, so not all players will know what to do with it.


        // Loop until the output side is done.
        boolean inputDone = false;
        boolean encoderDone = false;
        boolean outputDone = false;
        this.isTailer = false;

        m_callback.onStartEncode();

        while (!outputDone) {
            if (YUVQueue.size() == 0 && generateIndex < NUMFRAMES) { //假如队列空了，通知要获取数据
                if(this.isTailer == true)
                    NUMFRAMES--;
                continue;
            }
            // If we're not done submitting frames, generate a new one and submit it.  By
            // doing this on every loop we're working to ensure that the encoder always has
            // work to do.
            //
            // We don't really want a timeout here, but sometimes there's a delay opening
            // the encoder device, so a short timeout can keep us from spinning hard.
            if (!inputDone) {
                int inputBufIndex = encoder.dequeueInputBuffer(TIMEOUT_USEC);

                if (inputBufIndex >= 0) {
                    long ptsUsec = computePresentationTime(generateIndex);

                    if (generateIndex >= NUMFRAMES) {
                        // Send an empty frame with the end-of-stream flag set.  If we set EOS
                        // on a frame with data, that frame data will be ignored, and the
                        // output will be short one frame.
                        encoder.queueInputBuffer(inputBufIndex, 0, 0, ptsUsec,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                        if (VERBOSE) Log.d(TAG, "sent input EOS (with zero-length frame)");

                    } else {

                        if (YUVQueue.size() > 0) {
                            frameData = YUVQueue.poll();

                            ByteBuffer inputBuf = encoderInputBuffers[inputBufIndex];
                            // the buffer should be sized to hold one full frame

                            inputBuf.clear();
                            inputBuf.put(frameData);
                            encoder.queueInputBuffer(inputBufIndex, 0, frameData.length, ptsUsec, 0);
                            if (VERBOSE) Log.d(TAG, "submitted frame " + generateIndex + " to enc");

                            if(generateIndex < NUMFRAMES-1)
                                mMainHandler.sendEmptyMessage(0);
                        }else {
                            generateIndex = NUMFRAMES;
                            continue;
                        }

                    }
                    generateIndex++;
//                    Log.d("bbb", " generateIndex  ==  " + generateIndex);

                } else {
                    // either all in use, or we timed out during initial setup
                    if (VERBOSE) Log.d(TAG, "input buffer not available");
                }
            }
            // Check for output from the encoder.  If there's no output yet, we either need to
            // provide more input, or we need to wait for the encoder to work its magic.  We
            // can't actually tell which is the case, so if we can't get an output buffer right
            // away we loop around and see if it wants more input.
            //
            // Once we get EOS from the encoder, we don't need to do this anymore.
            if (!encoderDone) {
                int encoderStatus = encoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    if (VERBOSE) Log.d(TAG, "no output from encoder available");
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // not expected for an encoder
                    encoderOutputBuffers = encoder.getOutputBuffers();
                    if (VERBOSE) Log.d(TAG, "encoder output buffers changed");
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // not expected for an encoder
                    MediaFormat newFormat = encoder.getOutputFormat();
                    if (VERBOSE) Log.d(TAG, "encoder output format changed: " + newFormat);

                    if (mMuxerStarted) {
                        throw new RuntimeException("format changed twice");
                    }

                    Log.d(TAG, "encoder output format changed: " + newFormat);

                    // now that we have the Magic Goodies, start the muxer
                    mTrackIndex = mMuxer.addTrack(newFormat);
                    mMuxer.start();
                    mMuxerStarted = true;

                } else if (encoderStatus < 0) {
                    Log.d(TAG, "unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
                } else { // encoderStatus >= 0
                    ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                    if (encodedData == null) {
                        Log.d(TAG, "encoderOutputBuffer " + encoderStatus + " was null");
                    }

                    if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        // Codec config info.  Only expected on first packet.  One way to
                        // handle this is to manually stuff the data into the MediaFormat
                        // and pass that to configure().  We do that here to exercise the API.

                        MediaFormat format =
                                MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
                        format.setByteBuffer("csd-0", encodedData);

                        info.size = 0;

                    } else {
                        // Get a decoder input buffer, blocking until it's available.

                        encoderDone = (info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;

                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                            outputDone = true;
                        if (VERBOSE) Log.d(TAG, "passed " + info.size + " bytes to decoder"
                                + (encoderDone ? " (EOS)" : ""));
                    }


                    // It's usually necessary to adjust the ByteBuffer values to match BufferInfo.

                    if (info.size != 0 && ptsQueue.size() > 0) {

                        encodedData.position(info.offset);
                        encodedData.limit(info.offset + info.size);
                        info.presentationTimeUs = ptsQueue.poll();
                        Log.i("abc","pts = " + info.presentationTimeUs);
                        mMuxer.writeSampleData(mTrackIndex, encodedData, info);

                        encodedSize += info.size;

                    }

                    encoder.releaseOutputBuffer(encoderStatus, false);
                }
            }

        }

        if (outputDone) {
            stopRecord();
//            NativeUtils.endDecodeFrameBySeekTime();

            prevOutputPTSUs = 0;
            m_callback.onEncodeFinish();
        }

        if (VERBOSE) Log.d(TAG, "decoded " + checkIndex + " frames at "
                + mWidth + "x" + mHeight + ": raw=" + rawSize + ", enc=" + encodedSize);

        if (checkIndex != NUMFRAMES) {
            Log.d(TAG, "expected " + 120 + " frames, only decoded " + checkIndex);
        }
        if (badFrames != 0) {
            Log.d(TAG, "Found " + badFrames + " bad frames");
        }
    }

    public static long computePresentationTime(int frameIndex) {
        return 132 + frameIndex * 1000000 / FRAME_RATE;
    }

    public long prevOutputPTSUs = 0;

    protected long getPTSUs() {
        if (prevOutputPTSUs == 0) {
            prevOutputPTSUs = System.nanoTime() / 1000L; // 微妙

        } else {
            prevOutputPTSUs = prevOutputPTSUs + (1000000L / 30);
        }
        return prevOutputPTSUs;
    }

}