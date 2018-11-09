package doupai.venus.venus;

import android.content.res.AssetManager;

/**
 * @author lmx
 * Created by lmx on 2018/11/6.
 */
public abstract class NativeObject
{
    private long mNativeHandle;

    public static native void native_init(AssetManager assetManager);

    public abstract void destroy();

    public final boolean isAvailable()
    {
        return this.mNativeHandle != 0;
    }

}
