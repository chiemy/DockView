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
 class VerticalPianoKeyView extends PianoKeyView {
    static final int UP = 0;
    static final int DOWN = 1;

    @IntDef({UP, DOWN})
    public @interface VerticalType {
    }

    private int type;

    public VerticalPianoKeyView(PianoView view, View content, @VerticalType int type) {
        super(view, content);
        this.type = type;
        int spacing= view.getItemSpacing() / 2;
        setPadding(spacing, 0, spacing, 0);
    }

    @NonNull
    @Override
    Animator buildAnimation(float percent, boolean expand) {
        float endValue;
        if (type == UP) {
            endValue =  (1 - percent) * getHeight();
        } else {
            endValue =  (percent - 1) * getHeight();
        }
        ObjectAnimator animator =
                ObjectAnimator.ofFloat(
                        content,
                        "translationY",
                        content.getTranslationY(),
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
    float validPeekPercent(float peekPercent) {
        if (peekPercent <= 0 && content.getHeight() > 0) {
            peekPercent = pianoView.peekSize / content.getHeight();
        }
        return peekPercent;
    }

    @Override
    void onHide(float transPercent) {
        if (type == DOWN) {
            transPercent = -transPercent;
        }
        content.setTranslationY(getHeight() * transPercent);
    }
}
