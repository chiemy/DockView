package com.chiemy.dock;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chiemy on 2017/9/15.
 */
 class HorizontalTouchEventHelper extends TouchEventHelper {

    HorizontalTouchEventHelper(DockView pianoView) {
        super(pianoView);
    }

    @Override
    View findTouchView(MotionEvent e) {
        float x = Math.min(pianoView.getWidth(), e.getX());
        x = Math.max(0, x);
        return pianoView.findChildViewUnder(x, 0);
    }

    @Override
    void scrollToViewCenter(View touchView) {
        final int touchViewCenter = calculate(touchView);
        final int deltaX =  touchViewCenter - pianoView.getWidth() / 2;
        pianoView.smoothScrollBy(deltaX, 0);
    }

    private int calculate(View view) {
        return (int) view.getX() + view.getWidth() / 2;
    }
}
