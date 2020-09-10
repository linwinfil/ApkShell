package com.maoxin.apkshell.camera;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

/**
 * @author lmx
 * Created by lmx on 2019/5/8.
 */
public class GlUtils {
    public static final float TEXTURE_NO_ROTATION[] = {0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,};

    public static final float TEXTURE_ROTATED_90[] = {1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,};
    public static final float TEXTURE_ROTATED_180[] = {1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,};
    public static final float TEXTURE_ROTATED_270[] = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,};

    private static final String TAG = "GlUtils";

    public static final int NO_TEXTURE = -1;

    public static int createTextureOES()
    {
        int textureType = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        checkGlError("glGenTextures");
        GLES30.glBindTexture(textureType, textures[0]);
        checkGlError("glBindTexture " + textures[0]);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        checkGlError("glTexParameter");
        return textures[0];
    }

    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap bitmap = null;
        AssetManager manager = context.getResources().getAssets();
        try {
            InputStream is = manager.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static int createTextureFromAssets(Context context, String asset) {
        Bitmap bitmap = getImageFromAssetsFile(context, asset);
        int texture = createTexture(bitmap);
        bitmap.recycle();
        return texture;
    }

    public static int createTexture(Bitmap bitmap)
    {
        int[] textureId = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            //生成纹理
            GLES30.glGenTextures(1, textureId, 0);
            //绑定纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0]);

            //纹理环绕方式
            {
                //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            }

            //纹理过滤
            {
                //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            }

            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
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
        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return body.toString();
    }

    public static int loadProgram(int vertexShader, int fragmentShader)
    {
        if (vertexShader == 0 || fragmentShader == 0) {
            Log.e(TAG, "GlUtils --> loadProgram: vertex or fragment load failed");
        }

        int program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);

        //检查状态
        int[] status = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, status, 0);
        if (status[0] <= 0) {
            Log.e(TAG, "loadProgram: link program failed");
            return 0;
        }
        GlUtils.checkGlError("load program");
        return program;
    }

    public static int loadProgram(@NonNull String vertexShader, @NonNull String fragmentShader)
    {
        int vertex = loadShader(vertexShader, GLES30.GL_VERTEX_SHADER);
        if (vertex == 0) {
            Log.e(TAG, "GlUtils --> loadProgram: vertex load failed");
            return 0;
        }
        int fragment = loadShader(fragmentShader, GLES30.GL_FRAGMENT_SHADER);
        if (fragment == 0) {
            Log.e(TAG, "GlUtils --> loadProgram: fragment load failed");
            return 0;
        }

        int program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertex);
        GLES30.glAttachShader(program, fragment);
        GLES30.glLinkProgram(program);

        //检查状态
        int[] status = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, status, 0);
        if (status[0] <= 0) {
            Log.e(TAG, "loadProgram: link program failed");
            return 0;
        }
        GLES30.glDeleteShader(vertex);
        GLES30.glDeleteShader(fragment);
        GlUtils.checkGlError("load program");
        return program;
    }


    public static int loadShader(final String strSource, final int iType)
    {
        if (TextUtils.isEmpty(strSource)) return 0;

        //创建一个着色器
        int iShader = GLES30.glCreateShader(iType);
        //加载着色器内容
        GLES30.glShaderSource(iShader, strSource);
        //绑定着色器
        GLES30.glCompileShader(iShader);
        //检测着色器状态
        int[] compiled = new int[1];
        GLES30.glGetShaderiv(iShader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("Load Shader Failed", "Compilation\n" + GLES30.glGetShaderInfoLog(iShader));
            GLES30.glDeleteShader(iShader);
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
        int error = GLES30.glGetError();
        if (error != GLES30.GL_NO_ERROR) {
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
        if (context != null) {
            final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
                if (configurationInfo != null) {
                    System.out.println(configurationInfo.toString());
                    version = Float.parseFloat(configurationInfo.getGlEsVersion());
                }
            }
        }
        return version;
    }

    public static final float QUAD_VERTICES[] = {
            // positions // texCoords
            -1.0f, 1.0f, 0.0f, 1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f};


    /**
     * 创建VAO（顶点数组对象）
     * 创建VBO（顶点缓冲对象）
     *
     * @param vertexIndex        顶点坐标在gl中的标示
     * @param textureVertexIndex 纹理坐标在gl中的标示
     * @return [0]vao, [1]vbo
     */
    public static int[] createQuadVertexArrays(int vertexIndex, int textureVertexIndex) {
        int[] vao = {-1};
        int[] vbo = {-1};

        //创建1个VAO、VBO对象，暂未分配空间
        GLES30.glGenVertexArrays(1, vao, 0);
        GLES30.glGenBuffers(1, vbo, 0);

        //激活对象
        GLES30.glBindVertexArray(vao[0]);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);

        // GLES30.GL_STATIC_DRAW 位置数据不会改变，每次渲染调用时都保存原样
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, QUAD_VERTICES.length * 4, getFloatBuffer(QUAD_VERTICES), GLES30.GL_STATIC_DRAW);
        GLES30.glEnableVertexAttribArray(vertexIndex);
        GLES30.glVertexAttribPointer(vertexIndex, 2, GLES30.GL_FLOAT, false, 4 * 4, 0);

        GLES30.glEnableVertexAttribArray(textureVertexIndex);
        GLES30.glVertexAttribPointer(textureVertexIndex, 2, GLES30.GL_FLOAT, false, 4 * 4, 2 * 4);
        GLES30.glBindVertexArray(0);

        return new int[]{vao[0], vbo[0]};
    }

    public static FloatBuffer getFloatBuffer(float[] arr) {
        // 创建顶点坐标数据缓冲
        // vertices.length*4是因为一个float占四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(arr.length * 4);
        vbb.order(ByteOrder.nativeOrder()); //设置字节顺序
        FloatBuffer vertexBuf = vbb.asFloatBuffer(); //转换为Float型缓冲
        vertexBuf.put(arr); //向缓冲区中放入顶点坐标数据
        vertexBuf.position(0); //设置缓冲区起始位置
        return vertexBuf;
    }

    /**
     * @param width
     * @param height
     * @return [0]:texture id
     * [1]:frameBuffer id
     */
    public static int[] crateFrameBuffer(int width, int height) {
        int textureId = createRGBATexture2D(width, height);
        int frameBufferId = crateFrameBuffer(textureId);
        return new int[]{textureId, frameBufferId};
    }

    public static int crateFrameBuffer(int textureId) {
        int[] id = {-1};
        GLES30.glGenFramebuffers(1, id, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, id[0]);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureId, 0);
        if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        return id[0];
    }

    public static int createRGBATexture2D(int w, int h) {
        int[] id = {-1};
        GLES30.glGenTextures(1, id, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, id[0]);
        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT, 4);//4字节对齐
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA8, w, h, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        return id[0];
    }

    /**
     * 计算最小texture size(最省内存和减少不必要的计算)，此矩形不会大于显示view和camera数据的size
     */
    public static Size getTextureSize(int viewWidth, int viewHeight, int cameraWidth, int cameraHeight) {
        final int maxSize = 4096;
        //计算texture大小
        int w;
        int h;
        //目标比例
        float scale = (float) viewWidth / (float) viewHeight;
        //选宽来按比例计算长宽，长宽为2的倍数
        w = Math.min(cameraWidth, maxSize);
        w = (w >> 1) << 1;
        h = Math.round((float) w / scale);
        h = (h >> 1) << 1;
        //是否为内切矩形
        int minH = Math.min(cameraHeight, maxSize);
        if (h > minH) {
            h = minH;
            h = (h >> 1) << 1;
            w = Math.round(h * scale);
            w = (w >> 1) << 1;
        }
        //是否大于显示矩形
        if (w > viewWidth) {
            w = viewWidth;
            w = (w >> 1) << 1;
            h = viewHeight;
            h = (h >> 1) << 1;
        }
        return new Size(w, h);
    }

}
