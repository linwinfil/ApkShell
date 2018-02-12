package com.adnonstop.exoplayer;

import android.view.View;

/**
 * @author lmx
 *         Created by lmx on 2018-02-09.
 */

public interface IMediaController
{
    void setMediaPlayer(IMediaController.MediaPlayerControl control);

    void show();

    void show(int var1);

    void hide();

    boolean isShowing();

    void setEnabled(boolean var1);

    void setAnchorView(View var1);

    public interface MediaPlayerControl
    {
        void start();

        void pause();

        long getDuration();

        long getCurrentPosition();

        void seekTo(long millisecond);

        boolean isPlaying();

        int getBufferPercentage();

        /*boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();*/
    }
}
