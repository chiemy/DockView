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

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;

/**
 * Created by chiemy on 2017/9/14.
 */
abstract class DockItemView extends FrameLayout implements Runnable {
    private float currentPercent = 0;
    private float peekPercent;
    DockView pianoView;
    View content;

    DockItemView(DockView view, View content) {
        super(view.getContext());
        pianoView = view;
        addView(content);
        this.content = content;
        setClipToPadding(false);
    }

    void show(float percent) {
        if (currentPercent == percent) {
            return;
        }
        boolean expand = percent > currentPercent;
        currentPercent = percent;
        buildAnimation(percent, expand).start();
    }

    void hide() {
        peekPercent = getValidPeekPercent();
        if (currentPercent != peekPercent) {
            currentPercent = peekPercent;
            post(this);
        }
    }

    abstract float validPeekPercent(float peekPercent);

    private float getValidPeekPercent() {
        float peekPercent = validPeekPercent(pianoView.peekPercent);
        if (peekPercent <= 0) {
            peekPercent = DockView.DEF_MIN_PERCENT;
        }
        return peekPercent;
    }


    float getPeekPercent() {
        return getValidPeekPercent();
    }

    public float getCurrentPercent() {
        return currentPercent;
    }

    @Override
    public void run() {
        onHide(1 - peekPercent);
    }

    @NonNull
    abstract ViewPropertyAnimator buildAnimation(float percent, boolean expand);

    abstract void onHide(float transPercent);

}
