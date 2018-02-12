package com.adnonstop.exoplayer;

/**
 * @author lmx
 *         Created by lmx on 2018-02-09.
 */

public interface IPlayerViewListener
{
    /***
     * 显示隐藏加载布局
     *
     * @param visibility 显示类型
     */
    void showLoadStateView(int visibility);


    void onVideoSizeChanged(int width,
                            int height,
                            int unappliedRotationDegrees,
                            float pixelWidthHeightRatio);

}
