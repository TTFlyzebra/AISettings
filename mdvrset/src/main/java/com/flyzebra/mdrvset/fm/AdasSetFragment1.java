package com.flyzebra.mdrvset.fm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.flyzebra.ffplay.GlVideoView;
import com.flyzebra.mdvrset.R;

public class AdasSetFragment1 extends Fragment{
    private GlVideoView glVideoView;
    private RelativeLayout start_layout;
    private RelativeLayout line_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adasset1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        glVideoView = view.findViewById(R.id.gl_ffplay);
        //glVideoView.playUrl("rtsp://192.168.3.17:11554/chn=0/type=0");
        glVideoView.playUrl("rtsp://192.168.137.126:8554/camera1");

        start_layout = view.findViewById(R.id.fm_aiset_start_layout);
        line_layout = view.findViewById(R.id.fm_aiset_line_layout);

        start_layout.setVisibility(View.VISIBLE);
        line_layout.setVisibility(View.INVISIBLE);

        Button startButton = start_layout.findViewById(R.id.calibration_start_btn);
        startButton.setOnClickListener(v -> {
            start_layout.setVisibility(View.INVISIBLE);
            line_layout.setVisibility(View.VISIBLE);
        });
    }
}
