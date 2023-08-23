package com.flyzebra.mdrvset.fm;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
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
import com.flyzebra.mdrvset.AdasSetActivity;
import com.flyzebra.mdrvset.Config;
import com.flyzebra.mdrvset.bean.CalibInfo;
import com.flyzebra.mdrvset.bean.RtmpChannel;
import com.flyzebra.mdrvset.view.AdasSetView;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.GsonUtil;
import com.flyzebra.utils.WifiUtil;
import com.flyzebra.utils.http.HttpResult;
import com.flyzebra.utils.http.HttpUtil;

public class AdasSetFragment1 extends Fragment {
    private CalibInfo calibInfo = new CalibInfo();
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
    private Button calibration_start_btn;
    private boolean is_connected = false;

    private int mLiveChannel = 0;
    public Runnable playTask = new Runnable() {
        @Override
        public void run() {
            AdasSetActivity activity = (AdasSetActivity) getActivity();
            if (activity == null) return;
            String gateway = WifiUtil.getGateway(activity);
            if (TextUtils.isEmpty(gateway)) {
                activity.showMessage(R.string.adas_note_wifi_connected);
                return;
            }
            String json = "{\"CMD\":\"LIVE_PREVIEW_RTMP\",\"DO\":[{\"Channel\":" + mLiveChannel + ",\"CMD\":\"PLAY\",\"STREAM_TYPE\":1},";
            final HttpResult result = HttpUtil.doPostJson("http://" + gateway + "/bin-cgi/mlg.cgi", json);
            if (result.code == 200) {
                try {
                    RtmpChannel.GetRtmpResult getRtmpResult = GsonUtil.json2Object(result.data, RtmpChannel.GetRtmpResult.class);
                    if (getRtmpResult != null && getRtmpResult.LIVE_PREVIEW_RTMP != null && getRtmpResult.LIVE_PREVIEW_RTMP.size() > 0) {
                        String playUrl = getRtmpResult.LIVE_PREVIEW_RTMP.get(0).RTMP_ADDR;
                        mHandler.post(() -> glVideoView.play(playUrl));
                    } else {
                        tHandler.postDelayed(AdasSetFragment1.this.playTask, 2000);
                    }
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                    tHandler.postDelayed(AdasSetFragment1.this.playTask, 2000);
                }
            } else {
                tHandler.postDelayed(AdasSetFragment1.this.playTask, 2000);
            }
        }
    };

    private static final HandlerThread httpThread = new HandlerThread("http_thread");

    static {
        httpThread.start();
    }

