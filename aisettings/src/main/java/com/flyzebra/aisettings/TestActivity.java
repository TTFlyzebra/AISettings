package com.flyzebra.aisettings;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flyzebra.ffplay.GlVideoView;

public class TestActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private GlVideoView glVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_aisettings);

        glVideoView = findViewById(R.id.gl_ffplay);
        //glVideoView.playUrl("rtsp://192.168.3.17:11554/chn=0/type=0");
        glVideoView.playUrl("rtsp://192.168.137.126:8554/camera1");
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }
}