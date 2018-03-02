package cn.poco.video;

/**
 * Created by admin on 2017/3/30.
 */
public interface EncodeVideoCallback {
    public void onEncodeFinish();
    public void onEncodeOneFrame();
    public void onStartEncode();

}
