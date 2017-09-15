package com.chiemy.piano;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chiemy on 2017/9/14.
 */
abstract class TouchEventHelper extends RecyclerView.OnScrollListener {
    PianoView pianoView;
    LinearLayoutManager layoutManager;
    int selectedPosition = -1;

    TouchEventHelper(PianoView pianoView) {
        this.pianoView = pianoView;
        this.layoutManager = pianoView.getInnerLayoutManager();
    }

    void onTouchEvent(MotionEvent e) {
        View touchView = findTouchView(e);
        if (touchView != null) {
            final int position = pianoView.getChildAdapterPosition(touchView);
            selectedPosition = position;
            onShow(position);

            if (e.getAction() == MotionEvent.ACTION_UP) {
                onHide(position);

                scrollToViewCenter(touchView);

                if (pianoView.listener != null) {
                    pianoView.listener.onItemSelected(pianoView, ((PianoKeyView) touchView).content, position);
                }
            }
        }
    }

    void onShow(int touchViewPosition) {
        final int startPosition = Math.max(0, touchViewPosition - pianoView.popOffset);
        final int endPosition = Math.min(layoutManager.getItemCount(), touchViewPosition + pianoView.popOffset);

        for (int i = startPosition; i <= endPosition; i++) {
            View view = layoutManager.findViewByPosition(i);
            if (view != null && view instanceof PianoKeyView) {
                int offset = Math.abs(i - touchViewPosition);
                float percent = 1 - offset * pianoView.deltaHeightPercent;
                percent = Math.max(percent, ((PianoKeyView) view).getPeekPercent());
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
                    ((PianoKeyView) view).show(((PianoKeyView) view).getPeekPercent());
                }
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            performTouch(selectedPosition);
        }
    }

    void performTouch(int position) {
        selectedPosition = position;
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        if (position >= firstVisiblePosition
                && position <= lastVisiblePosition) {
            View view = layoutManager.findViewByPosition(position);
            if (view != null) {
                scrollToViewCenter(view);
                if (view instanceof PianoKeyView) {
                    ((PianoKeyView) view).show(1);
                }
            }
        } else {
            pianoView.smoothScrollTo(position);
        }
    }

    abstract View findTouchView(MotionEvent e);

    abstract void scrollToViewCenter(View touchView);

}
