package com.flyzebra.mdrvset.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.flyzebra.mdrvset.Config;
import com.flyzebra.mdrvset.bean.BsdBean;
import com.flyzebra.mdrvset.http.BsdInfo;
import com.flyzebra.utils.FlyLog;

public class BsdSetView extends RelativeLayout {
    private static final int LINE_WIDTH = 6;
    private static final int BASELINE_COLOR = 0xFF000000;
    private static final int HIGHDANGERLINE_COLOR = 0xFFFF0000;
    private static final int MEDIUMDANGERLINE_COLOR = 0xFFFFFF00;
    private static final int LOWDANGERLINE_COLOR = 0xFF00FF00;
    private int width = 1280;
    private int height = 720;

    private BsdBean bsdBean = new BsdBean();
    private Paint base_paint;
    private Paint high_paint;
    private Paint medium_paint;
    private Paint low_paint;

    private final CircleView[] baseLineView = new CircleView[4];
    private final Point[] baseLinePoint = new Point[4];
    private final Point[] baseLinePointDown = new Point[4];


    private final CircleView[] highDangerLineView = new CircleView[2];
    private final Point[] highDangerLinePoint = new Point[2];
    private final Point[] highDangerLinePointDown = new Point[2];

    private final CircleView[] mediumDangerLineView = new CircleView[2];
    private final Point[] mediumDangerLinePoint = new Point[2];
    private final Point[] mediumDangerLinePointDown = new Point[2];

    private final CircleView[] lowDangerLineView = new CircleView[2];
    private final Point[] lowDangerLinePoint = new Point[2];
    private final Point[] lowDangerLinePointDown = new Point[2];

    public BsdSetView(Context context) {
        this(context, null);
    }

    public BsdSetView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BsdSetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        setBackgroundColor(0x01FFFFFF);
        base_paint = new Paint();
        base_paint.setAntiAlias(true);
        base_paint.setStyle(Paint.Style.STROKE);
        base_paint.setColor(BASELINE_COLOR);
        base_paint.setStrokeWidth(LINE_WIDTH);
        high_paint = new Paint();
        high_paint.setAntiAlias(true);
        high_paint.setStyle(Paint.Style.STROKE);
        high_paint.setColor(HIGHDANGERLINE_COLOR);
        high_paint.setStrokeWidth(LINE_WIDTH);
        medium_paint = new Paint();
        medium_paint.setAntiAlias(true);
        medium_paint.setStyle(Paint.Style.STROKE);
        medium_paint.setColor(MEDIUMDANGERLINE_COLOR);
        medium_paint.setStrokeWidth(LINE_WIDTH);
        low_paint = new Paint();
        low_paint.setAntiAlias(true);
        low_paint.setStyle(Paint.Style.STROKE);
        low_paint.setColor(LOWDANGERLINE_COLOR);
        low_paint.setStrokeWidth(LINE_WIDTH);

