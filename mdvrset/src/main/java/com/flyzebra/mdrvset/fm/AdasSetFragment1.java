package com.flyzebra.mdrvset.fm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.flyzebra.ffplay.view.GlVideoView;
import com.flyzebra.mdrvset.Config;
import com.flyzebra.mdrvset.view.AdasSetView;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;

public class AdasSetFragment1 extends Fragment {
    private GlVideoView glVideoView;

    private RelativeLayout start_layout;
    private RelativeLayout line_layout;
    private Spinner adas_spinner;
    private AdasSetView adasSetView;

    private TextView adas_cali_horizon_text;
    private TextView adas_cali_carMiddle_text;
    private TextView adas_cali_cameraHeight_text;
    private TextView adas_cali_cameraToAxle_text;
    private TextView adas_cali_carWidth_text;
    private TextView adas_cali_cameraToBumper_text;
    private TextView adas_cali_cameraToLeftWheel_text;

    private ImageButton adas_cali_horizont_up;
    private ImageButton adas_cali_horizont_down;
    private ImageButton adas_cali_carMiddle_left;
    private ImageButton adas_cali_carMiddle_right;
    private ImageButton adas_cali_cameraHeight_left;
    private ImageButton adas_cali_cameraHeight_right;
    private ImageButton adas_cali_cameraToAxle_left;
    private ImageButton adas_cali_cameraToAxle_right;
    private ImageButton adas_cali_carWidth_left;
    private ImageButton adas_cali_carWidth_right;
    private ImageButton adas_cali_cameraToBumper_left;
    private ImageButton adas_cali_cameraToBumper_right;
    private ImageButton adas_cali_cameraToLeftWheel_left;
    private ImageButton adas_cali_cameraToLeftWheel_right;

    private ImageView adas_save_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adasset1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        glVideoView = view.findViewById(R.id.gl_ffplay);
        start_layout = view.findViewById(R.id.fm_aiset_start_layout);
        line_layout = view.findViewById(R.id.fm_aiset_line_layout);
        start_layout.setVisibility(View.VISIBLE);
        line_layout.setVisibility(View.INVISIBLE);
        Button startButton = start_layout.findViewById(R.id.calibration_start_btn);
        startButton.setOnClickListener(v -> {
            start_layout.setVisibility(View.INVISIBLE);
            line_layout.setVisibility(View.VISIBLE);
        });

        adas_spinner = view.findViewById(R.id.adas_spinner);
        adasSetView = view.findViewById(R.id.adasSetView);

        adas_cali_horizon_text = view.findViewById(R.id.adas_cali_horizon_text);
        adas_cali_carMiddle_text = view.findViewById(R.id.adas_cali_carMiddle_text);
        adas_cali_cameraHeight_text = view.findViewById(R.id.adas_cali_cameraHeight_text);
        adas_cali_cameraToAxle_text = view.findViewById(R.id.adas_cali_cameraToAxle_text);
        adas_cali_carWidth_text = view.findViewById(R.id.adas_cali_carWidth_text);
        adas_cali_cameraToBumper_text = view.findViewById(R.id.adas_cali_cameraToBumper_text);
        adas_cali_cameraToLeftWheel_text = view.findViewById(R.id.adas_cali_cameraToLeftWheel_text);
        adas_cali_horizont_up = view.findViewById(R.id.adas_cali_horizont_up);
        adas_cali_horizont_down = view.findViewById(R.id.adas_cali_horizont_down);
        adas_cali_carMiddle_left = view.findViewById(R.id.adas_cali_carMiddle_left);
        adas_cali_carMiddle_right = view.findViewById(R.id.adas_cali_carMiddle_right);
        adas_cali_cameraHeight_left = view.findViewById(R.id.adas_cali_cameraHeight_left);
        adas_cali_cameraHeight_right = view.findViewById(R.id.adas_cali_cameraHeight_right);
        adas_cali_cameraToAxle_left = view.findViewById(R.id.adas_cali_cameraToAxle_left);
        adas_cali_cameraToAxle_right = view.findViewById(R.id.adas_cali_cameraToAxle_right);
        adas_cali_carWidth_left = view.findViewById(R.id.adas_cali_carWidth_left);
        adas_cali_carWidth_right = view.findViewById(R.id.adas_cali_carWidth_right);
        adas_cali_cameraToBumper_left = view.findViewById(R.id.adas_cali_cameraToBumper_left);
        adas_cali_cameraToBumper_right = view.findViewById(R.id.adas_cali_cameraToBumper_right);
        adas_cali_cameraToLeftWheel_left = view.findViewById(R.id.adas_cali_cameraToLeftWheel_left);
        adas_cali_cameraToLeftWheel_right = view.findViewById(R.id.adas_cali_cameraToLeftWheel_right);

