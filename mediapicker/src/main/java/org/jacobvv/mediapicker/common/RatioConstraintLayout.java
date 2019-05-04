package org.jacobvv.mediapicker.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.jacobvv.mediapicker.R;

/**
 * Created by Jacob on 18-1-12.
 */

public class RatioConstraintLayout extends ConstraintLayout {

    private static final String TAG = "RatioConstraintLayout";
    private float mRatio = -1f;

    public RatioConstraintLayout(Context context) {
        this(context, null);
    }

    public RatioConstraintLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioConstraintLayout);
        String ratioStr = a.getString(R.styleable.RatioConstraintLayout_dimensionRatio);
        a.recycle();
        if (ratioStr == null) {
            return;
        }
        try {
            mRatio = Float.parseFloat(ratioStr);
        } catch (NumberFormatException e) {
            if (!ratioStr.contains(":")) {
                Log.e(TAG, "Attribute dimensionRatio format ERROR.");
                return;
            }
            String[] split = ratioStr.split(":");
            if (split.length != 2) {
                Log.e(TAG, "Attribute dimensionRatio format ERROR.");
                return;
            }
            try {
                float widthRatio = Float.parseFloat(split[0]);
                float heightRatio = Float.parseFloat(split[1]);
                mRatio = widthRatio / heightRatio;
            } catch (NumberFormatException exception) {
                Log.e(TAG, "Attribute dimensionRatio format ERROR.");
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatio > 0) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = (int) (widthSize / mRatio);
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
