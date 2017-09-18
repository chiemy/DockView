package com.chiemy.piano;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;

/**
 * Created by chiemy on 2017/9/14.
 */
abstract class PianoKeyView extends FrameLayout implements Runnable {
    private float currentPercent = 0;
    private float peekPercent;
    PianoView pianoView;
    View content;

    PianoKeyView(PianoView view, View content) {
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
        post(this);
    }

    abstract float validPeekPercent(float peekPercent);

    private float getValidPeekPercent() {
        float peekPercent = validPeekPercent(pianoView.peekPercent);
        if (peekPercent <= 0) {
            peekPercent = PianoView.DEF_MIN_PERCENT;
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
        peekPercent = getValidPeekPercent();
        if (currentPercent != peekPercent) {
            currentPercent = peekPercent;
            onHide(1 - peekPercent);
        }
    }

    @NonNull
    abstract ViewPropertyAnimator buildAnimation(float percent, boolean expand);

    abstract void onHide(float transPercent);

}
