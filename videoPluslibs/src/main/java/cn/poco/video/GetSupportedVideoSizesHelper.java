package cn.poco.video;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by poco on 2017/9/14.
 */

public class GetSupportedVideoSizesHelper {
    private static final String TAG = "GetSupportedVideoSizesH";

    //内置视频硬编码所支持的分辨率
    private static List<Size> sSizes = new ArrayList<>();

    static {
        sSizes.add(new Size(320, 320));
        sSizes.add(new Size(480, 480));
        sSizes.add(new Size(640, 640));

    }

    /**
     * 获取视频最佳压缩分辨率
     *
     * @param context
     * @return
     */
    public static Size getRecordVideoParamSize(Context context, int destWidth, int destHeight) {
        //本地没有，读摄像头
        saveRecordVideoParamSize(context);

        SharedPreferences sharedPreferences = context.getSharedPreferences("record_video_params", Context.MODE_PRIVATE);

        //读本地的size
        int width = -1;
        int height = -1;
        List<Size> sizes = new ArrayList<>();
        int i = 0;
        int w = sharedPreferences.getInt("w" + i, -1);
        int h = sharedPreferences.getInt("h" + i, -1);
        while (w != -1 && h != -1) {
            sizes.add(new Size(w, h));
            i++;
            w = sharedPreferences.getInt("w" + i, -1);
            h = sharedPreferences.getInt("h" + i, -1);
        }

        //摄像头没有权限也就本地没有，读默认
        if (sizes.size() == 0) {
            sizes = sSizes;
        }

        //获取最佳视频压缩分辨率
        Size resultsize = null;
        int minDistance = Integer.MAX_VALUE;
        int distance = 0;
        for (Size size : sizes) {
            Log.d(TAG, "sizeW get:" + size.width + "  sizeH get:" + size.height);
            if (destWidth > destHeight)
                distance = (size.width - destWidth) * (size.width - destWidth) + (size.height - destHeight) * (size.height - destHeight);
            else
                distance = (size.width - destHeight) * (size.width - destHeight) + (size.height - destWidth) * (size.height - destWidth);
            if (distance < minDistance) {
                resultsize = size;
                minDistance = distance;
            }
        }

        if (resultsize != null && (destHeight > destWidth)) {
            int temp = resultsize.width;
            resultsize.width = resultsize.height;
            resultsize.height = temp;
        }


        return resultsize;
    }

    /**
     * 获取摄像头支持的分辨率保存到本地
     *
     * @param context
     */
    public static void saveRecordVideoParamSize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("record_video_params", Context.MODE_PRIVATE);
        //本地没有
        if (sharedPreferences.getInt("w0", -1) == -1) {
            try {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
                    long lastTime = System.currentTimeMillis();
                    Camera.getCameraInfo(cameraId, cameraInfo);
                    Camera camera = Camera.open(cameraId);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();
                        camera.release();
                        List<Camera.Size> previewSIzes = params.getSupportedVideoSizes();
                        if (previewSIzes != null) {
                            for (int i = 0; i < previewSIzes.size(); i++) {
                                Camera.Size size = previewSIzes.get(i);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("w" + i, size.width);
                                editor.putInt("h" + i, size.height);
                                editor.commit();
                                Log.d(TAG, "sizeW:" + size.width + "  sizeH:" + size.height);
                            }
                            Log.d(TAG, "currentTimeMillis: " + (System.currentTimeMillis() - lastTime) / 1000);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
