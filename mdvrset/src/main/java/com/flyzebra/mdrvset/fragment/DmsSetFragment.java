package com.flyzebra.mdrvset.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.flyzebra.ffplay.view.GlVideoView;
import com.flyzebra.mdrvset.activity.ArcsoftSetActivity;
import com.flyzebra.mdrvset.adapder.SpinnerAdapater;
import com.flyzebra.mdrvset.http.AdasInfo;
import com.flyzebra.mdrvset.http.RtmpInfo;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.GsonUtil;
import com.flyzebra.utils.WifiUtil;
import com.flyzebra.utils.http.HttpResult;
import com.flyzebra.utils.http.HttpUtil;

public class DmsSetFragment extends Fragment {
    private GlVideoView glVideoView;
    private Spinner channel_spinner;

    private int mLiveChannel = 0;

    private static final HandlerThread httpThread = new HandlerThread("http_thread");

    static {
        httpThread.start();
    }

    private static final Handler tHandler = new Handler(httpThread.getLooper());
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    public Runnable playTask = new Runnable() {
        @Override
        public void run() {
            ArcsoftSetActivity activity = (ArcsoftSetActivity) getActivity();
            if (activity == null) return;
            String gateway = WifiUtil.getGateway(activity);
            if (TextUtils.isEmpty(gateway)) {
                mHandler.post(() -> activity.showMessage(R.string.note_wifi_connected));
                return;
            }
            String str = String.format(RtmpInfo.GetRequest, mLiveChannel + 1);
            final HttpResult result = HttpUtil.doPostJson("http://" + gateway + "/bin-cgi/mlg.cgi", str);
            if (result.code == 200) {
                try {
                    RtmpInfo.GetRtmpResult getRtmpResult = GsonUtil.json2Object(result.data, RtmpInfo.GetRtmpResult.class);
                    if (getRtmpResult != null && getRtmpResult.LIVE_PREVIEW_RTMP != null && getRtmpResult.LIVE_PREVIEW_RTMP.size() > 0) {
                        String playUrl = getRtmpResult.LIVE_PREVIEW_RTMP.get(0).RTMP_ADDR;
                        mHandler.post(() -> glVideoView.play(playUrl));
                    } else {
                        tHandler.postDelayed(DmsSetFragment.this.playTask, 3000);
                    }
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                    tHandler.postDelayed(DmsSetFragment.this.playTask, 3000);
                }
            } else {
                tHandler.postDelayed(DmsSetFragment.this.playTask, 3000);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dmsset, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        glVideoView = view.findViewById(R.id.gl_ffplay);
        channel_spinner = view.findViewById(R.id.dms_channel_spinner);
        channel_spinner.setAdapter(new SpinnerAdapater(getContext(), getResources().getStringArray(R.array.spinnerchannelnum)));
        channel_spinner.setSelection(0);
        channel_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLiveChannel = position;
                tHandler.removeCallbacks(playTask);
                tHandler.post(playTask);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        checkConnected();
    }

    private void checkConnected() {
        String gateway = WifiUtil.getGateway(getActivity());
        if (!TextUtils.isEmpty(gateway)) {
            AdasInfo.GetRequest getRequest = new AdasInfo.GetRequest();
            String postStr = GsonUtil.objectToJson(getRequest);
            tHandler.removeCallbacksAndMessages(null);
            tHandler.post(() -> {
                final HttpResult result = HttpUtil.doPostJson("http://" + gateway + "/bin-cgi/mlg.cgi", postStr);
                mHandler.removeCallbacksAndMessages(null);
                mHandler.post(() -> {
                    if (result.code != 200) {
                        mHandler.post(() -> {
                            ArcsoftSetActivity activity = (ArcsoftSetActivity) getActivity();
                            if (activity != null) {
                                activity.showMessage(R.string.note_wifi_connected);
                            }
                        });
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
