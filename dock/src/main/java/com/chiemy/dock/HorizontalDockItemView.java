/*
 * Copyright 2017 chiemy
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.chiemy.dock;

import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by chiemy on 2017/9/14.
 */
 class HorizontalDockItemView extends DockItemView {
    static final int LEFT = 0;
    static final int RIGHT = 1;

    @IntDef({LEFT, RIGHT})
    public @interface HorizontalType {
    }

    private int type;

    public HorizontalDockItemView(DockView view, View content, @HorizontalType int type) {
        super(view, content);
        int spacing= view.getItemSpacing() / 2;
        setPadding(0, spacing, 0, spacing);
        this.type = type;
    }

    @NonNull
    @Override
    ViewPropertyAnimator buildAnimation(float percent, boolean expand) {
        float endValue;
        if (type == LEFT) {
            endValue =  (percent - 1) * getWidth();
        } else {
            endValue =  (1 - percent) * getWidth();
        }
        ViewPropertyAnimator animator =
                content.animate().translationX(endValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            animator.withLayer();
        }
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
        if (peekPercent == 0 && content.getWidth() > 0) {
            peekPercent = pianoView.peekSize / content.getWidth();
        }
        return peekPercent;
    }

    @Override
    void onHide(float transPercent) {
        if (type == LEFT) {
            transPercent = -transPercent;
        }
        content.setTranslationX(getWidth() * transPercent);
    }
}
