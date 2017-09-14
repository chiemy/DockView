package com.chiemy.piano;

import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chiemy on 2017/9/14.
 */
abstract class TouchEventHelper {
    PianoView pianoView;
    LinearLayoutManager layoutManager;
    int offsetCount;
    float miniPercent;

    TouchEventHelper(PianoView pianoView) {
        this.pianoView = pianoView;
        this.layoutManager = pianoView.getInnerLayoutManager();
        offsetCount = pianoView.offsetCount;
        miniPercent = pianoView.showMiniPercent;
    }

    public void onTouchEvent(MotionEvent e) {
        View touchView = findTouchView(e);
        if (touchView != null) {
            final int position = pianoView.getChildAdapterPosition(touchView);
            onShow(position);

            if (e.getAction() == MotionEvent.ACTION_UP) {
                onHide(position);

                scrollToTouchViewCenter(touchView);

                if (pianoView.listener != null) {
                    pianoView.listener.onItemSelected(pianoView, ((PianoKeyView) touchView).content, position);
                }
            }
        }
    }

    void onShow(int touchViewPosition) {
        final int startPosition = Math.max(0, touchViewPosition - offsetCount);
        final int endPosition = Math.min(layoutManager.getItemCount(), touchViewPosition + offsetCount);

        for (int i = startPosition; i <= endPosition; i++) {
            View view = layoutManager.findViewByPosition(i);
            if (view != null && view instanceof PianoKeyView) {
                int offset = Math.abs(i - touchViewPosition);
                float percent = Math.max(1 - (float) offset / offsetCount, miniPercent);
                ((PianoKeyView) view).show(percent);
            }
        }
    }

    void onHide(int actionUpPosition) {
        final int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        final int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            if (i != actionUpPosition) {
                View view = layoutManager.findViewByPosition(i);
                if (view != null && view instanceof PianoKeyView) {
                    ((PianoKeyView) view).show(miniPercent);
                }
            }
        }
    }

    abstract View findTouchView(MotionEvent e);

    abstract void scrollToTouchViewCenter(View touchView);

}
