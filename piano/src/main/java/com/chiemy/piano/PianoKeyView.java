package com.chiemy.piano;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.view.View;
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

    float validPeekPercent(float peekPercent) {
        return peekPercent <= 0 ? 0.2f : peekPercent;
    }

    float getPeekPercent() {
        return validPeekPercent(pianoView.peekPercent);
    }

    public float getCurrentPercent() {
        return currentPercent;
    }

    @Override
    public void run() {
        peekPercent = validPeekPercent(pianoView.peekPercent);
        if (currentPercent != peekPercent) {
            currentPercent = peekPercent;
            onHide(1 - peekPercent);
        }
    }

    @NonNull
    abstract Animator buildAnimation(float percent, boolean expand);

    abstract void onHide(float transPercent);

}