        adas_save_btn = view.findViewById(R.id.adas_save_btn);

        adas_spinner.setSelection(0);
        adas_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                glVideoView.play("rtsp://192.168.137.126:8554/camera" + (position + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adasSetView.setMoveLisenter(new AdasSetView.MoveLisenter() {
            @Override
            public void notifyHorizon(int vaule) {
                adas_cali_horizon_text.setText(String.valueOf(Config.adasCalibInfo.horizon));
            }

            @Override
            public void notiryCarMiddle(int value) {
                adas_cali_carMiddle_text.setText(String.valueOf(Config.adasCalibInfo.carMiddle));
            }
        });

        adas_cali_horizon_text.setOnClickListener(v -> showDialog((TextView) v, R.string.horizontal_line2, R.id.adas_cali_horizon_text));
        adas_cali_carMiddle_text.setOnClickListener(v -> showDialog((TextView) v, R.string.car_central_line2, R.id.adas_cali_carMiddle_text));
        adas_cali_cameraHeight_text.setOnClickListener(v -> showDialog((TextView) v, R.string.camera_height, R.id.adas_cali_cameraHeight_text));
        adas_cali_cameraToAxle_text.setOnClickListener(v -> showDialog((TextView) v, R.string.camera_to_axle, R.id.adas_cali_cameraToAxle_text));
        adas_cali_carWidth_text.setOnClickListener(v -> showDialog((TextView) v, R.string.car_width, R.id.adas_cali_carWidth_text));
        adas_cali_cameraToBumper_text.setOnClickListener(v -> showDialog((TextView) v, R.string.camera_to_bumper, R.id.adas_cali_cameraToBumper_text));
        adas_cali_cameraToLeftWheel_text.setOnClickListener(v -> showDialog((TextView) v, R.string.camera_to_left_wheel, R.id.adas_cali_cameraToLeftWheel_text));

        adas_cali_horizont_up.setOnClickListener(v -> {
            if (Config.adasCalibInfo.horizon > 0) {
                Config.adasCalibInfo.horizon--;
                adas_cali_horizon_text.setText(String.valueOf(Config.adasCalibInfo.horizon));
                adasSetView.updateHorizonView();
            }
        });
        adas_cali_horizont_down.setOnClickListener(v -> {
            if (Config.adasCalibInfo.horizon < (Config.CAMERA_H - 1)) {
                Config.adasCalibInfo.horizon++;
                adas_cali_horizon_text.setText(String.valueOf(Config.adasCalibInfo.horizon));
                adasSetView.updateHorizonView();
            }
        });

        adas_cali_carMiddle_left.setOnClickListener(v -> {
            if (Config.adasCalibInfo.carMiddle > (-Config.CAMERA_W / 2) - 1) {
                Config.adasCalibInfo.carMiddle--;
                adas_cali_carMiddle_text.setText(String.valueOf(Config.adasCalibInfo.carMiddle));
                adasSetView.updateCarMiddleView();
            }
        });
        adas_cali_carMiddle_right.setOnClickListener(v -> {
            if (Config.adasCalibInfo.carMiddle < (Config.CAMERA_W) / 2 - 1) {
                Config.adasCalibInfo.carMiddle++;
                adas_cali_carMiddle_text.setText(String.valueOf(Config.adasCalibInfo.carMiddle));
                adasSetView.updateCarMiddleView();
            }
        });

        adas_cali_cameraHeight_left.setOnClickListener(v -> {
            if (Config.adasCalibInfo.cameraHeight > 0) {
                Config.adasCalibInfo.cameraHeight--;
                adas_cali_cameraHeight_text.setText(String.valueOf(Config.adasCalibInfo.cameraHeight));
            }
        });
        adas_cali_cameraHeight_right.setOnClickListener(v -> {
            Config.adasCalibInfo.cameraHeight++;
            adas_cali_cameraHeight_text.setText(String.valueOf(Config.adasCalibInfo.cameraHeight));
        });

        adas_cali_cameraToAxle_left.setOnClickListener(v -> {
            Config.adasCalibInfo.cameraToAxle--;
            adas_cali_cameraToAxle_text.setText(String.valueOf(Config.adasCalibInfo.cameraToAxle));
        });
        adas_cali_cameraToAxle_right.setOnClickListener(v -> {
            Config.adasCalibInfo.cameraToAxle++;
            adas_cali_cameraToAxle_text.setText(String.valueOf(Config.adasCalibInfo.cameraToAxle));
        });

        adas_cali_carWidth_left.setOnClickListener(v -> {
            if (Config.adasCalibInfo.carWidth > 0) {
                Config.adasCalibInfo.carWidth--;
                adas_cali_carWidth_text.setText(String.valueOf(Config.adasCalibInfo.carWidth));
            }
        });
        adas_cali_carWidth_right.setOnClickListener(v -> {
            Config.adasCalibInfo.carWidth++;
            adas_cali_carWidth_text.setText(String.valueOf(Config.adasCalibInfo.carWidth));
        });

        adas_cali_cameraToBumper_left.setOnClickListener(v -> {
            Config.adasCalibInfo.cameraToBumper--;
            adas_cali_cameraToBumper_text.setText(String.valueOf(Config.adasCalibInfo.cameraToBumper));
        });
        adas_cali_cameraToBumper_right.setOnClickListener(v -> {
            Config.adasCalibInfo.cameraToBumper++;
            adas_cali_cameraToBumper_text.setText(String.valueOf(Config.adasCalibInfo.cameraToBumper));
        });

        adas_cali_cameraToLeftWheel_left.setOnClickListener(v -> {
            Config.adasCalibInfo.cameraToLeftWheel--;
            adas_cali_cameraToLeftWheel_text.setText(String.valueOf(Config.adasCalibInfo.cameraToLeftWheel));
        });
        adas_cali_cameraToLeftWheel_right.setOnClickListener(v -> {
            Config.adasCalibInfo.cameraToLeftWheel++;
            adas_cali_cameraToLeftWheel_text.setText(String.valueOf(Config.adasCalibInfo.cameraToLeftWheel));
        });

        adas_save_btn.setOnClickListener(v -> {
            FlyLog.e("");
        });

        update();
    }

    private void showDialog(TextView textView, int textId, int resID) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_adsa_edit, null);
        TextView title = view.findViewById(R.id.adas_dlg_text);
        EditText edit = view.findViewById(R.id.adas_dlg_edit);
        edit.setText(textView.getText());
        edit.requestFocus();
        Button bt1 = view.findViewById(R.id.lg_dlg_bt1);
        title.setText(textId);
        final AlertDialog dlg = new AlertDialog.Builder(getActivity()).setView(view).show();
        bt1.setOnClickListener(v -> {
            textView.setText(edit.getText());
            if (resID == R.id.adas_cali_horizon_text) {
                Config.adasCalibInfo.horizon = Integer.parseInt(edit.getText().toString());
                adasSetView.updateHorizonView();
            } else if (resID == R.id.adas_cali_carMiddle_text) {
                Config.adasCalibInfo.carMiddle = Integer.parseInt(edit.getText().toString());
                adasSetView.updateCarMiddleView();
            } else if (resID == R.id.adas_cali_cameraHeight_text) {
                Config.adasCalibInfo.cameraHeight = Integer.parseInt(edit.getText().toString());
            } else if (resID == R.id.adas_cali_cameraToAxle_text) {
                Config.adasCalibInfo.cameraToAxle = Integer.parseInt(edit.getText().toString());
            } else if (resID == R.id.adas_cali_carWidth_text) {
                Config.adasCalibInfo.carWidth = Integer.parseInt(edit.getText().toString());
            } else if (resID == R.id.adas_cali_cameraToBumper_text) {
                Config.adasCalibInfo.cameraToBumper = Integer.parseInt(edit.getText().toString());
            } else if (resID == R.id.adas_cali_cameraToLeftWheel_text) {
                Config.adasCalibInfo.cameraToLeftWheel = Integer.parseInt(edit.getText().toString());
            }
            dlg.dismiss();
        });
    }

    private void update() {
        adas_cali_horizon_text.setText(String.valueOf(Config.adasCalibInfo.horizon));
        adas_cali_carMiddle_text.setText(String.valueOf(Config.adasCalibInfo.carMiddle));
        adas_cali_cameraHeight_text.setText(String.valueOf(Config.adasCalibInfo.cameraHeight));
        adas_cali_cameraToAxle_text.setText(String.valueOf(Config.adasCalibInfo.cameraToAxle));
        adas_cali_carWidth_text.setText(String.valueOf(Config.adasCalibInfo.carWidth));
        adas_cali_cameraToBumper_text.setText(String.valueOf(Config.adasCalibInfo.cameraToBumper));
        adas_cali_cameraToLeftWheel_text.setText(String.valueOf(Config.adasCalibInfo.cameraToLeftWheel));
    }
}
