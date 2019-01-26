package com.adnonstop.exoplayer;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.adnonstop.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.util.Formatter;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * @author lmx
 *         Created by lmx on 2018-02-10.
 */

public class PlayerControllerBarView extends FrameLayout
{

    /**
     * Listener to be notified about changes of the visibility of the UI control.
     */
    public interface VisibilityListener
    {
        /**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility. Either {@link View#VISIBLE} or {@link View#GONE}.
         */
        void onVisibilityChange(int visibility);
    }

    public interface FullscreenListener {

        void onClickFullscreen();
    }

    private final Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };




    /**
     * The default show timeout, in milliseconds.
     */
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;

    private ComponentListener componentListener;
    private VisibilityListener visibilityListener;
    private ControlDispatcher controlDispatcher;
    private FullscreenListener fullscreenListener;

    private DefaultTimeBar defaultTimeBar;
    private TextView currentTimeView;
    private TextView durationTimeView;
    private AppCompatCheckBox fullscreenBtnView;
    private View playBtnView;
    private View pauseBtnView;
    private Player player;

    private final Timeline.Period period;
    private final Timeline.Window window;
    private final StringBuilder stringBuilder;
    private final Formatter formatter;

    private long showTimeoutMs;
    private long hideAtMs;

    private boolean isAttachedToWindow;
    private boolean scrubbing;

    @DrawableRes
    int fullscreenSelectorDrawable = R.drawable.ic_fullscreen_selector;

    public PlayerControllerBarView(@NonNull Context context)
    {
        this(context, null);
    }

    public PlayerControllerBarView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PlayerControllerBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        int layoutId = R.layout.exo_view_controller_bar_view;
        LayoutInflater.from(context).inflate(layoutId, this);

        playBtnView = findViewById(R.id.exo_view_controller_btn_play);
        pauseBtnView = findViewById(R.id.exo_view_controller_btn_pause);
        defaultTimeBar = findViewById(R.id.exo_view_controller_timeline_bar);
        currentTimeView = findViewById(R.id.exo_view_controller_txt_current_time);
        durationTimeView = findViewById(R.id.exo_view_controller_txt_duration_time);
        fullscreenBtnView = findViewById(R.id.exo_view_controller_btn_fullscreen);

        componentListener = new ComponentListener();
        controlDispatcher = new com.google.android.exoplayer2.DefaultControlDispatcher();

        if (playBtnView != null)
        {
            playBtnView.setOnClickListener(componentListener);
        }
        if (pauseBtnView != null)
        {
            pauseBtnView.setOnClickListener(componentListener);
        }
        if (fullscreenBtnView != null)
        {
            fullscreenBtnView.setOnClickListener(componentListener);
        }
        if (fullscreenBtnView != null)
        {
            fullscreenBtnView.setButtonDrawable(fullscreenSelectorDrawable);
        }

        defaultTimeBar.addListener(componentListener);

        stringBuilder = new StringBuilder();
        formatter = new Formatter(stringBuilder);

        window = new Timeline.Window();
        period = new Timeline.Period();
    }

    public void setPlayer(Player player)
    {
        if (this.player == player) return;
        this.player = player;
    }

    public void setShowTimeoutMs(long showTimeoutMs)
    {
        this.showTimeoutMs = showTimeoutMs;
    }

    public void hide()
    {
        if (isVisible())
        {
            setVisibility(View.GONE);
            if (visibilityListener != null)
            {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            removeCallbacks(updateProgressAction);
            removeCallbacks(hideAction);
            hideAtMs = C.TIME_UNSET;
        }
    }

    public void show()
    {
        if (!isVisible())
        {
            setVisibility(View.VISIBLE);
            if (visibilityListener != null)
            {
                visibilityListener.onVisibilityChange(getVisibility());
            }
           /* updateAll();*/
            requestPlayPauseFocus();
        }
        hideAfterTimeout();
    }

    private void hideAfterTimeout()
    {
        removeCallbacks(hideAction);
        if (showTimeoutMs > 0)
        {
            hideAtMs = SystemClock.uptimeMillis() + showTimeoutMs;
            if (isAttachedToWindow)
            {
                postDelayed(hideAction, showTimeoutMs);
            }
        }
        else
        {
            hideAtMs = C.TIME_UNSET;
        }
    }

    public boolean isVisible()
    {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        if (hideAtMs != C.TIME_UNSET)
        {
            long delayMs = hideAtMs - SystemClock.uptimeMillis();
            if (delayMs <= 0)
            {
                hide();
            }
            else
            {
                postDelayed(hideAction, delayMs);
            }
        }
        updateAll();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        removeCallbacks(updateProgressAction);
        removeCallbacks(hideAction);
    }

    public void setFullscreenListener(FullscreenListener fullscreenListener)
    {
        this.fullscreenListener = fullscreenListener;
    }

    public void setVisibilityListener(VisibilityListener visibilityListener)
    {
        this.visibilityListener = visibilityListener;
    }


    private void updateAll() {
        updatePlayPauseBtn();
        updateProgress();
    }

    private void updatePlayPauseBtn()
    {
        if (!isVisible() || !isAttachedToWindow)
        {
            return;
        }
        boolean requestPlayPauseFocus = false;
        boolean playing = player != null && player.getPlayWhenReady();
        if (playBtnView != null)
        {
            requestPlayPauseFocus |= playing && playBtnView.isFocused();
            playBtnView.setVisibility(playing ? View.GONE : View.VISIBLE);
        }
        if (pauseBtnView != null)
        {
            requestPlayPauseFocus |= !playing && pauseBtnView.isFocused();
            pauseBtnView.setVisibility(!playing ? View.GONE : View.VISIBLE);
        }
        if (requestPlayPauseFocus)
        {
            requestPlayPauseFocus();
        }
    }

    private void requestPlayPauseFocus()
    {
        boolean playing = player != null && player.getPlayWhenReady();
        if (!playing && playBtnView != null)
        {
            playBtnView.requestFocus();
        }
        else if (playing && pauseBtnView != null)
        {
            pauseBtnView.requestFocus();
        }
    }

    public void setFullscreenCheckedState(boolean checked) {
        if (fullscreenBtnView != null) {
            fullscreenBtnView.setChecked(checked);
        }
    }

    private void updateProgress() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }
        long position = 0;
        long bufferedPosition = 0;
        long duration = 0;
        if (player != null) {
            long currentWindowTimeBarOffsetUs = 0;
            long durationUs = 0;
            Timeline timeline = player.getCurrentTimeline();
            if (!timeline.isEmpty()) {
                int currentWindowIndex = player.getCurrentWindowIndex();
                int firstWindowIndex = 0;
                int lastWindowIndex = timeline.getWindowCount() - 1;
                for (int i = firstWindowIndex; i <= lastWindowIndex; i++) {
                    if (i == currentWindowIndex) {
                        currentWindowTimeBarOffsetUs = durationUs;
                    }
                    timeline.getWindow(i, window);
                    if (window.durationUs == C.TIME_UNSET) {
                        Assertions.checkState(true);
                        break;
                    }
                    durationUs += window.durationUs;
                }
            }
            duration = C.usToMs(durationUs);
            position = C.usToMs(currentWindowTimeBarOffsetUs);
            bufferedPosition = position;
            if (player.isPlayingAd()) {
                position += player.getContentPosition();
                bufferedPosition = position;
            } else {
                position += player.getCurrentPosition();
                bufferedPosition += player.getBufferedPosition();
            }
        }
        if (durationTimeView != null) {
            durationTimeView.setText(Util.getStringForTime(stringBuilder, formatter, duration));
        }
        if (currentTimeView != null && !scrubbing) {
            currentTimeView.setText(Util.getStringForTime(stringBuilder, formatter, position));
        }
        if (defaultTimeBar != null) {
            defaultTimeBar.setPosition(position);
            defaultTimeBar.setBufferedPosition(bufferedPosition);
            defaultTimeBar.setDuration(duration);
        }

        // Cancel any pending updates and schedule a new one if necessary.
        removeCallbacks(updateProgressAction);
        int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                float playbackSpeed = player.getPlaybackParameters().speed;
                if (playbackSpeed <= 0.1f) {
                    delayMs = 1000;
                } else if (playbackSpeed <= 5f) {
                    long mediaTimeUpdatePeriodMs = 1000 / Math.max(1, Math.round(1 / playbackSpeed));
                    long mediaTimeDelayMs = mediaTimeUpdatePeriodMs - (position % mediaTimeUpdatePeriodMs);
                    if (mediaTimeDelayMs < (mediaTimeUpdatePeriodMs / 5)) {
                        mediaTimeDelayMs += mediaTimeUpdatePeriodMs;
                    }
                    delayMs = playbackSpeed == 1 ? mediaTimeDelayMs
                            : (long) (mediaTimeDelayMs / playbackSpeed);
                } else {
                    delayMs = 200;
                }
            } else {
                delayMs = 1000;
            }
            postDelayed(updateProgressAction, delayMs);
        }
    }

    private void seekToTimeBarPosition(long positionMs) {
        int windowIndex;
        Timeline timeline = player.getCurrentTimeline();
        if (!timeline.isEmpty()) {
            int windowCount = timeline.getWindowCount();
            windowIndex = 0;
            while (true) {
                long windowDurationMs = timeline.getWindow(windowIndex, window).getDurationMs();
                if (positionMs < windowDurationMs) {
                    break;
                } else if (windowIndex == windowCount - 1) {
                    // Seeking past the end of the last window should seek to the end of the timeline.
                    positionMs = windowDurationMs;
                    break;
                }
                positionMs -= windowDurationMs;
                windowIndex++;
            }
        } else {
            windowIndex = player.getCurrentWindowIndex();
        }
        seekTo(windowIndex, positionMs);
    }

    private void seekTo(int windowIndex, long positionMs) {
        boolean dispatched = controlDispatcher.dispatchSeekTo(player, windowIndex, positionMs);
        if (!dispatched) {
            // The seek wasn't dispatched. If the progress bar was dragged by the user to perform the
            // seek then it'll now be in the wrong position. Trigger a progress update to snap it back.
            updateProgress();
        }
    }

    private class ComponentListener extends Player.DefaultEventListener implements
            DefaultTimeBar.OnScrubListener, OnClickListener
    {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState)
        {
            updatePlayPauseBtn();
            updateProgress();
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            updateProgress();
        }


        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            updateProgress();
        }


        @Override
        public void onScrubStart(TimeBar timeBar, long position)
        {
            removeCallbacks(hideAction);
            scrubbing = true;
        }

        @Override
        public void onScrubMove(TimeBar timeBar, long position)
        {
            scrubbing = true;
            if (currentTimeView != null) {
                currentTimeView.setText(Util.getStringForTime(stringBuilder, formatter, position));
            }
        }

        @Override
        public void onScrubStop(TimeBar timeBar, long position, boolean canceled)
        {
            scrubbing = false;
            if (!canceled && player != null) {
                seekToTimeBarPosition(position);
            }
            hideAfterTimeout();
        }

        @Override
        public void onClick(View v)
        {
            if (player == null) return;

            if (v == playBtnView)
            {
                if (controlDispatcher != null)
                {
                    controlDispatcher.dispatchSetPlayWhenReady(player, true);
                }
            }
            else if (v == pauseBtnView)
            {
                if (controlDispatcher != null)
                {
                    controlDispatcher.dispatchSetPlayWhenReady(player, false);
                }
            }
            else if (v == fullscreenBtnView)
            {
                if (fullscreenListener != null) fullscreenListener.onClickFullscreen();
            }

            hideAfterTimeout();
        }
    }
}
