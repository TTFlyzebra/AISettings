/**
 * FileName: FlySurfaceView
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/7/18 15:24
 * Description:
 */
package com.flyzebra.mdrvset.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class AutoTextureView extends TextureView {
    public AutoTextureView(Context context) {
        this(context, null);
    }

    public AutoTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, (int) (width * 9f / 16f));
    }

}
