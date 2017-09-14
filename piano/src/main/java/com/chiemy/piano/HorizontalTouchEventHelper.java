package com.chiemy.piano;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chiemy on 2017/9/15.
 */

public class HorizontalTouchEventHelper extends TouchEventHelper {

    HorizontalTouchEventHelper(PianoView pianoView) {
        super(pianoView);
    }

    @Override
    View findTouchView(MotionEvent e) {
        return pianoView.findChildViewUnder(e.getX(), 0);
    }

    @Override
    void scrollToTouchViewCenter(View touchView) {
        final int touchViewCenter = (int) touchView.getX() + touchView.getWidth() / 2;
        final int deltaX =  touchViewCenter - pianoView.getWidth() / 2;
        pianoView.smoothScrollBy(deltaX, 0);
    }
}
