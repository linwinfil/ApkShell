package com.adnonstop.exoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.adnonstop.R;
import com.adnonstop.exoplayer.utils.VideoPlayerUtils;
import com.google.android.exoplayer2.Player;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author lmx
 *         Created by lmx on 2018-02-10.
 */

public class ExoVideoView extends FrameLayout
{
    private static final int SURFACE_TYPE_NONE = 0;
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;

    private ComponentListener componentListener;

    private AspectRatioFrameLayout contentFrame;
    private PlayerControllerBarView playerController;
    private ExoVideoPlayer player;

    private View surfaceView;

    private long controllerShowTimeoutMs;
    private boolean controllerAutoShow;
    private boolean isLandspace;

    public ExoVideoView(@NonNull Context context)
    {
        this(context, null);
    }

    public ExoVideoView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ExoVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        int playerViewContentFrameId = R.layout.exo_view_content_frame;
        int resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
        int surfaceType = SURFACE_TYPE_SURFACE_VIEW;
        int controllerShowTimeoutMs = PlayerControllerBarView.DEFAULT_SHOW_TIMEOUT_MS;
        boolean controllerAutoShow = true;
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ExoVideoView, 0, 0);
            try {
                resizeMode = typedArray.getInt(R.styleable.ExoVideoView_resize_mode, resizeMode);
                surfaceType = typedArray.getInt(R.styleable.ExoVideoView_surface_type, surfaceType);
                controllerShowTimeoutMs = typedArray.getInt(R.styleable.ExoVideoView_controller_show_timeout, controllerShowTimeoutMs);
                controllerAutoShow = typedArray.getBoolean(R.styleable.ExoVideoView_controller_show_auto, controllerAutoShow);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            finally {
                typedArray.recycle();
            }
        }
        LayoutInflater.from(context).inflate(playerViewContentFrameId, this);

        componentListener = new ComponentListener();

        //surface view
        contentFrame = findViewById(R.id.exo_view_content_frame);
        contentFrame.setResizeMode(resizeMode);
        if (contentFrame != null && surfaceType != SURFACE_TYPE_NONE) {
            surfaceView = surfaceType == SURFACE_TYPE_SURFACE_VIEW ? new SurfaceView(context) : new TextureView(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            contentFrame.addView(surfaceView, 0, params);
        } else {
            surfaceView = null;
        }

        //controller bar
        PlayerControllerBarView playerControllerBarView = findViewById(R.id.exo_view_player_controller);
        View controllerPlaceholder = findViewById(R.id.exo_view_player_controller_placeholder);
        if (playerControllerBarView != null) {
            this.playerController = playerControllerBarView;
        } else if (controllerPlaceholder != null) {
            this.playerController = new PlayerControllerBarView(getContext());
            ViewGroup parent = ((ViewGroup) controllerPlaceholder.getParent());
            int controllerIndex = parent.indexOfChild(controllerPlaceholder);
            parent.removeView(controllerPlaceholder);
            parent.addView(this.playerController, controllerIndex);
        } else {
            this.playerController = null;
        }

        if (this.playerController != null) {
            this.playerController.setFullscreenListener(componentListener);
            this.playerController.setVisibilityListener(componentListener);
            this.playerController.setPlayer(this.player != null ? this.player.getPlayer() : null);
        }
        this.controllerShowTimeoutMs = this.playerController == null ? 0 : controllerShowTimeoutMs;
        this.controllerAutoShow = controllerAutoShow;
    }

    public void setPlayer(ExoVideoPlayer player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.release();
        }
        this.player = player;
        if (player != null) {
            this.player.setVideoRenderSurfaceView(surfaceView);
        }
        if (this.playerController != null) {
            this.playerController.setPlayer(this.player != null ? this.player.getPlayer() : null);
        }
    }

    public void setControllerShowTimeoutMs(long controllerShowTimeoutMs) {
        this.controllerShowTimeoutMs = controllerShowTimeoutMs;
    }

    public void setControllerAutoShow(boolean controllerAutoShow) {
        this.controllerAutoShow = controllerAutoShow;
    }

    public void hideController() {
        if (playerController != null) {
            playerController.hide();
        }
    }

    public void showController() {
        showController(shouldShowControllerIndefinitely());
    }

    public void showController(boolean showIndefinitely) {
        if (playerController != null) {
            playerController.setShowTimeoutMs(showIndefinitely ? 0 : controllerShowTimeoutMs);
        }
    }

    private boolean shouldShowControllerIndefinitely() {
        if (player == null || player.getPlayer() == null) {
            return true;
        }
        int playbackState = player.getPlayer().getPlaybackState();
        return controllerAutoShow && (playbackState == Player.STATE_IDLE|| playbackState == Player.STATE_ENDED || !player.getPlayer().getPlayWhenReady());
    }

    private void doOnConfigurationChanged(int newConfig)
    {
        if (newConfig == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            if (isLandspace) return;
            isLandspace = true;
            VideoPlayerUtils.HideActionBar(getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ((Activity)getContext()).getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            if (playerController != null) {
                playerController.setFullscreenCheckedState(true);
            }

        } else if (newConfig == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            if (!isLandspace) return;
            isLandspace = false;
            ((Activity)getContext()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            VideoPlayerUtils.ShowActionBar(getContext());
            if (playerController != null) {
                playerController.setFullscreenCheckedState(false);
            }
        }
    }

    /****
     * 监听返回键 true 可以正常返回处理，false 切换到竖屏
     *
     * @return boolean boolean
     */
    public boolean onBackPressed() {
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((Activity)getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (playerController != null) playerController.setFullscreenCheckedState(false);
            doOnConfigurationChanged(Configuration.ORIENTATION_PORTRAIT);
            return false;
        } else {
            return true;
        }
    }


    private class ComponentListener implements PlayerControllerBarView.VisibilityListener, PlayerControllerBarView.FullscreenListener {

        @Override
        public void onVisibilityChange(int visibility)
        {

        }

        @Override
        public void onClickFullscreen()
        {
            int orientation = VideoPlayerUtils.GetOrientation(getContext());
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //横屏切竖屏
                ((Activity)getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                doOnConfigurationChanged(Configuration.ORIENTATION_PORTRAIT);
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //竖屏切横屏
                ((Activity)getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                doOnConfigurationChanged(Configuration.ORIENTATION_LANDSCAPE);
            }
        }
    }
}