        for (int i = 0; i < 4; i++) {
            baseLineView[i] = new CircleView(context);
            baseLinePoint[i] = new Point();
            baseLinePointDown[i] = new Point();
            RelativeLayout.LayoutParams base_params0 = new RelativeLayout.LayoutParams(width / 10, width / 10);
            addView(baseLineView[i], base_params0);
            baseLineView[i].setBackgroundColor(BASELINE_COLOR);
            baseLineView[i].setOnClickListener(v -> {
            });
            baseLineView[i].setTag(i);
            baseLineView[i].setOnTouchListener((v, event) -> {
                int n = (int) v.getTag();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        baseLinePoint[n].x = (int) event.getRawX();
                        baseLinePoint[n].y = (int) event.getRawY();
                        baseLinePointDown[n].x = bsdBean.baseLine[n].x;
                        baseLinePointDown[n].y = bsdBean.baseLine[n].y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        bsdBean.baseLine[n].x = (int) (baseLinePointDown[n].x + (event.getRawX() - baseLinePoint[n].x) * Config.CAM_WIDTH / width);
                        bsdBean.baseLine[n].y = (int) (baseLinePointDown[n].y + (event.getRawY() - baseLinePoint[n].y) * Config.CAM_HEIGHT / height);
                        bsdBean.baseLine[n].x = Math.max(0, bsdBean.baseLine[n].x);
                        bsdBean.baseLine[n].x = Math.min(Config.CAM_WIDTH, bsdBean.baseLine[n].x);
                        bsdBean.baseLine[n].y = Math.max(0, bsdBean.baseLine[n].y);
                        bsdBean.baseLine[n].y = Math.min(Config.CAM_HEIGHT, bsdBean.baseLine[n].y);
                        updateView();
                        if (moveLisenter != null) {
                            moveLisenter.notifyBsdInfo(bsdBean.toBsdInfo(bsdBean));
                        }
                        break;
                }
                return false;
            });
        }

        for (int i = 0; i < 2; i++) {
            highDangerLineView[i] = new CircleView(context);
            highDangerLinePoint[i] = new Point();
            highDangerLinePointDown[i] = new Point();
            RelativeLayout.LayoutParams base_params0 = new RelativeLayout.LayoutParams(width / 10, width / 10);
            addView(highDangerLineView[i], base_params0);
            highDangerLineView[i].setBackgroundColor(HIGHDANGERLINE_COLOR);
            highDangerLineView[i].setOnClickListener(v -> {
            });
            highDangerLineView[i].setTag(i);
            highDangerLineView[i].setOnTouchListener((v, event) -> {
                int n = (int) v.getTag();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        highDangerLinePoint[n].x = (int) event.getRawX();
                        highDangerLinePoint[n].y = (int) event.getRawY();
                        highDangerLinePointDown[n].x = bsdBean.highLine[n].x;
                        highDangerLinePointDown[n].y = bsdBean.highLine[n].y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        bsdBean.highLine[n].x = (int) (highDangerLinePointDown[n].x + (event.getRawX() - highDangerLinePoint[n].x) * Config.CAM_WIDTH / width);
                        bsdBean.highLine[n].y = (int) (highDangerLinePointDown[n].y + (event.getRawY() - highDangerLinePoint[n].y) * Config.CAM_HEIGHT / height);
                        bsdBean.highLine[n].x = Math.max(0, bsdBean.highLine[n].x);
                        bsdBean.highLine[n].x = Math.min(Config.CAM_WIDTH, bsdBean.highLine[n].x);
                        bsdBean.highLine[n].y = Math.max(0, bsdBean.highLine[n].y);
                        bsdBean.highLine[n].y = Math.min(Config.CAM_HEIGHT, bsdBean.highLine[n].y);
                        updateView();
                        if (moveLisenter != null) {
                            moveLisenter.notifyBsdInfo(bsdBean.toBsdInfo(bsdBean));
                        }
                        break;
                }
                return false;
            });
        }

        for (int i = 0; i < 2; i++) {
            mediumDangerLineView[i] = new CircleView(context);
            mediumDangerLinePoint[i] = new Point();
            mediumDangerLinePointDown[i] = new Point();
            RelativeLayout.LayoutParams base_params0 = new RelativeLayout.LayoutParams(width / 10, width / 10);
            addView(mediumDangerLineView[i], base_params0);
            mediumDangerLineView[i].setBackgroundColor(MEDIUMDANGERLINE_COLOR);
            mediumDangerLineView[i].setOnClickListener(v -> {
            });
            mediumDangerLineView[i].setTag(i);
            mediumDangerLineView[i].setOnTouchListener((v, event) -> {
                int n = (int) v.getTag();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mediumDangerLinePoint[n].x = (int) event.getRawX();
                        mediumDangerLinePoint[n].y = (int) event.getRawY();
                        mediumDangerLinePointDown[n].x = bsdBean.mediumLine[n].x;
                        mediumDangerLinePointDown[n].y = bsdBean.mediumLine[n].y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        bsdBean.mediumLine[n].x = (int) (mediumDangerLinePointDown[n].x + (event.getRawX() - mediumDangerLinePoint[n].x) * Config.CAM_WIDTH / width);
                        bsdBean.mediumLine[n].y = (int) (mediumDangerLinePointDown[n].y + (event.getRawY() - mediumDangerLinePoint[n].y) * Config.CAM_HEIGHT / height);
                        bsdBean.mediumLine[n].x = Math.max(0, bsdBean.mediumLine[n].x);
                        bsdBean.mediumLine[n].x = Math.min(Config.CAM_WIDTH, bsdBean.mediumLine[n].x);
                        bsdBean.mediumLine[n].y = Math.max(0, bsdBean.mediumLine[n].y);
                        bsdBean.mediumLine[n].y = Math.min(Config.CAM_HEIGHT, bsdBean.mediumLine[n].y);
                        updateView();
                        if (moveLisenter != null) {
                            moveLisenter.notifyBsdInfo(bsdBean.toBsdInfo(bsdBean));
                        }
                        break;
                }
                return false;
            });
        }

        for (int i = 0; i < 2; i++) {
            lowDangerLineView[i] = new CircleView(context);
            lowDangerLinePoint[i] = new Point();
            lowDangerLinePointDown[i] = new Point();
            RelativeLayout.LayoutParams base_params0 = new RelativeLayout.LayoutParams(width / 10, width / 10);
            addView(lowDangerLineView[i], base_params0);
            lowDangerLineView[i].setBackgroundColor(LOWDANGERLINE_COLOR);
            lowDangerLineView[i].setOnClickListener(v -> {
            });
            lowDangerLineView[i].setTag(i);
            lowDangerLineView[i].setOnTouchListener((v, event) -> {
                int n = (int) v.getTag();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lowDangerLinePoint[n].x = (int) event.getRawX();
                        lowDangerLinePoint[n].y = (int) event.getRawY();
                        lowDangerLinePointDown[n].x = bsdBean.lowLine[n].x;
                        lowDangerLinePointDown[n].y = bsdBean.lowLine[n].y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        bsdBean.lowLine[n].x = (int) (lowDangerLinePointDown[n].x + (event.getRawX() - lowDangerLinePoint[n].x) * Config.CAM_WIDTH / width);
                        bsdBean.lowLine[n].y = (int) (lowDangerLinePointDown[n].y + (event.getRawY() - lowDangerLinePoint[n].y) * Config.CAM_HEIGHT / height);
                        bsdBean.lowLine[n].x = Math.max(0, bsdBean.lowLine[n].x);
                        bsdBean.lowLine[n].x = Math.min(Config.CAM_WIDTH, bsdBean.lowLine[n].x);
                        bsdBean.lowLine[n].y = Math.max(0, bsdBean.lowLine[n].y);
                        bsdBean.lowLine[n].y = Math.min(Config.CAM_HEIGHT, bsdBean.lowLine[n].y);
                        updateView();
                        if (moveLisenter != null) {
                            moveLisenter.notifyBsdInfo(bsdBean.toBsdInfo(bsdBean));
                        }
                        break;
                }
                return false;
            });
        }
    }

    private void updateView() {
        for (int i = 0; i < 4; i++) {
            int left = bsdBean.baseLine[i].x * width / Config.CAM_WIDTH - width / 20;
            int top = bsdBean.baseLine[i].y * height / Config.CAM_HEIGHT - width / 20;
            baseLineView[i].layout(left, top, left + width / 10, top + width / 10);
        }

        for (int i = 0; i < 2; i++) {
            int left = bsdBean.highLine[i].x * width / Config.CAM_WIDTH - width / 20;
            int top = bsdBean.highLine[i].y * height / Config.CAM_HEIGHT - width / 20;
            highDangerLineView[i].layout(left, top, left + width / 10, top + width / 10);
        }

        for (int i = 0; i < 2; i++) {
            int left = bsdBean.mediumLine[i].x * width / Config.CAM_WIDTH - width / 20;
            int top = bsdBean.mediumLine[i].y * height / Config.CAM_HEIGHT - width / 20;
            mediumDangerLineView[i].layout(left, top, left + width / 10, top + width / 10);
        }

        for (int i = 0; i < 2; i++) {
            int left = bsdBean.lowLine[i].x * width / Config.CAM_WIDTH - width / 20;
            int top = bsdBean.lowLine[i].y * height / Config.CAM_HEIGHT - width / 20;
            lowDangerLineView[i].layout(left, top, left + width / 10, top + width / 10);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = (int) (width * 9f / 16f);
        try {
            for (int i = 0; i < 4; i++) {
                LayoutParams base_params = (LayoutParams) baseLineView[i].getLayoutParams();
                base_params.width = base_params.height = width / 10;
                base_params.leftMargin = bsdBean.baseLine[i].x * width / Config.CAM_WIDTH - width / 20;
                base_params.topMargin = bsdBean.baseLine[i].y * height / Config.CAM_HEIGHT - width / 20;
            }

            for (int i = 0; i < 2; i++) {
                LayoutParams high_params = (LayoutParams) highDangerLineView[i].getLayoutParams();
                high_params.width = high_params.height = width / 10;
                high_params.leftMargin = bsdBean.highLine[i].x * width / Config.CAM_WIDTH - width / 20;
                high_params.topMargin = bsdBean.highLine[i].y * height / Config.CAM_HEIGHT - width / 20;
            }

            for (int i = 0; i < 2; i++) {
                LayoutParams high_params = (LayoutParams) mediumDangerLineView[i].getLayoutParams();
                high_params.width = high_params.height = width / 10;
                high_params.leftMargin = bsdBean.mediumLine[i].x * width / Config.CAM_WIDTH - width / 20;
                high_params.topMargin = bsdBean.mediumLine[i].y * height / Config.CAM_HEIGHT - width / 20;
            }

            for (int i = 0; i < 2; i++) {
                LayoutParams high_params = (LayoutParams) lowDangerLineView[i].getLayoutParams();
                high_params.width = high_params.height = width / 10;
                high_params.leftMargin = bsdBean.lowLine[i].x * width / Config.CAM_WIDTH - width / 20;
                high_params.topMargin = bsdBean.lowLine[i].y * height / Config.CAM_HEIGHT - width / 20;
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(dx(bsdBean.baseLine[0].x), dy(bsdBean.baseLine[0].y),
                dx(bsdBean.baseLine[1].x), dy(bsdBean.baseLine[1].y), base_paint);
        canvas.drawLine(dx(bsdBean.baseLine[1].x), dy(bsdBean.baseLine[1].y),
                dx(bsdBean.baseLine[2].x), dy(bsdBean.baseLine[2].y), base_paint);
        canvas.drawLine(dx(bsdBean.baseLine[2].x), dy(bsdBean.baseLine[2].y),
                dx(bsdBean.baseLine[3].x), dy(bsdBean.baseLine[3].y), base_paint);

        canvas.drawLine(dx(bsdBean.highLine[0].x), dy(bsdBean.highLine[0].y),
                dx(bsdBean.highLine[1].x), dy(bsdBean.highLine[1].y), high_paint);

        canvas.drawLine(dx(bsdBean.mediumLine[0].x), dy(bsdBean.mediumLine[0].y),
                dx(bsdBean.mediumLine[1].x), dy(bsdBean.mediumLine[1].y), medium_paint);

        canvas.drawLine(dx(bsdBean.lowLine[0].x), dy(bsdBean.lowLine[0].y),
                dx(bsdBean.lowLine[1].x), dy(bsdBean.lowLine[1].y), low_paint);
    }

    public int dx(int x) {
        return x * width / Config.CAM_WIDTH;
    }

    public int dy(int y) {
        return y * height / Config.CAM_HEIGHT;
    }

    public void upBsdInfo(BsdInfo bsdInfo) {
        this.bsdBean = BsdBean.fromBsdInfo(bsdInfo);
        updateView();
    }

    public interface MoveLisenter {
        void notifyBsdInfo(BsdInfo bsdInfo);
    }

    private MoveLisenter moveLisenter;

    public void setMoveLisenter(MoveLisenter moveLisenter) {
        this.moveLisenter = moveLisenter;
    }
}
