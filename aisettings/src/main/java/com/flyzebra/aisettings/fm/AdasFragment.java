package com.flyzebra.aisettings.fm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.flyzebra.aisettings.R;
import com.flyzebra.ffplay.GlVideoView;

public class AdasFragment extends Fragment{

    private GlVideoView glVideoView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aisettings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        glVideoView = view.findViewById(R.id.gl_ffplay);
        //glVideoView.playUrl("rtsp://192.168.3.17:11554/chn=0/type=0");
        glVideoView.playUrl("rtsp://192.168.137.126:8554/camera1");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
