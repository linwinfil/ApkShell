package doupai.venus.venus;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author lmx
 * Created by lmx on 2018/11/6.
 */
public final class Venus
{

    static
    {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("funkit");
        System.loadLibrary("venus_v3");
    }

    public static void init()
    {
    }

    public static native void argbScale(Bitmap bitmap, Bitmap bitmap2);

    public static native void buffer2bitmap(Bitmap bitmap, ByteBuffer byteBuffer, int i, int i2, int i3);

    public static native String decrypt(String str, String str2);

    public static native Bitmap jpegDecode(String str, int i, int i2);

    public static void load(AssetManager assetManager, boolean z)
    {
        String str = (!has64BitABI() || z) ? "venus_v2" : "venus_v3";
        System.loadLibrary(str);
        NativeObject.native_init(assetManager);
    }

    public static native Bitmap webpDecode(String str);

    public static native void yuv2argb(Bitmap bitmap, ByteBuffer byteBuffer, int i, int i2, int i3, int i4);

    public static native void yuv2rgb565(Bitmap bitmap, ByteBuffer byteBuffer, int i, int i2, int i3, int i4);

    public static boolean has64BitABI()
    {
        return Build.VERSION.SDK_INT >= 21 && Build.SUPPORTED_64_BIT_ABIS != null && Build.SUPPORTED_64_BIT_ABIS.length > 0;
    }


}
