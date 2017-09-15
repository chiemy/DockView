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
    private float miniPercent;
    View content;

    PianoKeyView(PianoView view, View content) {
        super(view.getContext());
        addView(content);
        this.content = content;
        miniPercent = view.showMiniPercent;
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
        if (currentPercent != miniPercent) {
            currentPercent = miniPercent;
            post(this);
        }
    }

    @Override
    public void run() {
        onHide(1 - miniPercent);
    }

    @NonNull
    abstract Animator buildAnimation(float percent, boolean expand);

    abstract void onHide(float transPercent);

}
