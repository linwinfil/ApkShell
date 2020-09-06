package com.maoxin.apkshell.camera;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

/**
 * @author lmx
 * Created by lmx on 2019/5/8.
 */
public class GlUtils
{
    public static final float TEXTURE_NO_ROTATION[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    public static final float TEXTURE_ROTATED_90[] = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };
    public static final float TEXTURE_ROTATED_180[] = {
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
    };
    public static final float TEXTURE_ROTATED_270[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    private static final String TAG = "GlUtils";

    public static final int NO_TEXTURE = -1;

    public static int createTextureOES()
    {
        int textureType = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        checkGlError("glGenTextures");
        GLES20.glBindTexture(textureType, textures[0]);
        checkGlError("glBindTexture " + textures[0]);
        GLES20.glTexParameterf(textureType, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(textureType, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(textureType, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(textureType, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        checkGlError("glTexParameter");
        return textures[0];
    }

    public static int createTexture(Bitmap bitmap)
    {
        int[] textureId = new int[1];
        if (bitmap != null && !bitmap.isRecycled())
        {
            //生成纹理
            GLES20.glGenTextures(1, textureId, 0);
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);

            //纹理环绕方式
            {
                //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
                //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            }

            //纹理过滤
            {
                //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
                //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            }

            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            GlUtils.checkGlError("glGenTextures");
            return textureId[0];
        }
        return NO_TEXTURE;
    }

    public static String loadShaderRawResource(@NonNull Context context, @RawRes int resourceId)
    {
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String nextLine;
        final StringBuilder body = new StringBuilder();
        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }

        return body.toString();
    }

    public static int loadProgram(int vertexShader, int fragmentShader)
    {
        if (vertexShader == 0 || fragmentShader == 0) {
            Log.e(TAG, "GlUtils --> loadProgram: vertex or fragment load failed");
        }

        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        //检查状态
        int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] <= 0) {
            Log.e(TAG, "loadProgram: link program failed");
            return 0;
        }
        GlUtils.checkGlError("load program");
        return program;
    }

    public static int loadProgram(@NonNull String vertexShader, @NonNull String fragmentShader)
    {
        int vertex = loadShader(vertexShader, GLES20.GL_VERTEX_SHADER);
        if (vertex == 0) {
            Log.e(TAG, "GlUtils --> loadProgram: vertex load failed");
            return 0;
        }
        int fragment = loadShader(fragmentShader, GLES20.GL_FRAGMENT_SHADER);
        if (fragment == 0) {
            Log.e(TAG, "GlUtils --> loadProgram: fragment load failed");
            return 0;
        }

        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertex);
        GLES20.glAttachShader(program, fragment);
        GLES20.glLinkProgram(program);

        //检查状态
        int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] <= 0) {
            Log.e(TAG, "loadProgram: link program failed");
            return 0;
        }
        GLES20.glDeleteShader(vertex);
        GLES20.glDeleteShader(fragment);
        GlUtils.checkGlError("load program");
        return program;
    }


    public static int loadShader(final String strSource, final int iType)
    {
        if (TextUtils.isEmpty(strSource)) return 0;

        //创建一个着色器
        int iShader = GLES20.glCreateShader(iType);
        //加载着色器内容
        GLES20.glShaderSource(iShader, strSource);
        //绑定着色器
        GLES20.glCompileShader(iShader);
        //检测着色器状态
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0)
        {
            Log.e("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }

    /**
     * 检查是否出错
     *
     * @param op op
     */
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }

    public static int getGlSupportVersionInt(Context context)
    {
        return (int) getGlSupportVersion(context);
    }

    public static float getGlSupportVersion(Context context)
    {
        float version = 0;
        if (context != null)
        {
            final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null)
            {
                final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
                if (configurationInfo != null)
                {
                    System.out.println(configurationInfo.toString());
                    version = Float.parseFloat(configurationInfo.getGlEsVersion());
                }
            }
        }
        return version;
    }
}
