package com.example.opengl.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;

import com.example.opengl.R;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TextureVideoActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener {

    Surface surface;
    TextureView textureView;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_video);

        textureView = findViewById(R.id.texture_view);
        textureView.setSurfaceTextureListener(this);

        findViewById(R.id.btn_open_video).setOnClickListener(view -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("video/*");
            this.startActivityForResult(photoPickerIntent, 0x22);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == 0x22 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    if (textureView.isAvailable()) {
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.setOnPreparedListener(null);
                            mediaPlayer.release();
                        }
                        if (surface != null) surface.release();
                        surface = new Surface(textureView.getSurfaceTexture());
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setLooping(true);
                        mediaPlayer.setSurface(surface);
                        mediaPlayer.setDataSource(this, uri);
                        mediaPlayer.setOnPreparedListener(this);
                        mediaPlayer.prepareAsync();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (surface != null) {
            surface.release();
            surface = null;
        }
        textureView.setSurfaceTextureListener(null);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}