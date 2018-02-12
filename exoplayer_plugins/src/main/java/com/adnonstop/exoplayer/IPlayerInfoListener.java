package com.adnonstop.exoplayer;

import android.support.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;

/**
 * @author lmx
 *         Created by lmx on 2018-02-09.
 */

public interface IPlayerInfoListener
{
    /***
     * 开始播放
     */
    void onPlayStart();

    /***
     * 播放结束
     */
    void onPlayEnd();

    /***
     * 播放是否加载中
     */
    void onLoadingChanged();

    /***
     * 播放失败
     * @param error 异常
     */
    void onPlayerError(@Nullable ExoPlaybackException error);


    /***
     *暂停还是播放
     * @param playWhenReady 暂停还是播放
     */
    void isPlaying(boolean playWhenReady);
}
