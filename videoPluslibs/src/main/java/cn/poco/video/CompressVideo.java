package cn.poco.video;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Created by admin on 2017/10/30.
 */

public class CompressVideo {
    private VideoEncode mEncoder = null;
    File newFile = null;
    RandomAccessFile raf = null;
    int framenum = 0;           //总共编码多少帧
    int framerate = 30;          //帧率设定
    int m_bitrate = 0;
    byte[] framedata = null;      //存放帧的缓存
    int buffersize = 0;
    int m_width = 0;            //视频宽
    int m_height = 0;           //视频高
    int m_videoindex = 0;
    int m_startTime = 0;
    int m_endTime = 0;
    String m_tempdiretory = ""; //临时文件夹路径
    String m_videopath = "";  //输入文件名
    String m_videoout = ""; //输出文件名
    TaskFinishCallback m_callback = null;
    int haveDecodeCount = 0;

    //修正分辨率为硬编码所支持的分辨率
    private Camera.Size adjustResolution(int width, int height)
    {
        Camera.Size resultsize = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int MinDistance = Integer.MAX_VALUE;
        for(int cameraId=0; cameraId<Camera.getNumberOfCameras(); cameraId++)
        {
            Camera.getCameraInfo( cameraId, cameraInfo);
            Camera camera = Camera.open(cameraId);
            Camera.Parameters params = camera.getParameters();
            camera.release();
            List<Camera.Size> previewSIzes = params.getSupportedVideoSizes();

            int distance = 0;
            for(Camera.Size size:previewSIzes)
            {
                if(width> height)
                    distance = (size.width - width) * (size.width-width) + (size.height-height)* (size.height-height);
                else
                    distance = (size.width - height) *(size.width - height) + (size.height-width)*(size.height-width);
                if(distance<MinDistance)
                {
                    resultsize = size;
                    MinDistance = distance;
                }
            }
        }
        if(height>width)
        {
            int temp = 0;
            temp = resultsize.width;
            resultsize.width = resultsize.height;
            resultsize.height = temp;
        }
        return resultsize;
    }

    /**
     * @param videoin 输入视频路径
     * @param tempdiretory  输出文件夹
     * @param videoout  输出视频文件名
     * @param dstWidth  目标宽
     * @param dstHeight 目标高
     * @param bitrate 目标码率
     * @param fps  目标帧率
     * @param videoindex 底层占用标识符
     * @param framenum 需要编码多少帧
     * @param startTime 需要编码的输入视频的开始时间
     * @param endTime 需要编码的输入视频的结束时间
     * @param callback 回调通知
     */
    public void changeVideoResolution(final String videoin, String tempdiretory, String videoout, int dstWidth, int dstHeight, int bitrate, int fps, final int videoindex, int framenum, int startTime, int endTime, TaskFinishCallback callback)
    {
        // CamcorderProfile.
        Camera.Size size = adjustResolution(dstWidth, dstHeight);
        m_bitrate = bitrate;  //码率
        m_callback = callback;
        m_tempdiretory = tempdiretory;
        m_videoout = videoout;
        m_videoindex = videoindex;
        m_startTime = startTime;
        m_endTime = endTime;
        Integer fix_dstWidth = new Integer(size.width);
        Integer fix_dstHeight = new Integer(size.height);


        m_width = fix_dstWidth.intValue();
        m_height = fix_dstHeight.intValue();

        mEncoder = new VideoEncode(tempdiretory, videoout);
        framerate = fps;
        framedata = new byte[dstWidth*dstHeight*3/2];
        mEncoder.setVideoInfo(dstWidth, dstHeight, framenum, m_bitrate);
        mEncoder.VideoEncodePrepare();  //初始化编码器完毕！
        Bitmap bitmap = NativeUtils.decodeFrameBySeekTime2(videoin, m_startTime, m_endTime, videoindex);
      //  Bitmap bitmap = NativeUtils.decodeFrameBySeekTime2("/sdcard/android-ffmpeg-tutorial01/output2.mp4",(int)(5), 10, 0);
        if(bitmap == null)
            mEncoder.isTailer = true;

        if(bitmap != null) {
            if (mEncoder.isSupportNV12 == true)
                BitmapToYUV.getYUVByBitmap(bitmap, framedata, 2);
            else
                BitmapToYUV.getYUVByBitmap(bitmap, framedata, 1);

            mEncoder.putYUVData(framedata, (float) haveDecodeCount / framerate);
            ++haveDecodeCount;
        }
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
                    if(!mEncoder.isTailer)
                    {
                        Bitmap bitmap = NativeUtils.decodeFrameBySeekTime2(videoin, m_startTime, m_endTime, videoindex);
                        if(bitmap == null)
                            mEncoder.isTailer = true;
                        if(bitmap != null) {
                            if (mEncoder.isSupportNV12 == true)
                                BitmapToYUV.getYUVByBitmap(bitmap, framedata, 2);
                            else
                                BitmapToYUV.getYUVByBitmap(bitmap, framedata, 1);

                            mEncoder.putYUVData(framedata, (float) haveDecodeCount / framerate);
                            ++haveDecodeCount;
                        }
                    }
                }
            }

            @Override
            public void onStartEncode() {

            }
        });
    }
}
