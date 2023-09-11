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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.flyzebra.ffplay.view.GlVideoView;
import com.flyzebra.mdrvset.activity.AdasSetActivity;
import com.flyzebra.mdrvset.bean.CalibInfo;
import com.flyzebra.mdrvset.bean.RtmpChannel;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.GsonUtil;
import com.flyzebra.utils.WifiUtil;
import com.flyzebra.utils.http.HttpResult;
import com.flyzebra.utils.http.HttpUtil;

public class BdsSetFragment extends Fragment {
    private CalibInfo calibInfo = new CalibInfo();
    private GlVideoView glVideoView;
    private RelativeLayout start_layout;
    private RelativeLayout line_layout;
    private Spinner bds_spinner;

    private ImageView bds_save_btn;
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
                mHandler.post(() -> activity.showMessage(R.string.note_wifi_connected));
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
                        tHandler.postDelayed(BdsSetFragment.this.playTask, 2000);
                    }
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                    tHandler.postDelayed(BdsSetFragment.this.playTask, 2000);
                }
            } else {
                tHandler.postDelayed(BdsSetFragment.this.playTask, 2000);
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
        return inflater.inflate(R.layout.fragment_bdsset, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        glVideoView = view.findViewById(R.id.gl_ffplay);
        start_layout = view.findViewById(R.id.fm_aiset_start_layout);
        line_layout = view.findViewById(R.id.fm_aiset_line_layout);
        start_layout.setVisibility(View.VISIBLE);
        line_layout.setVisibility(View.INVISIBLE);

        bds_spinner = view.findViewById(R.id.bds_spinner);
        bds_save_btn = view.findViewById(R.id.bds_save_btn);
        bds_spinner.setSelection(0);
        bds_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        bds_save_btn.setOnClickListener(v -> {
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
                                activity.showMessage(R.string.set_ok);
                            } else {
                                activity.showMessage(R.string.set_json_error);
                            }
                        } catch (Exception e) {
                            FlyLog.e(e.toString());
                        }
                    } else {
                        activity.showMessage(R.string.set_error_network);
                    }
                });
            });
        });

        bds_save_btn.setVisibility(View.INVISIBLE);
        calibration_start_btn = start_layout.findViewById(R.id.calibration_start_btn);
        calibration_start_btn.setOnClickListener(v -> {
            if (is_connected) {
                start_layout.setVisibility(View.INVISIBLE);
                line_layout.setVisibility(View.VISIBLE);
                bds_save_btn.setVisibility(View.VISIBLE);
            } else {
                AdasSetActivity activity = (AdasSetActivity) getActivity();
                if (activity != null) {
                    activity.showMessage(R.string.note_wifi_connected);
                }
                checkConnected();
            }
        });
        updateView();
    }

    private void showDialog(TextView textView, int textId, int resID) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_adsa_edit, null);
        TextView title = view.findViewById(R.id.dlg_text);
        EditText edit = view.findViewById(R.id.dlg_edit);
        edit.setText(textView.getText());
        edit.requestFocus();
        Button bt1 = view.findViewById(R.id.lg_dlg_bt1);
        title.setText(textId);
        final AlertDialog dlg = new AlertDialog.Builder(getActivity()).setView(view).show();
        bt1.setOnClickListener(v -> {
            dlg.dismiss();
        });
    }

    private void updateView() {

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
