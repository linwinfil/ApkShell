package com.example.opengl.gl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.example.opengl.gl.utils.GlMatrixTools;
import com.example.opengl.gl.utils.GlUtils;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * @author lmx
 * Created by lmx on 2019/5/8.
 */
public abstract class AFilter implements IFilter
{
    protected Context mContext;

    protected GlMatrixTools mGlMatrixTools;

    //着色器语句
    protected final String mVertexShader;
    protected final String mFragmentShader;

    //着色器句柄
    protected int mVertexShaderHandle;
    protected int mFragmentShaderHandle;

    //执行句柄
    protected int mProgramHandle;
    //顶点坐标句柄
    protected int mPositionHandle;
    //纹理坐标句柄
    protected int mCoordinateHandle;
    //变换矩阵句柄
    protected int mMatrixHandle;
    //当前纹理句柄
    protected int mTextureHandle;


    public AFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        this.mContext = mContext;
        this.mVertexShader = mVertexShader;
        this.mFragmentShader = mFragmentShader;
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig)
    {
        if (GLES20.glIsProgram(mProgramHandle))
        {
            if (GLES20.glIsShader(mVertexShaderHandle)) {
                GLES20.glDetachShader(mProgramHandle, mVertexShaderHandle);
                GLES20.glDeleteShader(mVertexShaderHandle);
                mVertexShaderHandle = 0;
            }

            if (GLES20.glIsShader(mFragmentShaderHandle)) {
                GLES20.glDetachShader(mProgramHandle, mFragmentShaderHandle);
                GLES20.glDeleteShader(mFragmentShaderHandle);
                mFragmentShaderHandle = 0;
            }

            GLES20.glDeleteProgram(mProgramHandle);
            mProgramHandle = 0;
        }

        mVertexShaderHandle = GlUtils.loadShader(mVertexShader, GLES20.GL_VERTEX_SHADER);
        mFragmentShaderHandle = GlUtils.loadShader(mFragmentShader, GLES20.GL_FRAGMENT_SHADER);
        mProgramHandle = GlUtils.loadProgram(mVertexShaderHandle, mFragmentShaderHandle);
        onInitProgram();
    }

    @Override
    public void onSurfaceChanged(int width, int height)
    {

    }

    @Override
    public void onDrawFrame(int textureId, float[] mvpMatrix, float[] texMatrix)
    {
        //清屏颜色与深度
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //加载程序句柄
        GLES20.glUseProgram(mProgramHandle);

        //给视图矩阵赋值
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mvpMatrix, 0);

        //启用顶点坐标句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //启用纹理坐标句柄
        GLES20.glEnableVertexAttribArray(mCoordinateHandle);

        //给纹理单元分配一个默认值
        GLES20.glUniform1i(mTextureHandle, 0);

    }


    public void onInitProgram() {
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "vPosition");
        mCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "vCoordinate");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "vMatrix");
        mTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, "vTexture");
    }


}
