package com.maoxin.apkshell.audio;

import android.os.Environment;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;
/**
 * @author lmx
 *         Created by lmx on 2017/7/26.
 */

public class FileUtils
{
    public static final String AAC_FORMAT = ".aac";
    public static final String WAV_FORMAT = ".wav";
    public static final String PCM_FORMAT = ".pcm";
    public static final String MP3_FORMAT = ".mp3";
    public static final String MP4_FORMAT = ".mp4";
    public static final String H264_FORMAT = ".h264";

    @StringDef({AAC_FORMAT, WAV_FORMAT, PCM_FORMAT, MP3_FORMAT, MP4_FORMAT, H264_FORMAT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Format
    {
    }

    public static String sVideoDir;
    private static String sTempDir;


    public static String GetAppPath()
    {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return absolutePath + File.separator + "apkshell";
    }

    public static boolean DeleteSDFile(String path, boolean deleteParent)
    {
        if (TextUtils.isEmpty(path))
        {
            return false;
        }
        File file = new File(path);
        return !file.exists() || DeleteFile(file, deleteParent);

    }

    public static boolean DeleteFile(File file, boolean deleteParent) {
        boolean flag = false;
        if (file == null) {
            return flag;
        }
        if (file.isDirectory()) {
            //是文件夹
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file1 : files)
                {
                    flag = DeleteFile(file1, true);
                    if (!flag)
                    {
                        return false;
                    }
                }
            }
            if (deleteParent) {
                flag = file.delete();
            }
        } else {
            flag = file.delete();
        }
        file = null;
        return flag;
    }

    public static void MakeFolder(String path)
    {
        if(path != null)
        {
            File file = new File(path);
            if(!(file.exists() && file.isDirectory()))
            {
                file.mkdirs();
            }
        }
    }


    /**
     * 视频存储目录
     *
     * @return
     */
    public static String getVideoDir()
    {
        if (TextUtils.isEmpty(sVideoDir))
        {
            sVideoDir = GetAppPath() + File.separator + "video";
        }
        return sVideoDir;
    }

    /**
     * 视频混合临时缓存目录（隐藏）
     *
     * @return
     */
    public static String getTempDir()
    {
        if (TextUtils.isEmpty(sTempDir))
        {
            sTempDir = getVideoDir() + File.separator + ".temp";
        }

        return sTempDir;
    }


    /**
     * 清除所有临时文件
     */
    public static void clearTempFiles()
    {
        if (sTempDir != null && new File(sTempDir).exists())
        {
            DeleteSDFile(FileUtils.sTempDir, false);
        }
        sTempDir = null;
    }


    /**
     * 清除录制视频临时文件
     */
    public static void clearVideoFiles()
    {
        if (sVideoDir != null && new File(sVideoDir).exists())
        {
            DeleteSDFile(FileUtils.sVideoDir, false);
        }
        sVideoDir = null;
    }

    /**
     * 获取临时路径
     *
     * @param format 文件格式
     * @return 临时路径
     */
    public static String getTempPath(@Format String format)
    {
        // 确保文件夹存在
        sTempDir = getTempDir();
        MakeFolder(sTempDir);
        return sTempDir + File.separator + UUID.randomUUID() + format;
    }


    /**
     * 获取临时路径
     *
     * @param format 文件格式
     * @return 临时路径
     */
    public static String getTempPath(@Format String format, String suffix)
    {
        // 确保文件夹存在
        sTempDir = getTempDir();
        MakeFolder(sTempDir);
        return sTempDir + File.separator + UUID.randomUUID() + (!TextUtils.isEmpty(suffix) ? ("_" + suffix) : "") + format;
    }

    public static boolean isAssetFile(String fileName)
    {
        if (TextUtils.isEmpty(fileName))
        {
            return false;
        }

        if (fileName.startsWith("file:///android_asset"))
        {
            return true;
        }

        return false;
    }


    public static boolean isFileExists(String path)
    {
        return !(path == null || path.trim().isEmpty() || !new File(path).exists());
    }

    public static boolean copyFile(String inputPath, String outputPath)
    {
        InputStream is = getSDStream(inputPath);
        return write2SD(is, outputPath, true, true);
    }

    /**
     * 获取sd卡文件
     */
    public static InputStream getSDStream(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return null;
        }
        File file = new File(filePath);
        InputStream is = null;
        if (!file.exists()) {
            //不存在
            return null;
        }
        if (file.isDirectory()) {
            //是目录 返回null
            return null;
        }
        file = null;
        try {
            is = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return is;
    }

    private static boolean isNullOrEmpty(String content) {
        return content == null || content.trim().isEmpty();
    }

    public static boolean write2SD(InputStream is, String path, boolean deleteOld, boolean close) {
        if (is == null || isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            return false;
        }
        if (file.exists()) {
            if (deleteOld) {
                file.delete();
            } else {
                return true;
            }
        } else {
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    return false;
                }
            }
        }
        try {
            if (!file.createNewFile()) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
            byte[] buf = new byte[4096];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                os = null;
            }
            if (close && is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
        }
        return true;
    }


    /**
     * 文件是否可读取
     *
     * @param path
     * @return
     */
    public static boolean isFileCanRead(String path)
    {
        if (!TextUtils.isEmpty(path))
        {
            try
            {
                File file = new File(path);
                if (file.exists() && file.canRead())
                {
                    return true;
                }
            }
            catch (Throwable t)
            {
                return false;
            }
        }
        return false;
    }


    public static boolean isFileValid(String path)
    {
        if (!TextUtils.isEmpty(path))
        {
            try
            {
                File file = new File(path);
                if (file.exists() && file.canRead() && file.length() > 1024 * 2)
                {
                    return true;
                }
            }
            catch (Throwable t)
            {
                return false;
            }
        }
        return false;
    }


    /**
     * 根据后缀创建临时音频文件（AAC WAV MP3）
     *
     * @param file
     * @return
     */
    public static String newTempAudioFile(String file)
    {
        String result = null;
        if (TextUtils.isEmpty(file)) return null;

        if (file.endsWith(FileUtils.AAC_FORMAT))
        {
            result = FileUtils.getTempPath(FileUtils.AAC_FORMAT);
        }
        else if (file.endsWith(FileUtils.WAV_FORMAT))
        {
            result = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
        }
        else if (file.endsWith(FileUtils.MP3_FORMAT))
        {
            result = FileUtils.getTempPath(FileUtils.MP3_FORMAT);
        }
        return result;
    }



    /**
     * 删除指定文件，如果指定文件时目录，需要先删除该目录下的所有文件才能删除该目录
     *
     * @param path 文件路径
     * @return 删除是否成功
     */
    public static boolean delete(String path)
    {
        if (!TextUtils.isEmpty(path))
        {
            File file = new File(path);
            if (!file.exists())
            {
                return false;
            }

            if (file.isDirectory())
            {
                String[] filePaths = file.list();
                for (String filePath : filePaths)
                {
                    delete(path + "/" + filePath);
                }
            }

            return file.delete();
        }

        return false;
    }
}
