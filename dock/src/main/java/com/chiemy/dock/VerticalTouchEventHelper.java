package com.chiemy.dock;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chiemy on 2017/9/15.
 */
 class VerticalTouchEventHelper extends TouchEventHelper {

    VerticalTouchEventHelper(DockView pianoView) {
        super(pianoView);
    }

    @Override
    View findTouchView(MotionEvent e) {
        float y = Math.min(pianoView.getHeight(), e.getY());
        y = Math.max(0, y);
        return pianoView.findChildViewUnder(0, y);
    }

    @Override
    void scrollToViewCenter(View touchView) {
        final int touchViewCenter = (int) touchView.getY() + touchView.getHeight() / 2;
        final int deltaY =  touchViewCenter - pianoView.getHeight() / 2;
        pianoView.smoothScrollBy(0, deltaY);
    }
}
