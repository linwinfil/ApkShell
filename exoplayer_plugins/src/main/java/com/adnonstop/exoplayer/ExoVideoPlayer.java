package com.adnonstop.exoplayer;

import android.content.Context;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.util.ArrayList;

/**
 * @author lmx
 *         Created by lmx on 2018-02-09.
 */

public class ExoVideoPlayer implements IMediaController.MediaPlayerControl
{
    private static final String TAG = ExoVideoPlayer.class.getSimpleName();

    private Context mContext;
    private SimpleExoPlayer mPlayer;
    private Timeline.Window mWindow;
    private MediaSourceBuilder mMediaSourceBuilder;
    private View mSurfaceView;

    private ComponentListener mComponentListener;
    private IPlayerViewListener mPlayerViewListener;
    private IPlayerInfoListener mPlayerInfoListener;

    private int mResumeWindow;
    private long mResumePosition;

    private boolean handPause;
    private boolean isPause;

    public ExoVideoPlayer(Context mContext, IDataSourceFactory mDataSourceFactory)
    {
        this(mContext, new MediaSourceBuilder(mContext, mDataSourceFactory));
    }

    public ExoVideoPlayer(Context mContext, MediaSourceBuilder mediaSourceBuilder) {
        this.mContext = mContext;
        this.mMediaSourceBuilder = mediaSourceBuilder;
        this.mComponentListener = new ComponentListener();
        this.mWindow = new Timeline.Window();
        this.mPlayer = initPlayer();
        this.mPlayer.addVideoListener(mComponentListener);
        this.mPlayer.addListener(mComponentListener);
        clearResumePosition();
    }

