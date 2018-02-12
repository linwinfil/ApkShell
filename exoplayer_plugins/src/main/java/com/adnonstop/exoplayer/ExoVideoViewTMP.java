// package com.adnonstop.exoplayer;
//
// import android.app.Activity;
// import android.content.Context;
// import android.os.Build;
// import android.os.Environment;
// import android.view.Gravity;
// import android.view.TextureView;
// import android.view.View;
// import android.view.ViewGroup;
// import android.view.WindowManager;
// import android.widget.FrameLayout;
// import android.widget.ProgressBar;
//
// import com.google.android.exoplayer2.upstream.DataSource;
// import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
// import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
// import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
// import com.google.android.exoplayer2.upstream.cache.SimpleCache;
// import com.google.android.exoplayer2.util.Util;
//
// import java.io.File;
// import java.util.ArrayList;
//
// /**
//  * @author lmx
//  *         Created by lmx on 2018-02-07.
//  */
//
// public class ExoVideoViewTMP extends AspectRatioFrameLayout implements
//         IDataSourceFactory, IPlayerViewListener, IMediaController.MediaPlayerControl
// {
//     protected ExoVideoPlayer mPlayer;
//     protected TextureView mTextureView;
//     protected ProgressBar mProgressBar;
//     private boolean isRealse;
//
//     private int mDefSystemUiVisibility = -1;
//
//     private boolean isLooping = true;
//
//     public ExoVideoViewTMP(Context context)
//     {
//         super(context);
//         initScreen();
//         initPlayer();
//         initUI();
//     }
//
//     private void initScreen()
//     {
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//         {
//             View decorView = ((Activity) getContext()).getWindow().getDecorView();
//             mDefSystemUiVisibility = decorView.getSystemUiVisibility();
//             decorView.setSystemUiVisibility(
//                     View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                             | View.SYSTEM_UI_FLAG_FULLSCREEN
//                             | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//         }
//         ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//     }
//
//     private void resetScreen()
//     {
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//             View decorView = ((Activity) getContext()).getWindow().getDecorView();
//             if (mDefSystemUiVisibility != -1) {
//                 decorView.setSystemUiVisibility(mDefSystemUiVisibility);
//             }
//         }
//         ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//     }
//
//     private void initPlayer()
//     {
//         mPlayer = new ExoVideoPlayer(getContext(), this);
//         mPlayer.setPlayerViewListener(this);
//     }
//
//     private void initUI()
//     {
//         setResizeMode(RESIZE_MODE_FIT);
//         FrameLayout.LayoutParams params;
//
//         mTextureView = new TextureView(getContext());
//         mTextureView.setFocusable(true);
//         mTextureView.setFocusableInTouchMode(true);
//         mTextureView.requestFocus();
//         params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//         params.gravity = Gravity.CENTER;
//         this.addView(mTextureView, params);
//         mPlayer.setVideoTextureView(mTextureView);
//
//         mProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
//         mProgressBar.setVisibility(View.GONE);
//         params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//         params.gravity = Gravity.CENTER;
//         this.addView(mProgressBar, params);
//     }
//
//     public void showLoadViewState(int visibility)
//     {
//         if (mProgressBar != null)
//         {
//             mProgressBar.setVisibility(visibility);
//         }
//     }
//
//     public void setLooping(boolean looping) {
//         isLooping = looping;
//     }
//
//     /**
//      * @param videoPath 视频路径
//      */
//     public void setVideoPath(ArrayList<String> videoPath)
//     {
//
//         if (mPlayer != null && videoPath != null && videoPath.size() > 0)
//         {
//             ArrayList<DefaultMediaSource> list = new ArrayList<>();
//             for (String path : videoPath) {
//                 DefaultMediaSource defaultMediaSource = new DefaultMediaSource();
//                 defaultMediaSource.setVideoUri(path);
//                 list.add(defaultMediaSource);
//             }
//             mPlayer.setDataSource(list);
//         }
//     }
//
//     @Override
//     public boolean isPlaying() {
//         return mPlayer != null && mPlayer.isPlaying();
//     }
//
//     @Override
//     public int getBufferPercentage()
//     {
//         return mPlayer != null ? mPlayer.getBufferPercentage() : 0;
//     }
//
//
//     public void resume() {
//     }
//
//
//     @Override
//     public void pause() {
//         showLoadViewState(View.GONE);
//         if (mPlayer != null) {
//             mPlayer.pause();
//         }
//     }
//
//     @Override
//     public long getDuration()
//     {
//         return mPlayer != null ? mPlayer.getDuration() : 0;
//     }
//
//     @Override
//     public long getCurrentPosition()
//     {
//         return mPlayer != null ? mPlayer.getCurrentPosition():0;
//     }
//
//
//     @Override
//     public void start()
//     {
//         if (mPlayer != null) {
//             mPlayer.start();
//         }
//     }
//
//     @Override
//     public void seekTo(long millisecond)
//     {
//         if (mPlayer != null) {
//             mPlayer.seekTo(millisecond);
//         }
//     }
//
//     /**
//      * 预载player
//      */
//     public void prepared()
//     {
//         if (mPlayer != null) {
//             mPlayer.prepare();
//         }
//     }
//
//     public void release()
//     {
//         if (mPlayer != null) mPlayer.release();
//         resetScreen();
//         isRealse = true;
//         mPlayer = null;
//     }
//
//
//
//
//     // ----- IDataSourceFactory -----
//     @Override
//     public DataSource.Factory getDataSourceFactory()
//     {
//         // String cachePath = Environment.getExternalStorageDirectory() + File.separator + "exo_player_plugins";
//         // return new DefaultCacheDataSourceFactory(getContext(), cachePath, null);
//
//         File file = new File(Environment.getExternalStorageDirectory() + File.separator + "exo_player_plugins");
//         SimpleCache simpleCache = new SimpleCache(file, new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 50));
//         DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), getContext().getApplicationContext().getPackageName()));
//         return new CacheDataSourceFactory(simpleCache, dataSourceFactory);
//     }
//
//
//     // ----- IPlayerViewListener -----
//     @Override
//     public void showLoadStateView(int visibility)
//     {
//         if (mProgressBar != null) {
//             mProgressBar.setVisibility(visibility);
//         }
//     }
//
//     @Override
//     public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio)
//     {
//         setAspectRatio(height == 0 ? 1 : (width * pixelWidthHeightRatio) / height);
//     }
// }
