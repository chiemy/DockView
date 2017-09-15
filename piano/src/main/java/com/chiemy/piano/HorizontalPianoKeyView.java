package com.chiemy.piano;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by chiemy on 2017/9/14.
 */
 class HorizontalPianoKeyView extends PianoKeyView {
    static final int LEFT = 0;
    static final int RIGHT = 1;

    @IntDef({LEFT, RIGHT})
    public @interface HorizontalType {
    }

    private int type;

    public HorizontalPianoKeyView(PianoView view, View content, @HorizontalType int type) {
        super(view, content);
        int spacing= view.getItemSpacing() / 2;
        setPadding(0, spacing, 0, spacing);
        this.type = type;
    }

    @NonNull
    @Override
    Animator buildAnimation(float percent, boolean expand) {
        float endValue;
        if (type == LEFT) {
            endValue =  (percent - 1) * getWidth();
        } else {
            endValue =  (1 - percent) * getWidth();
        }
        ObjectAnimator animator =
                ObjectAnimator.ofFloat(
                        content,
                        "translationX",
                        content.getTranslationX(),
                        endValue
                );
        if (!expand) {
            animator.setInterpolator(new OvershootInterpolator());
        } else {
            animator.setInterpolator(new AccelerateInterpolator());
        }
        animator.setDuration(200);
        return animator;
    }

    @Override
    void onHide(float transPercent) {
        if (type == LEFT) {
            transPercent = -transPercent;
        }
        content.setTranslationX(getWidth() * transPercent);
    }
}
