package com.chiemy.piano;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chiemy on 2017/9/15.
 */
 class VerticalTouchEventHelper extends TouchEventHelper {

    VerticalTouchEventHelper(PianoView pianoView) {
        super(pianoView);
    }

    @Override
    View findTouchView(MotionEvent e) {
        return pianoView.findChildViewUnder(0, e.getY());
    }

    @Override
    void scrollToViewCenter(View touchView) {
        final int touchViewCenter = (int) touchView.getY() + touchView.getHeight() / 2;
        final int deltaY =  touchViewCenter - pianoView.getHeight() / 2;
        pianoView.smoothScrollBy(0, deltaY);
    }
}