    private static final Handler tHandler = new Handler(httpThread.getLooper());
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

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
                glVideoView.stop();
                mLiveChannel = position + 1;
                tHandler.removeCallbacks(playTask);
                tHandler.post(playTask);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adasSetView.setMoveLisenter(new AdasSetView.MoveLisenter() {
            @Override
            public void notifyHorizon(int vaule) {
                adas_cali_horizon_text.setText(String.valueOf(calibInfo.horizon));
            }

            @Override
            public void notiryCarMiddle(int value) {
                adas_cali_carMiddle_text.setText(String.valueOf(calibInfo.carMiddle));
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
            if (calibInfo.horizon > 0) {
                calibInfo.horizon--;
                adas_cali_horizon_text.setText(String.valueOf(calibInfo.horizon));
                adasSetView.updateHorizonView();
            }
        });
        adas_cali_horizont_down.setOnClickListener(v -> {
            if (calibInfo.horizon < (Config.CAMERA_H - 1)) {
                calibInfo.horizon++;
                adas_cali_horizon_text.setText(String.valueOf(calibInfo.horizon));
                adasSetView.updateHorizonView();
            }
        });

        adas_cali_carMiddle_left.setOnClickListener(v -> {
            if (calibInfo.carMiddle > (-Config.CAMERA_W / 2) - 1) {
                calibInfo.carMiddle--;
                adas_cali_carMiddle_text.setText(String.valueOf(calibInfo.carMiddle));
                adasSetView.updateCarMiddleView();
            }
        });
        adas_cali_carMiddle_right.setOnClickListener(v -> {
            if (calibInfo.carMiddle < (Config.CAMERA_W) / 2 - 1) {
                calibInfo.carMiddle++;
                adas_cali_carMiddle_text.setText(String.valueOf(calibInfo.carMiddle));
                adasSetView.updateCarMiddleView();
            }
        });

        adas_cali_cameraHeight_left.setOnClickListener(v -> {
            if (calibInfo.cameraHeight > 0) {
                calibInfo.cameraHeight--;
                adas_cali_cameraHeight_text.setText(String.valueOf(calibInfo.cameraHeight));
            }
        });
        adas_cali_cameraHeight_right.setOnClickListener(v -> {
            calibInfo.cameraHeight++;
            adas_cali_cameraHeight_text.setText(String.valueOf(calibInfo.cameraHeight));
        });

        adas_cali_cameraToAxle_left.setOnClickListener(v -> {
            calibInfo.cameraToAxle--;
            adas_cali_cameraToAxle_text.setText(String.valueOf(calibInfo.cameraToAxle));
        });
        adas_cali_cameraToAxle_right.setOnClickListener(v -> {
            calibInfo.cameraToAxle++;
            adas_cali_cameraToAxle_text.setText(String.valueOf(calibInfo.cameraToAxle));
        });

        adas_cali_carWidth_left.setOnClickListener(v -> {
            if (calibInfo.carWidth > 0) {
                calibInfo.carWidth--;
                adas_cali_carWidth_text.setText(String.valueOf(calibInfo.carWidth));
            }
        });
        adas_cali_carWidth_right.setOnClickListener(v -> {
            calibInfo.carWidth++;
            adas_cali_carWidth_text.setText(String.valueOf(calibInfo.carWidth));
        });

        adas_cali_cameraToBumper_left.setOnClickListener(v -> {
            calibInfo.cameraToBumper--;
            adas_cali_cameraToBumper_text.setText(String.valueOf(calibInfo.cameraToBumper));
        });
        adas_cali_cameraToBumper_right.setOnClickListener(v -> {
            calibInfo.cameraToBumper++;
            adas_cali_cameraToBumper_text.setText(String.valueOf(calibInfo.cameraToBumper));
        });

        adas_cali_cameraToLeftWheel_left.setOnClickListener(v -> {
            calibInfo.cameraToLeftWheel--;
            adas_cali_cameraToLeftWheel_text.setText(String.valueOf(calibInfo.cameraToLeftWheel));
        });
        adas_cali_cameraToLeftWheel_right.setOnClickListener(v -> {
            calibInfo.cameraToLeftWheel++;
            adas_cali_cameraToLeftWheel_text.setText(String.valueOf(calibInfo.cameraToLeftWheel));
        });

        adas_save_btn.setOnClickListener(v -> {
            String gateway = WifiUtil.getGateway(getActivity());
            if (TextUtils.isEmpty(gateway)) {
                return;
            }
            CalibInfo.SetRequest setRequest = new CalibInfo.SetRequest();
            setRequest.DATA = calibInfo;
            String setString = GsonUtil.objectToJson(setRequest);
            tHandler.post(() -> {
                final HttpResult result = HttpUtil.doPostJson("http://" + gateway + "/bin-cgi/mlg.cgi", setString);
                mHandler.post(() -> {
                    AdasSetActivity activity = (AdasSetActivity) getActivity();
                    if (activity == null) return;
                    if (result.code == 200) {
                        try {
                            CalibInfo.SetResult data = GsonUtil.json2Object(result.data, CalibInfo.SetResult.class);
                            if (!TextUtils.isEmpty(data.ErrNO) && data.ErrNO.equals("0000")) {
                                activity.showMessage(R.string.adas_set_ok);
                            } else {
                                activity.showMessage(R.string.adas_set_json_error);
                            }
                        } catch (Exception e) {
                            FlyLog.e(e.toString());
                        }
                    } else {
                        activity.showMessage(R.string.adas_set_error_network);
                    }
                });
            });
        });

        adas_save_btn.setVisibility(View.INVISIBLE);
        calibration_start_btn = start_layout.findViewById(R.id.calibration_start_btn);
        calibration_start_btn.setOnClickListener(v -> {
            if (is_connected) {
                start_layout.setVisibility(View.INVISIBLE);
                line_layout.setVisibility(View.VISIBLE);
                adas_save_btn.setVisibility(View.VISIBLE);
            } else {
                AdasSetActivity activity = (AdasSetActivity) getActivity();
                if (activity != null) {
                    activity.showMessage(R.string.adas_note_wifi_connected);
                }
                checkConnected();
            }
        });
        updateView();
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
                calibInfo.horizon = Integer.parseInt(edit.getText().toString());
                adasSetView.updateHorizonView();
            } else if (resID == R.id.adas_cali_carMiddle_text) {
                calibInfo.carMiddle = Integer.parseInt(edit.getText().toString());
                adasSetView.updateCarMiddleView();
            } else if (resID == R.id.adas_cali_cameraHeight_text) {
                calibInfo.cameraHeight = Integer.parseInt(edit.getText().toString());
            } else if (resID == R.id.adas_cali_cameraToAxle_text) {
                calibInfo.cameraToAxle = Integer.parseInt(edit.getText().toString());
            } else if (resID == R.id.adas_cali_carWidth_text) {
                calibInfo.carWidth = Integer.parseInt(edit.getText().toString());
            } else if (resID == R.id.adas_cali_cameraToBumper_text) {
                calibInfo.cameraToBumper = Integer.parseInt(edit.getText().toString());
            } else if (resID == R.id.adas_cali_cameraToLeftWheel_text) {
                calibInfo.cameraToLeftWheel = Integer.parseInt(edit.getText().toString());
            }
            dlg.dismiss();
        });
    }

    private void updateView() {
        adas_cali_horizon_text.setText(String.valueOf(calibInfo.horizon));
        adas_cali_carMiddle_text.setText(String.valueOf(calibInfo.carMiddle));
        adas_cali_cameraHeight_text.setText(String.valueOf(calibInfo.cameraHeight));
        adas_cali_cameraToAxle_text.setText(String.valueOf(calibInfo.cameraToAxle));
        adas_cali_carWidth_text.setText(String.valueOf(calibInfo.carWidth));
        adas_cali_cameraToBumper_text.setText(String.valueOf(calibInfo.cameraToBumper));
        adas_cali_cameraToLeftWheel_text.setText(String.valueOf(calibInfo.cameraToLeftWheel));
        adasSetView.upCalibInfo(calibInfo);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkConnected();
    }

    private void checkConnected() {
        is_connected = false;
        String gateway = WifiUtil.getGateway(getActivity());
        if (!TextUtils.isEmpty(gateway)) {
            CalibInfo.GetRequest getRequest = new CalibInfo.GetRequest();
            String getString = GsonUtil.objectToJson(getRequest);
            tHandler.removeCallbacksAndMessages(null);
            tHandler.post(() -> {
                final HttpResult result = HttpUtil.doPostJson("http://" + gateway + "/bin-cgi/mlg.cgi", getString);
                mHandler.removeCallbacksAndMessages(null);
                mHandler.post(() -> {
                    if (result.code == 200) {
                        try {
                            CalibInfo.GetResult data = GsonUtil.json2Object(result.data, CalibInfo.GetResult.class);
                            if (!TextUtils.isEmpty(data.ErrNO) && data.ErrNO.equals("0000")) {
                                calibInfo = data.DATA;
                                updateView();
                                is_connected = true;
                            }
                        } catch (Exception e) {
                            FlyLog.e(e.toString());
                        }
                    }
                });
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        tHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
    }
}
