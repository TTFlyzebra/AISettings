/**
 * FileName: FlySurfaceView
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/7/18 15:24
 * Description:
 */
package com.flyzebra.aisettings.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class AutoFrameLayout extends FrameLayout {
    public AutoFrameLayout(Context context) {
        super(context);
    }

    public AutoFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, (int) (width * 9f / 16f));
    }

}
