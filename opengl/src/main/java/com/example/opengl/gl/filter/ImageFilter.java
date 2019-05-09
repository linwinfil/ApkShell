package com.example.opengl.gl.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.opengl.gl.utils.GlUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * @author lmx
 * Created by lmx on 2019/5/9.
 */
public class ImageFilter extends AFilter
{

    private float[] mProjectMatrix = new float[16];
    private float[] mMvpMatrix = new float[16];
    private int mTextureId = GlUtils.NO_TEXTURE;
    private Bitmap mBitmap;

    protected FloatBuffer mPositionBuffer;
    protected FloatBuffer mCoordinateBuffer;

    //顶点坐标
    protected final float[] positionPoint = {
            -1.0f, 1.0f, //左上角
            -1.0f, -1.0f,//左下角
            1.0f, 1.0f,//右上角
            1.0f, -1.0f//右下角
    };

    //纹理坐标（对应顶点坐标）
    protected final float[] coordinatePoint = {
            0f, 0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f,
    };

    public ImageFilter(Context mContext, String mVertexShader, String mFragmentShader)
    {
        super(mContext, mVertexShader, mFragmentShader);

        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(positionPoint.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(positionPoint);
        floatBuffer.position(0);
        mPositionBuffer = floatBuffer;

        floatBuffer = ByteBuffer.allocateDirect(coordinatePoint.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(coordinatePoint);
        floatBuffer.position(0);
        mCoordinateBuffer = floatBuffer;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    @Override
    public void onSurfaceCreatedInit(EGLConfig eglConfig)
    {
        //清屏颜色与深度
        GLES20.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void onSurfaceChangedInit(int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        if (mBitmap != null && !mBitmap.isRecycled())
        {
            int bw = mBitmap.getWidth();
            int bh = mBitmap.getHeight();
            float bWH = bw * 1f / bh;
            float vWH = width * 1f / height;

            //near < far
            float near = 3.0f;
            float far = 9.0f;
            if (bWH > vWH) {
                Matrix.setIdentityM(mProjectMatrix, 0);
                Matrix.frustumM(mProjectMatrix, 0, -1, 1, - bWH / vWH, bWH / vWH, near, far);
            } else {
                Matrix.setIdentityM(mProjectMatrix, 0);
                Matrix.frustumM(mProjectMatrix, 0, -vWH / bWH, vWH / bWH, - 1, 0, near, far);
            }
            float eyez = near;
            //设置相机位置
            float[] cameraMatrix = new float[16];
            Matrix.setLookAtM(cameraMatrix,
                    0,              //偏移量，一般为0
                    0, 0, eyez,     //相机位置（eyez <= far）
                    0f, 0f, 0f,     //观察点
                    0f, 1.0f, 0.0f  //辅助向上量
            );
            //计算变化矩阵
            Matrix.setIdentityM(mMvpMatrix, 0);
            Matrix.multiplyMM(mMvpMatrix, 0, mProjectMatrix, 0, cameraMatrix, 0);
        }
    }

    @Override
    public void onDraw()
    {
        if (mBitmap == null || mBitmap.isRecycled()) return;

        //清屏颜色与深度
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //加载程序句柄
        GLES20.glUseProgram(mProgramHandle);

        //给视图矩阵赋值
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMvpMatrix, 0);

        //给纹理单元分配一个默认值
        GLES20.glUniform1i(mTextureHandle, 0);

        //启用顶点坐标句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //启用纹理坐标句柄
        GLES20.glEnableVertexAttribArray(mCoordinateHandle);

        //传入顶点坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mPositionBuffer);
        //传如纹理坐标数据
        GLES20.glVertexAttribPointer(mCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0 , mCoordinateBuffer);

        //TODO 绘制模式

    }

    @Override
    public int onCreateProgram(EGLConfig eglConfig)
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
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "vPosition");
        mCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "vCoordinate");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "vMatrix");
        mTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, "vTexture");

        return mProgramHandle;
    }
}