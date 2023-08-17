package com.flyzebra.aisettings;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flyzebra.aisettings.databinding.ActivityMainBinding;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private ActivityMainBinding binding;
    private TextureView textureView;
    private IjkMediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        textureView = binding.rtmpTexture;
        textureView.setSurfaceTextureListener(this);
        player = new IjkMediaPlayer();
        player.setOnErrorListener((mp, what, extra) -> {
            //FlyLog.d("Mediaplayer onError, what=%d, extra=%d", what, extra);
            return false;
        });
        player.setOnVideoSizeChangedListener((iMediaPlayer, w, h, i1, i2) -> {
            //FlyLog.d("Mediaplayer onVideoSizeChanged, %dx%d", w, h);
        });
        player.setOnPreparedListener(mp -> {
            player.start();
            //FlyLog.d("Mediaplayer onPrepared.");
        });
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        player.setSurface(new Surface(surface));
        player.setLooping(true);
        try {
            player.setDataSource("rtsp://192.168.3.8:8554/live/869409066940142/camera1");
            //player.setDataSource("rtsp://192.168.137.126:8554/camera1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        player.prepareAsync();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }
}