/**
 * FileName: ViewPager
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/12/2 10:35
 * Description:
 */
package com.flyzebra.mdrvset.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AutoImageView extends androidx.appcompat.widget.AppCompatImageView {
    private int mWidth;
    private int mHeight;

    public AutoImageView(@NonNull Context context) {
        this(context, null);
    }

    public AutoImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width == 0 || height == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            mWidth = width;
            mHeight = mWidth * 120 / 200;
            setMeasuredDimension(mWidth, mHeight);
        }
    }
}
