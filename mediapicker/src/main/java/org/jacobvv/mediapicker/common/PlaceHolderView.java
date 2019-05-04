package org.jacobvv.mediapicker.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.jacobvv.mediapicker.R;

/**
 * Created by Jacob on 18-1-11.
 */

public class PlaceHolderView extends View {

    private float mWidthRatio;
    private float mHeightRatio;

    public PlaceHolderView(Context context) {
        this(context, null);
    }

    public PlaceHolderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlaceHolderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PlaceHolderView);
        mWidthRatio = a.getFloat(R.styleable.PlaceHolderView_widthRatio, -1f);
        mHeightRatio = a.getFloat(R.styleable.PlaceHolderView_heightRatio, -1f);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mWidthRatio > 0 && mWidthRatio < 1) {
            width = (int) (width * mWidthRatio);
        }
        if (mHeightRatio > 0 && mHeightRatio < 1) {
            height = (int) (height * mHeightRatio);
        }
        setMeasuredDimension(width, height);
    }

}
