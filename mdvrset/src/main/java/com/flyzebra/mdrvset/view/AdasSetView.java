/**
 * FileName: MoveRelativeLayout
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/7/25 8:12
 * Description:
 */
package com.flyzebra.mdrvset.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.flyzebra.mdrvset.Config;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;

public class AdasSetView extends RelativeLayout{
    private RelativeLayout horizonView;
    private RelativeLayout carMiddleView;
    private RelativeLayout carMiddleView_child;
    private int width;
    private int height;

    private float horizon_down;
    private float horizon_y;

    private float carMiddle_down;
    private float carMiddle_x;

    public AdasSetView(Context context) {
        this(context, null);
    }

    public AdasSetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdasSetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {

        horizonView = new RelativeLayout(context);
        RelativeLayout.LayoutParams params_h = new RelativeLayout.LayoutParams(-1, 100);
        addView(horizonView, params_h);
        horizonView.setBackgroundResource(R.drawable.horizon_rectangle);
        horizonView.setOnClickListener(v -> {
        });
        horizonView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    horizon_y = event.getRawY();
                    horizon_down = Config.adasCalibInfo.horizon;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Config.adasCalibInfo.horizon = (int) (horizon_down + (event.getRawY() - horizon_y) * Config.CAMERA_H / height);
                    updateHorizonView();
                    if(moveLisenter!=null) moveLisenter.notifyHorizon(Config.adasCalibInfo.horizon);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        });

        carMiddleView = new RelativeLayout(context);
        carMiddleView_child = new RelativeLayout(context);
        RelativeLayout.LayoutParams params_c = new RelativeLayout.LayoutParams(4, -1);
        carMiddleView.addView(carMiddleView_child, params_c);
        carMiddleView_child.setBackgroundResource(R.color.YELLOW);
        RelativeLayout.LayoutParams params_m = new RelativeLayout.LayoutParams(100, -1);
        addView(carMiddleView, params_m);
        carMiddleView.setOnClickListener(v -> {
        });
        carMiddleView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    carMiddle_x = event.getRawX();
                    carMiddle_down = Config.adasCalibInfo.carMiddle;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Config.adasCalibInfo.carMiddle = (int) (carMiddle_down + (event.getRawX() - carMiddle_x) * Config.CAMERA_W / width);
                    updateCarMiddleView();
                    if(moveLisenter!=null) moveLisenter.notiryCarMiddle(Config.adasCalibInfo.carMiddle);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = (int) (width * 9f / 16f);
        try {
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) horizonView.getLayoutParams();
            if (params1 != null) {
                params1.height = height / 5;
                params1.topMargin = Config.adasCalibInfo.horizon * height / Config.CAMERA_H - height / 5;
            }
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) carMiddleView_child.getLayoutParams();
            if (params2 != null) {
                params2.leftMargin = height / 10 - 2;
            }
            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) carMiddleView.getLayoutParams();
            if (params3 != null) {
                params3.width = height / 5;
                params3.leftMargin = (Config.adasCalibInfo.carMiddle + Config.CAMERA_W / 2) * width / Config.CAMERA_W - height / 10;
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    public void updateHorizonView() {
        int top = Config.adasCalibInfo.horizon * height / Config.CAMERA_H - height / 5;
        horizonView.layout(0, top, horizonView.getWidth(), top + horizonView.getHeight());
    }

    public void updateCarMiddleView() {
        int left = (Config.adasCalibInfo.carMiddle + Config.CAMERA_W / 2) * width / Config.CAMERA_W - height / 10;
        carMiddleView.layout(left, 0, left + carMiddleView.getWidth(), carMiddleView.getHeight());
    }

    public interface MoveLisenter{
        void notifyHorizon(int vaule);
        void notiryCarMiddle(int value);
    }

    private MoveLisenter moveLisenter;

    public void setMoveLisenter(MoveLisenter moveLisenter){
        this.moveLisenter = moveLisenter;
    }
}
