package cn.poco.video;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by admin on 2017/4/17.
 */

public class ChangeVideoResolutionAndFramerate
{
    private VideoEncode mEncoder = null;
    File newFile = null;
    RandomAccessFile raf = null;
    int framenum = 0;           //总共编码多少帧
    int m_framerate = 30;          //帧率设定
    int m_bitrate = 0;
    byte[] framedata = null;      //存放帧的缓存
    int buffersize = 0;
    int m_width = 0;            //视频宽
    int m_height = 0;           //视频高
    float m_startTime = -1;
    float m_endTime = -1;
    int m_videoindex = 0;
    String m_tempdiretory = ""; //临时文件夹路径
    String m_videopath = "";  //输入文件名
    String m_videoout = ""; //输出文件名
    TaskFinishCallback m_callback = null;
    int haveDecodeCount = 0;
//    public void changeVideoResolution(String videopath, String tempdiretory, String videoout, int dstWidth, int dstHeight, TaskFinishCallback callback)
//    {
//        m_callback = callback;
//        m_tempdiretory = tempdiretory;
//        m_videoout = videoout;
//        NativeUtils.changeVideoResolutionAndGetYUV(dstWidth, dstHeight, videopath, tempdiretory, "yuv"+(1) +".yuv");
//        m_width = dstWidth;
//        m_height = dstHeight;
//
//        mEncoder = new VideoEncode(tempdiretory, videoout);
//        framenum = NativeUtils.getFrameNumFromFile("/sdcard/android-ffmpeg-tutorial01/1.mp4");
//        framerate = (int)NativeUtils.getFPSFromFile(videopath);
//        mEncoder.setVideoInfo(m_width, m_height, framenum);
//        try {
//            framedata = getOneFrameFromYUV("/sdcard/android-ffmpeg-tutorial01/yuv1.yuv", m_width, m_height, currentframe_index);
//            if(framedata == null)
//            {
//                Log.i("bbb", "读取YUV文件数据失败");
//                m_callback.onEncodeError(-1);
//                return;
//            }
//            mEncoder.putYUVData(framedata, (float)currentframe_index/framerate);
//            ++currentframe_index;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mEncoder.excuate(new EncodeVideoCallback() {
//            @Override
//            public void onEncodeFinish() {
//                Log.d("bbb", "encode finish!");
//                closeYUVReader();
//
//                m_callback.onEncodeSuccess(m_tempdiretory+m_videoout);
//            }
//
//            @Override
//            public void onEncodeOneFrame() {
//                if(currentframe_index > framenum-1)
//                {
//                    VideoEncode.isTailer = true;
//                    VideoEncode.NUMFRAMES--;
//                    return;
//                }
//                try {
//                    framedata = getOneFrameFromYUV("/sdcard/android-ffmpeg-tutorial01/yuv1.yuv", m_width, m_height, currentframe_index);
//                    if(framedata == null)
//                    {
//                        VideoEncode.isTailer = true;
//                        VideoEncode.NUMFRAMES--;
//                        return;
//                    }
//                    mEncoder.putYUVData(framedata, (float)currentframe_index/framerate);
//                    ++currentframe_index;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onStartEncode() {
//
//            }
//        });
//    }

    public void changeVideoResolution(String videopath, String tempdiretory, String videoout, int dstWidth, int dstHeight, int bitrate, int framerate, int videoindex, TaskFinishCallback callback)
    {
       // CamcorderProfile.
        m_bitrate = bitrate;  //码率
        m_callback = callback;
        m_tempdiretory = tempdiretory;
        m_videoout = videoout;
        m_videopath = videopath;
        m_videoindex = videoindex;
        Integer fix_dstWidth = new Integer(dstWidth);
        Integer fix_dstHeight = new Integer(dstHeight);
        buffersize = NativeUtils.getBufferSizeBySelf(fix_dstWidth,fix_dstHeight);
        Log.i("wangjiasheng info", "fixed width = "+fix_dstWidth.intValue() + "   fixed height = "+fix_dstHeight.intValue());
//        buffersize = NativeUtils.getBufferSizeBySelf(dstWidth,dstHeight);
        framedata = new byte[buffersize];

        m_width = fix_dstWidth.intValue();
        m_height = fix_dstHeight.intValue();

        mEncoder = new VideoEncode(tempdiretory, videoout);
        framenum = NativeUtils.getFrameNumFromFile(videopath);
        m_framerate = framerate;//(int)NativeUtils.getFPSFromFile(videopath);
        mEncoder.setVideoInfo(m_width, m_height, framenum, m_bitrate);
        mEncoder.VideoEncodePrepare();  //初始化编码器完毕！

        if(mEncoder.isSupportNV12 == true)
           NativeUtils.getNextFrameYUVFromFile(videopath, videoindex, m_width, m_height, framedata);
        else
            NativeUtils.getNextFrameYUV420PFromFile(videopath, videoindex, m_width, m_height, framedata);

        mEncoder.putYUVData(framedata, (float)haveDecodeCount/framerate);
        ++haveDecodeCount;

        mEncoder.excuate(new EncodeVideoCallback() {
            @Override
            public void onEncodeFinish() {
                Log.d("bbb", "encode finish!");
                mEncoder.close();
                m_callback.onEncodeSuccess(m_tempdiretory+m_videoout);
                haveDecodeCount = 0;
                mEncoder.isTailer = false;
            }

            @Override
            public void onEncodeOneFrame() {
                if(haveDecodeCount < mEncoder.NUMFRAMES)
                {
                    int length = -1;
                    if(!mEncoder.isTailer)
                    {
                        if(mEncoder.isSupportNV12 == true)
                            length = NativeUtils.getNextFrameYUVFromFile(m_videopath, m_videoindex, m_width, m_height,  framedata);
                        else
                            length = NativeUtils.getNextFrameYUV420PFromFile(m_videopath, m_videoindex, m_width, m_height, framedata);
                    }
                    if (length == -1)
                    {
                            mEncoder.NUMFRAMES--;
                            mEncoder.isTailer = true; //告诉编码器  ，  解码已到末尾

                     }
                     else
                    {
                            mEncoder.putYUVData(framedata, (float)haveDecodeCount/m_framerate);
                    }
                    haveDecodeCount++;
                }
            }

            @Override
            public void onStartEncode() {

            }
        });
    }

