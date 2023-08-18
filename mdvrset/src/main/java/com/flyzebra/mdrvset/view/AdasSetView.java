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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;

public class AdasSetView extends RelativeLayout implements View.OnTouchListener {
    private LinearLayout selected_v;
    private LinearLayout selected_h;
    private float down_x;
    private float down_y;
    private float move_x;
    private float move_y;

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

    public AdasSetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        setFocusable(true);
        setOnClickListener(v -> {
        });
        setOnTouchListener(this);

        selected_v = new LinearLayout(context);
        LinearLayout.LayoutParams params_v = new LinearLayout.LayoutParams(-1, 100);
        addView(selected_v, params_v);
        selected_v.setBackgroundResource(R.drawable.rectangle);
        selected_v.setOnClickListener(v -> {
        });
        selected_v.setOnTouchListener((v, event) -> {
            FlyLog.e("selected_v onTouch" + event);
            return false;
        });

        selected_h = new LinearLayout(context);
        LinearLayout child = new LinearLayout(context);
        LinearLayout.LayoutParams params_child = new LinearLayout.LayoutParams(3, -1);
        child.setBackgroundResource(R.color.YELLOW);
        selected_h.addView(child, params_child);
        LinearLayout.LayoutParams params_h = new LinearLayout.LayoutParams(20, -1);
        addView(selected_h, params_h);
        selected_h.setOnClickListener(v -> {
        });
        selected_h.setOnTouchListener((v, event) -> {
            FlyLog.e("selected_v onTouch" + event);
            return false;
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
