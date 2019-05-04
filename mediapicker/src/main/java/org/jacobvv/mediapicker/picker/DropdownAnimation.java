package org.jacobvv.mediapicker.picker;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Jacob on 18-1-29.
 */

public class DropdownAnimation {

    private Context mContext;

    DropdownAnimation(Context context) {
        this.mContext = context;
    }

    void startAnimationToShow(boolean animated, final View dropdownMenu, final View overlay) {
        float height = dropdownMenu.getHeight();
        if (animated) {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(dropdownMenu, "translationY",
                    -1 * height, 0);
            ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                    0, 0.7f);
            set.play(menuAnim).with(overlayAnim);
            set.setDuration(250);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    dropdownMenu.setVisibility(View.VISIBLE);
                    overlay.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            set.start();
        } else {
            dropdownMenu.setTranslationY(0);
            dropdownMenu.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
        }
    }

    void startAnimationToDismiss(boolean animated, final View dropdownMenu, final View overlay) {
        float height = dropdownMenu.getHeight();
        if (animated) {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(dropdownMenu, "translationY",
                    0, -1 * height);
            ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                    0.7f, 0);
            set.play(menuAnim).with(overlayAnim);
            set.setDuration(250);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    dropdownMenu.setVisibility(View.INVISIBLE);
                    overlay.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    dropdownMenu.setVisibility(View.INVISIBLE);
                    overlay.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            set.start();
        } else {
            dropdownMenu.setTranslationY(-1 * height);
            dropdownMenu.setVisibility(View.INVISIBLE);
            overlay.setVisibility(View.GONE);
        }
    }
}
