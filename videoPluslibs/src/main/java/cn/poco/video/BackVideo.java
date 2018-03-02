package cn.poco.video;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by admin on 2017/4/17.
 */

public class BackVideo
{
    private VideoEncode mEncoder = null;
    RandomAccessFile raf  = null;   //读文件用
    File newFile = null;         //输出文件
    int currentframe_index = 1;  //当前帧的索引
    int framenum = 0;           //总共编码多少帧
    int framerate = 30;          //帧率设定
    byte[] framedata = null;      //存放帧的缓存
    int m_width = 0;            //视频宽
    int m_height = 0;           //视频高

    public void backVideo(String videopath, String tempdiretory, String videoout, int FrameRate)
    {
        Integer width = new Integer(-1);
        Integer height = new Integer(-1);
        NativeUtils.getVideoToYUVFILE(width, height, videopath, tempdiretory, "yuv"+(1) +".yuv");
        m_width = width;
        m_height = height;

        mEncoder = new VideoEncode(tempdiretory, videoout);
        framenum = NativeUtils.getFrameNumFromFile("/sdcard/android-ffmpeg-tutorial01/1.mp4");
        framerate = FrameRate;
        mEncoder.setVideoInfo(m_width, m_height, framenum, framerate);
        try {
            framedata = getOneFrameFromYUV("/sdcard/android-ffmpeg-tutorial01/yuv1.yuv", m_width, m_height, framenum-currentframe_index);
            if(framedata == null)
            {
                Log.i("bbb", "读取YUV文件数据失败");
                return;
            }
            mEncoder.putYUVData(framedata, (float)currentframe_index/framerate);
            ++currentframe_index;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mEncoder.excuate(new EncodeVideoCallback() {
            @Override
            public void onEncodeFinish() {
                Log.d("bbb", "encode finish!");
                closeYUVReader();
            }

            @Override
            public void onEncodeOneFrame() {
                if(currentframe_index > framenum-1)
                {
                    mEncoder.isTailer = true;
                    mEncoder.NUMFRAMES--;
                    return;
                }
                try {
                    framedata = getOneFrameFromYUV("/sdcard/android-ffmpeg-tutorial01/yuv1.yuv", m_width, m_height, framenum-currentframe_index);
                    if(framedata == null)
                    {
                        mEncoder.isTailer = true;
                        mEncoder.NUMFRAMES--;
                        return;
                    }
                    mEncoder.putYUVData(framedata, (float)currentframe_index/framerate);
                    ++currentframe_index;
                } catch (IOException e) {
                    e.printStackTrace();
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
