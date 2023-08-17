/**
 * FileName: MoveRelativeLayout
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/7/25 8:12
 * Description:
 */
package com.flyzebra.aisettings.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class TouchEventView extends FrameLayout implements View.OnTouchListener {
    private float down_x;
    private float down_y;
    private float move_x;
    private float move_y;
    public TouchEventView(Context context) {
        this(context, null);
    }

    public TouchEventView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TouchEventView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        setOnClickListener(v -> {
        });
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, (int) (width * 9f / 16f));
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down_x = ev.getX();
                down_y = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                move_x = ev.getX() - down_x;
                move_y = ev.getY() - down_y;
                if (mOnMoveListener != null) {
                    mOnMoveListener.onMoving(move_x, move_y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mOnMoveListener != null) {
                    mOnMoveListener.onMoveEnd(move_x, move_y);
                }
                if (move_x > 4 || move_y > 4) {
                    return true;
                }
                break;
        }
        return false;
    }

    public interface OnMoveListener {
        void onMoving(float x, float y);

        void onMoveEnd(float x, float y);
    }

    private OnMoveListener mOnMoveListener;

    public void setOnMoveListener(OnMoveListener onMoveListener) {
        mOnMoveListener = onMoveListener;
    }
}
