package com.adnonstop.exoplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.adnonstop.R;

/**
 * @author lmx
 *         Created by lmx on 2018-02-10.
 */

public class ExoVideoView extends FrameLayout
{
    private static final int SURFACE_TYPE_NONE = 0;
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;

    private AspectRatioFrameLayout contentFrame;
    private ExoVideoPlayer player;

    private View surfaceView;

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
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ExoVideoView, 0, 0);
            try {
                resizeMode = typedArray.getInt(R.styleable.ExoVideoView_resize_mode, resizeMode);
                surfaceType = typedArray.getInt(R.styleable.ExoVideoView_surface_type, surfaceType);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            finally {
                typedArray.recycle();
            }
        }
        LayoutInflater.from(context).inflate(playerViewContentFrameId, this);

        contentFrame = findViewById(R.id.exo_view_content_frame);
        contentFrame.setResizeMode(resizeMode);

        if (contentFrame != null && surfaceType != SURFACE_TYPE_NONE) {
            surfaceView = surfaceType == SURFACE_TYPE_SURFACE_VIEW ? new SurfaceView(context) : new TextureView(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            contentFrame.addView(surfaceView, 0, params);
        } else {
            surfaceView = null;
        }
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
    }
}
