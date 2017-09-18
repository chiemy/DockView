package com.chiemy.dock;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created: chiemy
 * Date: 9/15/17
 * Description:
 */
class DockViewLayoutManager extends OnScrollListener {
    private Context context;
    private DockView pianoView;
    private int gravity;
    private LinearLayoutManager layoutManager;
    private TouchEventHelper touchEventHelper;

    DockViewLayoutManager(DockView pianoView, int gravity) {
        this.pianoView = pianoView;
        this.gravity = gravity;
        pianoView.setLayoutManager(this);
        context = pianoView.getContext();

        init(gravity);
    }

    private void init(int gravity) {
        switch (gravity) {
            case Gravity.TOP:
                layoutManager = new LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                );
                touchEventHelper = new HorizontalTouchEventHelper(pianoView);
                break;
            case Gravity.BOTTOM:
                layoutManager = new LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                );
                touchEventHelper = new HorizontalTouchEventHelper(pianoView);
                break;
            case Gravity.LEFT:
            case Gravity.START:
                layoutManager = new LinearLayoutManager(
                        context,
                        LinearLayoutManager.VERTICAL,
                        false
                );
                touchEventHelper = new VerticalTouchEventHelper(pianoView);
                break;
            case Gravity.RIGHT:
            case Gravity.END:
                layoutManager = new LinearLayoutManager(
                        context,
                        LinearLayoutManager.VERTICAL,
                        false
                );
                touchEventHelper = new VerticalTouchEventHelper(pianoView);
                break;
        }
    }

    DockItemView createPianoKeyView(View content) {
        DockItemView view = null;
        switch (gravity) {
            case Gravity.TOP:
                view = new VerticalDockItemView(pianoView, content,
                        VerticalDockItemView.DOWN);
                break;
            case Gravity.BOTTOM:
                view = new VerticalDockItemView(pianoView, content,
                        VerticalDockItemView.UP);
                break;
            case Gravity.LEFT:
            case Gravity.START:
                view = new HorizontalDockItemView(pianoView, content,
                        HorizontalDockItemView.LEFT);
                break;
            case Gravity.RIGHT:
            case Gravity.END:
                view = new HorizontalDockItemView(pianoView, content,
                        HorizontalDockItemView.RIGHT);
                break;
        }
        return view;
    }

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    void onTouchEvent(MotionEvent e) {
        touchEventHelper.onTouchEvent(e);
    }

    void performTouch(int position) {
        touchEventHelper.performTouch(position);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        touchEventHelper.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        touchEventHelper.onScrolled(recyclerView, dx, dy);
    }
}