    private SimpleExoPlayer initPlayer()
    {
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(trackSelectionFactory);
        SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, defaultTrackSelector);
        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        return simpleExoPlayer;
    }


    private void updateResumePosition()
    {
        if (mPlayer == null) clearResumePosition();
        mResumeWindow = mPlayer.getCurrentWindowIndex();
        mResumePosition = Math.max(0, mPlayer.getContentPosition());
    }


    private void clearResumePosition()
    {
        mResumeWindow = C.INDEX_UNSET;
        mResumePosition = C.TIME_UNSET;
    }

    public void setMediaSourceBuilder(MediaSourceBuilder mediaSourceBuilder)
    {
        if (this.mMediaSourceBuilder != mediaSourceBuilder)
        {
            this.mMediaSourceBuilder.release();
            this.mMediaSourceBuilder = mediaSourceBuilder;
        }
    }

    public void setVideoRenderSurfaceView(View renderView) {
        if (mSurfaceView == renderView) return;
        this.mSurfaceView = renderView;
        if (renderView instanceof SurfaceView) {
            if (this.mPlayer != null) this.mPlayer.setVideoSurfaceView((SurfaceView) renderView);
        } else if (renderView instanceof TextureView) {
            if (this.mPlayer != null) this.mPlayer.setVideoTextureView((TextureView) renderView);
        }
    }

    public void setPlayerViewListener(IPlayerViewListener mPlayerViewListener)
    {
        this.mPlayerViewListener = mPlayerViewListener;
    }

    public void setPlayerInfoListener(IPlayerInfoListener mPlayerInfoListener)
    {
        this.mPlayerInfoListener = mPlayerInfoListener;
    }

    public void setDataSource(ArrayList<DefaultMediaSource> dataSource)
    {
        if (dataSource == null || dataSource.size() == 0) return;
        if (this.mMediaSourceBuilder != null)
        {
            this.mMediaSourceBuilder.setMediaUri(dataSource);
        }
    }

    public void setVolume(float volume)
    {
        if (mPlayer != null)
        {
            mPlayer.setVolume(volume);
        }
    }

    public SimpleExoPlayer getPlayer() {
        return mPlayer;
    }

    public void prepare()
    {
        if (mPlayer != null && mMediaSourceBuilder != null)
        {
            MediaSource mediaSource = mMediaSourceBuilder.getMediaSource();
            if (mediaSource != null) mPlayer.prepare(mediaSource);
        }
    }

    @Override
    public void start()
    {
        if (mPlayer != null)
        {
            boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;
            if (haveResumePosition)
            {
                mPlayer.seekTo(mResumeWindow, mResumePosition);
            }
            if (handPause)
            {
                mPlayer.setPlayWhenReady(false);
            }
            else
            {
                mPlayer.setPlayWhenReady(true);
            }
        }
    }

    @Override
    public void pause()
    {
        isPause = true;
        if (mPlayer != null)
        {
            handPause = !mPlayer.getPlayWhenReady();
            mPlayer.setPlayWhenReady(false);
            updateResumePosition();
        }
    }

    @Override
    public void seekTo(long millisecond)
    {
        if (mPlayer == null) return;
        int windowIndex = 0;
        if (millisecond == 0)
        {
            mPlayer.seekTo(windowIndex, millisecond);
            return;
        }

        Timeline timeline = mPlayer.getCurrentTimeline();
        if (!timeline.isEmpty())
        {
            int windowCount = timeline.getWindowCount();    //视频分段个数
            while (true)
            {
                long windowDurationMs = timeline.getWindow(windowIndex, mWindow).getDurationMs();
                if (millisecond < windowDurationMs)
                {
                    break;
                }
                else if (windowIndex == windowCount - 1)
                {
                    // Seeking past the end of the last window should seek to the end of the timeline.
                    millisecond = windowDurationMs;
                    break;
                }
                millisecond -= windowDurationMs;
                windowIndex++;
            }
        }
        else
        {
            windowIndex = mPlayer.getCurrentWindowIndex();
        }
        // Log.d(TAG, "VideoPlayer --> seekTo: windowIndex:" + windowIndex + " mill:" + millisecond);
        mPlayer.seekTo(windowIndex, millisecond);
    }

    public long getCurrentPosition()
    {
        return mPlayer != null ? mPlayer.getCurrentPosition() : 0;
    }

    @Override
    public long getDuration()
    {
        return mPlayer != null ? mPlayer.getDuration() : 0;
    }

    @Override
    public boolean isPlaying()
    {
        if (mPlayer == null) return false;
        int playbackState = mPlayer.getPlaybackState();
        return playbackState != Player.STATE_IDLE
                && playbackState != Player.STATE_ENDED
                && mPlayer.getPlayWhenReady();
    }

    @Override
    public int getBufferPercentage()
    {
        return mPlayer != null ? mPlayer.getBufferedPercentage() : 0;
    }


    public void release()
    {
        if (mPlayer != null)
        {
            pause();
            mPlayer.stop();
            mPlayer.release();
            if (mSurfaceView instanceof SurfaceView) {
                mPlayer.clearVideoSurfaceView((SurfaceView) mSurfaceView);
            } else if (mSurfaceView instanceof TextureView) {
                mPlayer.clearVideoTextureView((TextureView) mSurfaceView);
            }
            mPlayer.removeListener(mComponentListener);
            mPlayer.removeVideoListener(mComponentListener);
        }
        if (mMediaSourceBuilder != null) {
            mMediaSourceBuilder.release();
        }

        mPlayer = null;
        mMediaSourceBuilder = null;
    }



    private class ComponentListener implements SimpleExoPlayer.VideoListener, Player.EventListener
    {
        public ComponentListener()
        {
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio)
        {
            if (mPlayerViewListener != null)
            {
                mPlayerViewListener.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
            }
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState)
        {
            if (mPlayerInfoListener != null && mPlayer != null)
            {
                mPlayerInfoListener.isPlaying(mPlayer.getPlayWhenReady());
            }
            switch (playbackState)
            {
                case Player.STATE_BUFFERING:
                    if (playWhenReady)
                    {
                        if (mPlayerViewListener != null)
                        {
                            mPlayerViewListener.showLoadStateView(View.VISIBLE);
                        }
                    }
                    if (mPlayerInfoListener != null) mPlayerInfoListener.onLoadingChanged();
                    break;
                case Player.STATE_ENDED:
                    if (mPlayerInfoListener != null) mPlayerInfoListener.onPlayEnd();
                    break;
                case Player.STATE_IDLE:
                    break;
                case Player.STATE_READY:
                    if (mPlayerViewListener != null)
                    {
                        mPlayerViewListener.showLoadStateView(View.GONE);
                    }
                    if (mPlayerInfoListener != null) mPlayerInfoListener.onPlayStart();
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error)
        {
            if (mPlayerViewListener != null) mPlayerViewListener.showLoadStateView(View.VISIBLE);
            if (mPlayerInfoListener != null) mPlayerInfoListener.onPlayerError(error);
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest)
        {
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections)
        {
        }

        @Override
        public void onLoadingChanged(boolean isLoading)
        {
        }

        @Override
        public void onRepeatModeChanged(int repeatMode)
        {
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled)
        {
        }

        @Override
        public void onPositionDiscontinuity(int reason)
        {
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters)
        {
        }

        @Override
        public void onSeekProcessed()
        {
        }


        @Override
        public void onRenderedFirstFrame()
        {
        }
    }
}
