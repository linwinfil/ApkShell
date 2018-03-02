package cn.poco.video;

/**
 * Created by admin on 2017/4/20.
 */

public interface TaskFinishCallback {
    public void onEncodeError(int errorcode);
    public void onEncodeSuccess(String videooutpath);
}
