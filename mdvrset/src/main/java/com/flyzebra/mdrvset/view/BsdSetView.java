package com.flyzebra.mdrvset.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.flyzebra.mdrvset.Config;
import com.flyzebra.mdrvset.http.BsdInfo;

public class BsdSetView extends RelativeLayout {
    private static final int LINE_WIDTH = 6;
    private static final int LINE1_COLOR = 0xFF000000;
    private static final int LINE2_COLOR = 0xFFFF0000;
    private static final int LINE3_COLOR = 0xFFFFFF00;
    private static final int LINE4_COLOR = 0xFF00FF00;
    private static final int POINT_WIDTH = 20;
    private int width;
    private int height;

    private final BsdInfo bsdInfo = new BsdInfo();
    private Paint line_paint1;
    private Paint line_paint2;
    private Paint line_paint3;
    private Paint line_paint4;

    private RelativeLayout baseLinePoints_0;
    private float baseLinePoints_0_down_x;
    private float baseLinePoints_0_down_y;
    private float baseLinePoints_0_x;
    private float baseLinePoints_0_y;

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
        line_paint1 = new Paint();
        line_paint1.setAntiAlias(true);
        line_paint1.setStyle(Paint.Style.STROKE);
        line_paint1.setColor(LINE1_COLOR);
        line_paint1.setStrokeWidth(LINE_WIDTH);

        line_paint2 = new Paint();
        line_paint2.setAntiAlias(true);
        line_paint2.setStyle(Paint.Style.STROKE);
        line_paint2.setColor(LINE2_COLOR);
        line_paint2.setStrokeWidth(LINE_WIDTH);

        line_paint3 = new Paint();
        line_paint3.setAntiAlias(true);
        line_paint3.setStyle(Paint.Style.STROKE);
        line_paint3.setColor(LINE3_COLOR);
        line_paint3.setStrokeWidth(LINE_WIDTH);

        line_paint4 = new Paint();
        line_paint4.setAntiAlias(true);
        line_paint4.setStyle(Paint.Style.STROKE);
        line_paint4.setColor(LINE4_COLOR);
        line_paint4.setStrokeWidth(LINE_WIDTH);

        baseLinePoints_0 = new RelativeLayout(context);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(POINT_WIDTH, POINT_WIDTH);
        addView(baseLinePoints_0, params1);
        baseLinePoints_0.setBackgroundColor(LINE1_COLOR);
        baseLinePoints_0.setOnClickListener(v -> {
        });
        baseLinePoints_0.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    baseLinePoints_0_x = event.getRawX();
                    baseLinePoints_0_y = event.getRawY();
                    baseLinePoints_0_down_x = bsdInfo.baseLinePoints_0_x;
                    baseLinePoints_0_down_y = bsdInfo.baseLinePoints_0_y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    bsdInfo.baseLinePoints_0_x = (int) (baseLinePoints_0_down_x + (event.getRawY() - baseLinePoints_0_x) * Config.CAM_WIDTH / width);
                    bsdInfo.baseLinePoints_0_y = (int) (baseLinePoints_0_down_y + (event.getRawY() - baseLinePoints_0_y) * Config.CAM_HEIGHT / height);
                    updateView();
                    break;
            }
            return false;
        });
    }

    private void updateView() {
        int baseLinePoints_0_left = bsdInfo.baseLinePoints_0_x * width / Config.CAM_WIDTH - POINT_WIDTH / 2;
        int baseLinePoints_0_top = bsdInfo.baseLinePoints_0_y * height / Config.CAM_HEIGHT - POINT_WIDTH / 2;
        baseLinePoints_0.layout(baseLinePoints_0_left, baseLinePoints_0_top,
                baseLinePoints_0_left + POINT_WIDTH, baseLinePoints_0_top + POINT_WIDTH);
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = (int) (width * 9f / 16f);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(dx(bsdInfo.baseLinePoints_0_x), dy(bsdInfo.baseLinePoints_0_y),
                dx(bsdInfo.baseLinePoints_1_x), dy(bsdInfo.baseLinePoints_1_y), line_paint1);
        canvas.drawLine(dx(bsdInfo.baseLinePoints_1_x), dy(bsdInfo.baseLinePoints_1_y),
                dx(bsdInfo.baseLinePoints_2_x), dy(bsdInfo.baseLinePoints_2_y), line_paint1);
        canvas.drawLine(dx(bsdInfo.baseLinePoints_2_x), dy(bsdInfo.baseLinePoints_2_y),
                dx(bsdInfo.baseLinePoints_3_x), dy(bsdInfo.baseLinePoints_3_y), line_paint1);

        canvas.drawLine(dx(bsdInfo.highDangerLine_startPoint_x), dy(bsdInfo.highDangerLine_startPoint_y),
                dx(bsdInfo.highDangerLine_endPoint_x), dy(bsdInfo.highDangerLine_endPoint_y), line_paint2);

        canvas.drawLine(dx(bsdInfo.mediumDangerLine_startPoint_x), dy(bsdInfo.mediumDangerLine_startPoint_y),
                dx(bsdInfo.mediumDangerLine_endPoint_x), dy(bsdInfo.mediumDangerLine_endPoint_y), line_paint3);

        canvas.drawLine(dx(bsdInfo.lowDangerLine_startPoint_x), dy(bsdInfo.lowDangerLine_startPoint_y),
                dx(bsdInfo.lowDangerLine_endPoint_x), dy(bsdInfo.lowDangerLine_endPoint_y), line_paint4);
    }

    public int dx(int x) {
        return x * width / Config.CAM_WIDTH;
    }

    public int dy(int y) {
        return y * height / Config.CAM_HEIGHT;
    }
}
