package com.example.opengl.gl.utils;

import android.opengl.GLES20;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * @author lmx
 * Created by lmx on 2019/5/8.
 */
public class GlUtils
{
    private static final String TAG = "GlUtils";

    public static int loadProgram(@NonNull String vertexShader, @NonNull String fragmentShader)
    {
        int vertex = loadShader(vertexShader, GLES20.GL_SHADER_TYPE);
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


    private static int loadShader(final String strSource, final int iType)
    {
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
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

}