    public void clipVideoAndChangeResolution(String videopath, String tempdiretory, String videoout, int dstWidth, int dstHeight, int bitrate, int framerate, int videoindex, float startTime, float endTime, TaskFinishCallback callback)
    {
        // CamcorderProfile.
        m_startTime = startTime;
        m_endTime = endTime;
        m_bitrate = bitrate;  //码率
        m_callback = callback;
        m_tempdiretory = tempdiretory;
        m_videoout = videoout;
        m_videopath = videopath;
        m_videoindex = videoindex;
        Integer fix_dstWidth = new Integer(dstWidth);
        Integer fix_dstHeight = new Integer(dstHeight);
        buffersize = NativeUtils.getBufferSizeBySelf(fix_dstWidth,fix_dstHeight);
        Log.i("wangjiasheng info", "fixed width = "+fix_dstWidth.intValue() + "   fixed height = "+fix_dstHeight.intValue());
//        buffersize = NativeUtils.getBufferSizeBySelf(dstWidth,dstHeight);
        framedata = new byte[buffersize];

        m_width = fix_dstWidth.intValue();
        m_height = fix_dstHeight.intValue();

        mEncoder = new VideoEncode(tempdiretory, videoout);
        framenum = NativeUtils.getFrameNumFromFile(videopath);
        m_framerate = framerate;//(int)NativeUtils.getFPSFromFile(videopath);
        mEncoder.setVideoInfo(m_width, m_height, framenum, m_bitrate);
        mEncoder.VideoEncodePrepare();  //初始化编码器完毕！

        if(mEncoder.isSupportNV12 == true)
            NativeUtils.getNextFrameYUVFromFileByTime(videopath, videoindex, m_width, m_height, m_startTime, m_endTime, framedata);
        else
            NativeUtils.getNextFrameYUV420PFromFileByTime(videopath, videoindex, m_width, m_height, m_startTime, m_endTime, framedata);

        mEncoder.putYUVData(framedata, (float)haveDecodeCount/framerate);
        ++haveDecodeCount;

        mEncoder.excuate(new EncodeVideoCallback() {
            @Override
            public void onEncodeFinish() {
                Log.d("bbb", "encode finish!");
                mEncoder.close();
                m_callback.onEncodeSuccess(m_tempdiretory+m_videoout);
                haveDecodeCount = 0;
                mEncoder.isTailer = false;
            }

            @Override
            public void onEncodeOneFrame() {
                if(haveDecodeCount < mEncoder.NUMFRAMES)
                {
                    int length = -1;
                    if(!mEncoder.isTailer)
                    {
                        if(mEncoder.isSupportNV12 == true)
                            length = NativeUtils.getNextFrameYUVFromFileByTime(m_videopath, m_videoindex, m_width, m_height, m_startTime, m_endTime, framedata);
                        else
                            length = NativeUtils.getNextFrameYUV420PFromFileByTime(m_videopath, m_videoindex, m_width, m_height, m_startTime, m_endTime, framedata);
                    }
                    if (length == -1)
                    {
                        mEncoder.NUMFRAMES--;
                        mEncoder.isTailer = true; //告诉编码器  ，  解码已到末尾

                    }
                    else
                    {
                        mEncoder.putYUVData(framedata, (float)haveDecodeCount/m_framerate);
                    }
                    haveDecodeCount++;
                }
            }

            @Override
            public void onStartEncode() {

            }
        });
    }

    private byte[] getOneFrameFromYUV(String yuv, int width, int height, int frameindex) throws IOException {
        int framesize = width * height * 3 / 2;
        byte[] result = new byte[framesize];
        int readnum = 0;

        if(newFile == null)
        {
            newFile = new File(yuv);
            //文件大小
            long fileSize = newFile.length();

            raf = new RandomAccessFile(newFile, "r");
            raf.seek(frameindex * framesize);
            int ret;
            ret = raf.read(result, 0, framesize);
            if (ret < 0) {
                Log.i("bbb", "文件读取完毕");
                raf.close();
                raf = null;
            } else
                return result;
        }
        else
        {
            raf.seek(frameindex * framesize);
            int ret;
            ret = raf.read(result, 0, framesize);
            if (ret < 0) {
                Log.i("bbb", "文件读取完毕");
                raf.close();
                raf = null;
            } else
                return result;
        }

        return null;
    }
    private void closeYUVReader()
    {
        try {
            raf.close();
            raf = null;
            newFile = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
