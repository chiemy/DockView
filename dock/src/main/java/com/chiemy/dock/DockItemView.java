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
