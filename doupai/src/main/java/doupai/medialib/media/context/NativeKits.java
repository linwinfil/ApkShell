package doupai.medialib.media.context;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

import doupai.venus.venus.Venus;

/**
 * @author lmx
 * Created by lmx on 2018/11/6.
 */
public class NativeKits
{
    private static Set<String> UNSUPPORT = new HashSet();

    private static final Handler handler = new Handler(Looper.getMainLooper());

    static
    {
        UNSUPPORT.add("ALE-L21");
        UNSUPPORT.add("ALE-TL00");
        UNSUPPORT.add("ALE-UL00");
        UNSUPPORT.add("CAM L21");
        UNSUPPORT.add("CAM TL00");
        UNSUPPORT.add("CAM TL00H");
        UNSUPPORT.add("CAM UL00");
        UNSUPPORT.add("CHE-TL00");
        UNSUPPORT.add("CHE-TL00H");
        UNSUPPORT.add("CHE2-UL00");
        UNSUPPORT.add("CHM-TL00H");
        UNSUPPORT.add("CHM-UL00");
        UNSUPPORT.add("RIO-AL00");
    }

    // public static Bitmap applyBeautyFilter(@NonNull Bitmap bitmap, @FloatRange(from = 0.10000000149011612d, to = 1.0d) float f)
    // {
    //     Size2i size2i = new Size2i(CommonUtils.format2Even(bitmap.getWidth(), false), CommonUtils.format2Even(bitmap.getHeight(), false));
    //     return FlexLiveSession.beautify(Bitmap.createBitmap(bitmap, 0, 0, size2i.width, size2i.height), size2i);
    // }

    // public static String decryptJsonV1(@NonNull String str)
    // {
    //     return FunkitSession.decrypt(str);
    // }

    public static String decryptJsonV2(@NonNull String str, @NonNull String str2)
    {
        return Venus.decrypt(str, str2);
    }
    //
    // public static MetaData getMetaData(@NonNull String str)
    // {
    //     return new MetaData(FunkitSession.GetMetaAudioLite(str), FunkitSession.GetMetaVideoLite(str), str);
    // }
    //
    // public static boolean getMetaData(@NonNull String str, SimpleCallback<MetaData> simpleCallback)
    // {
    //     if (!FileUtils.isFilesExist(new String[]{str}))
    //     {
    //         return false;
    //     }
    //     new Thread(new 0 (simpleCallback, str)).start();
    //     return true;
    // }
    //
    // public static void initNativeLib(@NonNull Activity activity)
    // {
    //     Venus.load(activity.getAssets(), UNSUPPORT.contains(Build.MODEL));
    // }
    //
    // static final /* synthetic */ void lambda$writeExtraMeta$1$NativeKits(@NonNull String str, @NonNull String str2, @NonNull String str3, @NonNull SimpleCallback simpleCallback)
    // {
    //     try
    //     {
    //         FunkitSession.avremuxfile(str, str2, str3);
    //         simpleCallback.complete(str);
    //     }
    //     catch (Exception e)
    //     {
    //         while (true)
    //         {
    //         }
    //         e.printStackTrace();
    //         simpleCallback.complete(str2);
    //     }
    // }
    //
    // public static void registerBaseFilter(@NonNull String str, @NonNull String str2)
    // {
    //     FunkitSession.register_filter(str, str2);
    // }
    //
    // public static void releaseNativeLib()
    // {
    // }
    //
    // public static void writeExtraMeta(@NonNull String str, @NonNull String str2, @NonNull String str3, @NonNull SimpleCallback<String> simpleCallback)
    // {
    //     new Thread(new 1 (str2, str, str3, simpleCallback)).start();
    // }
}
