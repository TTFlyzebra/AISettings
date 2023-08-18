package com.flyzebra.ffplay.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.flyzebra.ffplay.AudioPlayer;
import com.flyzebra.ffplay.FfPlayer;
import com.flyzebra.ffplay.IFfPlayer;
import com.flyzebra.utils.FlyLog;


/**
 * Author: FlyZebra
 * Time: 18-5-14 下午9:00.
 * Discription: This is GlVideoView
 */
public class GlVideoView extends GLSurfaceView implements SurfaceHolder.Callback, IFfPlayer {
    private GlRenderI420 glRender;
    private FfPlayer ffplayer;
    private AudioPlayer audioPlayer;
    private String playUrl;

    public GlVideoView(Context context) {
        this(context, null);
    }

    public GlVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, (int) (width * 9f / 16f));
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        glRender = new GlRenderI420(context);
        setRenderer(glRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        super.surfaceCreated(surfaceHolder);
        ffplayer = new FfPlayer();
        if (!TextUtils.isEmpty(playUrl)) {
            ffplayer.play(this, playUrl);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        super.surfaceChanged(surfaceHolder, i, i1, i2);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        super.surfaceDestroyed(surfaceHolder);
        if (ffplayer != null) {
            ffplayer.stop();
            ffplayer = null;
        }
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer = null;
        }
    }

    @Override
    public void onVideoDecode(byte[] videoBytes, int size, int widht, int height) {
        glRender.upFrame(videoBytes, size, widht, height);
        requestRender();
    }


    @Override
    public void onAudioDecode(byte[] audioBytes, int size, int sampleRateInHz, int channelConfig, int audioFormat) {
        try {
            if (audioPlayer == null) {
                audioPlayer = new AudioPlayer(sampleRateInHz, channelConfig, audioFormat);
            }
            audioPlayer.write(audioBytes, size);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onComplete() {
        if (!TextUtils.isEmpty(playUrl)) {
            playUrl(playUrl);
        }
    }

    public void playUrl(String url) {
        playUrl = url;
        if (ffplayer != null) {
            ffplayer.stop();
        }
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer = null;
        }
        if (ffplayer != null) {
            ffplayer.play(this, playUrl);
        }
    }
}
