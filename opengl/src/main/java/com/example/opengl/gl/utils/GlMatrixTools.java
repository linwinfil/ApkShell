package com.example.opengl.gl.utils;

import android.opengl.Matrix;

import java.util.Arrays;
import java.util.Stack;

/**
 * @author lmx
 * Created by lmx on 2019/5/9.
 */
public class GlMatrixTools
{
    private float[] mMatrixCamera;      //相机矩阵
    private float[] mMatrixProjection;  //投影矩阵
    private float[] mMatrixCurrent;     //原始矩阵

    private Stack<float[]> mStack;      //变换矩阵堆栈

    public GlMatrixTools()
    {
        mStack = new Stack<>();

        mMatrixCamera = getOpenGLUnitMatrix();
        mMatrixProjection = getOpenGLUnitMatrix();
        mMatrixCurrent = getOpenGLUnitMatrix();
    }

    //保护现场
    public void pushMatrix()
    {
        mStack.push(Arrays.copyOf(mMatrixCurrent, 16));
    }

    //恢复现场
    public void popMatrix()
    {
        mMatrixCurrent = mStack.pop();
    }

    public void clearStack()
    {
        mStack.clear();
    }

    //平移变换
    public void translate(float x, float y, float z)
    {
        Matrix.translateM(mMatrixCurrent, 0, x, y, z);
    }

    //旋转变换
    public void rotate(float angle, float x, float y, float z)
    {
        Matrix.rotateM(mMatrixCurrent, 0, angle, x, y, z);
    }

    //缩放变换
    public void scale(float x, float y, float z)
    {
        Matrix.scaleM(mMatrixCurrent, 0, x, y, z);
    }

    //设置相机
    public void setCamera(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)
    {
        Matrix.setLookAtM(mMatrixCamera, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void frustum(float left, float right, float bottom, float top, float near, float far)
    {
        Matrix.frustumM(mMatrixProjection, 0, left, right, bottom, top, near, far);
    }

    public void ortho(float left, float right, float bottom, float top, float near, float far)
    {
        Matrix.orthoM(mMatrixProjection, 0, left, right, bottom, top, near, far);
    }

    public void reset()
    {
        mMatrixCamera = getOpenGLUnitMatrix();
        mMatrixProjection = getOpenGLUnitMatrix();
        mMatrixCurrent = getOpenGLUnitMatrix();
    }

    public float[] getFinalMatrix()
    {
        float[] ans = new float[16];
        Matrix.multiplyMM(ans, 0, mMatrixCamera, 0, mMatrixCurrent, 0);
        Matrix.multiplyMM(ans, 0, mMatrixProjection, 0, ans, 0);
        return ans;
    }

    /**
     * @return OpenGL 单位矩阵
     * <p>
     * 1,0,0,0,
     * 0,1,0,0,
     * 0,0,1,0,
     * 0,0,0,1
     */
    public float[] getOpenGLUnitMatrix()
    {
        float[] out = new float[16];
        Matrix.setIdentityM(out, 0);
        return out;
    }
}
