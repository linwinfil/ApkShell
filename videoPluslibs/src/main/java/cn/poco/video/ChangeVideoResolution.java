package cn.poco.video;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Created by admin on 2017/4/17.
 */

public class ChangeVideoResolution
{
    private static final String TAG = "ChangeVideoResolution";
    private boolean DEBUG = true;// TODO set false on release

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
    String m_tempdiretory = ""; //临时文件夹路径
    String m_videopath = "";  //输入文件名
    String m_videoout = ""; //输出文件名
    TaskFinishCallback m_callback = null;
    int haveDecodeCount = 0;
    private float m_startTime;
    private float m_endTime;
    private int m_framerate;

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

    public void changeVideoResolution(String videopath, String tempdiretory, String videoout, int dstWidth, int dstHeight, int bitrate, int videoindex, TaskFinishCallback callback)
    {
        // CamcorderProfile.
        Camera.Size size = adjustResolution(dstWidth, dstHeight);
        m_bitrate = bitrate;  //码率
        m_callback = callback;
        m_tempdiretory = tempdiretory;
        m_videoout = videoout;
        m_videopath = videopath;
        m_videoindex = videoindex;
        Integer fix_dstWidth = new Integer(size.width);
        Integer fix_dstHeight = new Integer(size.height);
        buffersize = NativeUtils.getBufferSizeBySelf(fix_dstWidth,fix_dstHeight);
        Log.i("wangjiasheng info", "fixed width = "+fix_dstWidth.intValue() + "   fixed height = "+fix_dstHeight.intValue());
//        buffersize = NativeUtils.getBufferSizeBySelf(dstWidth,dstHeight);
        framedata = new byte[buffersize];

        m_width = fix_dstWidth.intValue();
        m_height = fix_dstHeight.intValue();

        mEncoder = new VideoEncode(tempdiretory, videoout);
        framenum = NativeUtils.getFrameNumFromFile2(videopath);
        framerate = (int)NativeUtils.getFPSFromFile(videopath);
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
                        if(mEncoder.isSupportNV12 == true) {
                            length = NativeUtils.getNextFrameYUVFromFile(m_videopath, m_videoindex, m_width, m_height, framedata);
                            Log.i("test",String.valueOf(length));
                        }
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
                        mEncoder.putYUVData(framedata, (float)haveDecodeCount/framerate);
                    }
                    haveDecodeCount++;
                }
            }

            @Override
            public void onStartEncode() {

            }
        });
    }


    private int  index = 1;
    private int copyCount = 1;
    private int frameCount = 1;

    public void changeVideoResolutionForJane(Context context, String videopath, String tempdiretory, String videoout, int
            dstWidth, int dstHeight, int bitrate, int videoindex, TaskFinishCallback callback,
                                      final VideoResolutionCallBack resolutionCallBack) {
        // CamcorderProfile.

        m_bitrate = bitrate;  //码率
        m_callback = callback;
        m_tempdiretory = tempdiretory;
        m_videoout = videoout;
        m_videopath = videopath;
        m_videoindex = videoindex;

        Size size= GetSupportedVideoSizesHelper.getRecordVideoParamSize(context,dstWidth,dstHeight);
        Log.d(TAG,"MyFrames Msg : "+"sizie width"+size.width+" sizeH:"+size.height);
        int temp_width = size.width;
        int temp_height = size.height;


        float sizeRadio = (float)dstWidth / (float)dstHeight;
        float tmpSizeRadio = (float)temp_width/(float)temp_height;


        if(dstWidth > 640)
        {
            temp_width = 640;
            temp_height = (int) (temp_width / sizeRadio);
            int mistake = temp_height % 16;
            temp_height = temp_height - mistake;
        }

        if(sizeRadio == 1f)
        {
            temp_height = temp_width;
        }
        else if(tmpSizeRadio != sizeRadio)
        {
            temp_height = (int) (temp_width / sizeRadio);
            int mistake = temp_height % 16;
            temp_height = temp_height - mistake;
        }


        m_width = temp_width;
        m_height = temp_height;
//        buffersize = m_width * m_height * 3 / 2;
        Integer tempwidth = new Integer(m_width);
        Integer tempheight = new Integer(m_height);
        buffersize = NativeUtils.getBufferSizeBySelf(tempwidth,tempheight);
//        m_width = temp_width;
//        m_height = temp_height;
        framedata = new byte[buffersize];
        mEncoder = new VideoEncode(tempdiretory, videoout);

        framenum = NativeUtils.getFrameNumFromFile2(videopath);
        framerate = (int)NativeUtils.getFPSFromFile(videopath);
        mEncoder.setVideoInfo(m_width, m_height, framenum, m_bitrate);
        mEncoder.VideoEncodePrepare();  //初始化编码器完毕！

        if(mEncoder.isSupportNV12 == true){
            NativeUtils.getNextFrameYUVFromFile(videopath, videoindex, m_width, m_height, framedata);
        }else{
            NativeUtils.getNextFrameYUV420PFromFile(videopath, videoindex, m_width, m_height, framedata);
        }

        mEncoder.putYUVData(framedata, (float)haveDecodeCount/framerate);
        ++haveDecodeCount;
        //帧率
        //final int videoFPS = (int) NativeUtils.getFPSFromFile(videopath);
        final float videoFPSf = NativeUtils.getFPSFromFile(videopath);
        final float timef = NativeUtils.getDurationFromFile(videopath);

        float fillFPS = 0 ;
        boolean isAdd = false;

        if (videoFPSf == 25){
            fillFPS = 0;
        }else{
            fillFPS = Math.abs(videoFPSf/(25-videoFPSf));
            if (videoFPSf < 25){
                isAdd = true;
            }
        }
        Log.d("MyFrames Msg", "videoFPSf = " + videoFPSf + "  timef = " + timef+ "  fillFPS = " + fillFPS+ "  isAdd = " + isAdd);
        final float fill_FPS = fillFPS;
        final boolean finalIsAdd = isAdd;

        if (isAdd){
            int fCount = 1;
            int cCount = 1;
            while(fCount <= framenum){
                int pos = (int) Math.ceil(fill_FPS*cCount);

                if (fCount == pos){
                    //若POS==当前帧数，则直接copy当前帧不取下一帧
                    cCount ++ ;
                    if (fCount != framenum){
                        mEncoder.NUMFRAMES ++;
                    }
                    //Log.d("MyFrames GenTotal", "fCount =  " + fCount + " cCount ：" +cCount + " mEncoder.NUMFRAMES : " +mEncoder.NUMFRAMES);
                }else{
                    fCount ++;
                    //Log.d("MyFrames GenTotal", "get success fCount =  " + fCount );
                }
                if (fCount == framenum && fCount == (int) Math.ceil(fill_FPS*cCount) ){
                    mEncoder.NUMFRAMES ++;
                }
            }
            Log.d("MyFrames GenTotal", " mEncoder.NUMFRAMES  " + mEncoder.NUMFRAMES );
        }


        mEncoder.excuate(new EncodeVideoCallback() {
            @Override
            public void onEncodeFinish() {
                Log.d("MyFrames Msg", "encode finish!");
                mEncoder.close();
                m_callback.onEncodeSuccess(m_tempdiretory + m_videoout);
                haveDecodeCount = 0;
                mEncoder.isTailer = false;
            }

            @Override
            public void onEncodeOneFrame() {
                if (!finalIsAdd){
                    if (haveDecodeCount < mEncoder.NUMFRAMES) {

                        int length = -1;
                        boolean canPut = true;
                        //若没到结尾，则每次都get下一帧
                        if (!mEncoder.isTailer) {
                            if (mEncoder.isSupportNV12 == true) {
                                length = NativeUtils.getNextFrameYUVFromFile(m_videopath, m_videoindex, m_width, m_height, framedata);
                            } else {
                                length = NativeUtils.getNextFrameYUV420PFromFile(m_videopath, m_videoindex, m_width, m_height, framedata);
                            }
                        }
                        if (length == -1) {
                            //若没有拿到下一帧，则结束编解码
                            Log.d("MyFrames Extract", "END " );
                            mEncoder.NUMFRAMES--;
                            mEncoder.isTailer = true; //告诉编码器  ，  解码已到末尾
                        } else {
                            //若成功拿到下一帧，则累加帧数
                            frameCount++;
                            int pos = (int) Math.ceil( fill_FPS * copyCount);//eg:30/(30-25)=6 * 1、2、3...n

                            //首先get当前帧 ，而后判断当前帧是否被丢弃 ，==POS则被丢弃不予put
                            if (frameCount == pos){
                                Log.d("MyFrames Extract", "framecount =  " + frameCount + " copyCount ：" +copyCount + " mEncoder.NUMFRAMES : " +mEncoder.NUMFRAMES);
                                copyCount ++ ;//累加被抽走的帧数，从而预知下个需要被丢弃的帧数
                                canPut = false;
                            }
                            if (canPut){
                                mEncoder.putYUVData(framedata, (float) haveDecodeCount / framerate);
                                index ++;
                                Log.d("MyFrames Extract", "index = " + index + "  frameCount = " + frameCount);
                                haveDecodeCount++;
                            } else {
                                haveDecodeCount++;
                                onEncodeOneFrame();
                            }
                        }
                    } else {
                        Log.d("MyFrames Extract", "END " );
                        mEncoder.NUMFRAMES--;
                        mEncoder.isTailer = true; //告诉编码器  ，  解码已到末尾
                    }
                } else {
                    if (haveDecodeCount < mEncoder.NUMFRAMES) {
                        if (!mEncoder.isTailer){
                            int length = -1;
                            int pos = (int) Math.ceil(fill_FPS*copyCount);

                            if (frameCount == pos){
                                //若POS==当前帧数，则直接copy当前帧不取下一帧
                                copyCount ++ ;
                                Log.d("MyFrames Insert", "framecount =  " + frameCount + " copyCount ：" +copyCount + " mEncoder.NUMFRAMES : " +mEncoder.NUMFRAMES);
                            }else{
                                //若POS!=当前帧数，则取下一帧
                                if (mEncoder.isSupportNV12 == true) {
                                    length = NativeUtils.getNextFrameYUVFromFile(m_videopath, m_videoindex, m_width, m_height, framedata);
                                } else {
                                    length = NativeUtils.getNextFrameYUV420PFromFile(m_videopath, m_videoindex, m_width, m_height, framedata);
                                }

                                if (length == -1){
                                    //若视频到结尾
                                    Log.d("MyFrames Insert", "END " );
                                    mEncoder.isTailer = true; //告诉编码器  ，  解码已到末尾
                                }else{
                                    //若视频取到了下一帧，累加帧数
                                    frameCount ++;
                                    Log.d("MyFrames Insert", "get success framecount =  " + frameCount );
                                }
                            }
                            if (!mEncoder.isTailer){
                                mEncoder.putYUVData(framedata, (float) haveDecodeCount / framerate);
                                haveDecodeCount++;
                            }
                        }
                    } else {
                        Log.d("MyFrames Insert", "END " );
                        mEncoder.isTailer = true; //告诉编码器  ，  解码已到末尾
                    }
                }
                if (resolutionCallBack != null){
                    if (haveDecodeCount==mEncoder.NUMFRAMES)
                        Log.d("MyFrames Msg", "haveDecodeCount = "+haveDecodeCount + "mEncoder.NUMFRAMES = " +mEncoder.NUMFRAMES);

                    resolutionCallBack.callback((float) haveDecodeCount / mEncoder.NUMFRAMES);
                }
            }

            @Override
            public void onStartEncode() {

            }
        });
    }

    public void clipVideoAndChangeResolution(String videopath, String tempdiretory, String videoout, int dstWidth, int dstHeight, int bitrate, int framerate, int videoindex, float startTime,  float endTime, TaskFinishCallback callback)
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
