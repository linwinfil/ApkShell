package cn.poco.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;

/**
 * Created by menghd on 2017/3/2 0002.
 */

public class FileUtils {
    public static void deleteFile(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }

    public static void deleteFile(List<String> filePathList){
        if(filePathList == null || filePathList.size() == 0 ){
            return;
        }

        for (int i = 0 ; i < filePathList.size() ; i ++){
            File file = new File(filePathList.get(i));
            if(file.exists()){
                file.delete();
            }
        }
    }
    public static void deleteFile(String[] filePathList){
        if(filePathList == null || filePathList.length == 0 ){
            return;
        }

        for (int i = 0 ; i < filePathList.length ; i ++){
            File file = new File(filePathList[i]);
            if(file.exists()){
                file.delete();
            }
        }
    }

    public static void deleteFile(File[] filePathList){
        if(filePathList == null || filePathList.length == 0 ){
            return;
        }

        for (int i = 0 ; i < filePathList.length ; i ++){
            if(filePathList[i].exists()){
                filePathList[i].delete();
            }
        }
    }

    private static int toInt(byte[] b) {
        return ((b[3] << 24) + (b[2] << 16) + (b[1] << 8) + (b[0] << 0));
    }

    private static short toShort(byte[] b) {
        return (short)((b[1] << 8) + (b[0] << 0));
    }


    private static byte[] read(RandomAccessFile rdf, int pos, int length) throws IOException {
        rdf.seek(pos);
        byte result[] = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = rdf.readByte();
        }
        return result;
    }

    private static long toLong(byte[] b) {
        long l = 0;
        l = b[0];
        l |= ((long) b[1] << 8);
        l |= ((long) b[2] << 16);
        l |= ((long) b[3] << 24);
        return l;

    }

    /**
     * 1.channels  2.samplerate
     * @param wavFilePath
     * @return
     */
    public static HashMap<String,Integer> getWavInfo(String wavFilePath){
        File f = new File(wavFilePath);
        HashMap<String,Integer> wavInfoMap = new HashMap<String,Integer>();

        try {
            FileInputStream stream =  new FileInputStream(f);

            byte[] header = new byte[12];
            stream.read(header, 0, 12);
            if (header[0] != 'R' ||
                    header[1] != 'I' ||
                    header[2] != 'F' ||
                    header[3] != 'F' ||
                    header[8] != 'W' ||
                    header[9] != 'A' ||
                    header[10] != 'V' ||
                    header[11] != 'E') {
                MyLog.e(FileUtils.class,"Not a WAV file  " + MyLog.getLineNumber());
                return wavInfoMap;
            }

            byte[] chunkHeader = new byte[8];
            stream.read(chunkHeader, 0, 8);

            int chunkLen =
                    ((0xff & chunkHeader[7]) << 24) |
                            ((0xff & chunkHeader[6]) << 16) |
                            ((0xff & chunkHeader[5]) << 8) |
                            ((0xff & chunkHeader[4]));

            if (chunkHeader[0] == 'f' &&
                    chunkHeader[1] == 'm' &&
                    chunkHeader[2] == 't' &&
                    chunkHeader[3] == ' ') {
                if (chunkLen < 16 || chunkLen > 1024) {

                }

                byte[] fmt = new byte[chunkLen];
                stream.read(fmt, 0, chunkLen);

                int format =
                        ((0xff & fmt[1]) << 8) |
                                ((0xff & fmt[0]));
                int mChannels =
                        ((0xff & fmt[3]) << 8) |
                                ((0xff & fmt[2]));
                int mSampleRate =
                        ((0xff & fmt[7]) << 24) |
                                ((0xff & fmt[6]) << 16) |
                                ((0xff & fmt[5]) << 8) |
                                ((0xff & fmt[4]));
                wavInfoMap.put("channels",mChannels);
                wavInfoMap.put("samplerate",mSampleRate);
                wavInfoMap.put("format",format);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
            return wavInfoMap;
        }

    public static boolean copyFile(String srcFileName, String destFileName,
                                   boolean overlay) {
        File srcFile = new File(srcFileName);

        if (!srcFile.exists()) {
            return false;
        } else if (!srcFile.isFile()) {
            return false;
        }

        File destFile = new File(destFileName);
        if (destFile.exists()) {
            if (overlay) {
                new File(destFileName).delete();
            }
        } else {
            if (!destFile.getParentFile().exists()) {
                if (!destFile.getParentFile().mkdirs()) {
                    return false;
                }
            }
        }

        // 复制文件
        int byteread = 0; // 读取的字节数
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
