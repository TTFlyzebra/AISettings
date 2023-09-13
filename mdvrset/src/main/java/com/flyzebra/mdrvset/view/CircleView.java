package com.flyzebra.mdrvset.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleView extends View {
    private int width = 100;
    private int height = 100;
    private Paint backPaint;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setBackgroundColor(int color) {
        backPaint = new Paint();
        backPaint.setAntiAlias(true);
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (width > 0 && height > 0) {
            canvas.drawCircle(width / 2, height / 2, width / 10, backPaint);
        }
    }

    public void setSize(int width, int height){
        this.width = width;
        this.height = height;
    }
}
