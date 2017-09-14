package com.chiemy.piano;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created: chiemy
 * Date: 9/14/17
 * Description:
 */

public class PianoView extends RecyclerView {
    private LinearLayoutManager layoutManager;
    private int itemSpacing;
    float showMiniPercent = 0.2f;
    float translatePercent = 1 - showMiniPercent;
    int offsetCount = 5;
    private ScrollRunnable scrollRunnable = new ScrollRunnable();
    OnItemSelectedListener listener;

    private TouchEventHelper touchEventHelper;

    private int gravity = Gravity.BOTTOM;

    public PianoView(Context context) {
        this(context, null);
    }

    public PianoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PianoView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        addOnItemTouchListener(new OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return true;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        switch (gravity) {
            case Gravity.TOP:
                layoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
                touchEventHelper = new HorizontalTouchEventHelper(this);
                break;
            case Gravity.BOTTOM:
                layoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
                touchEventHelper = new HorizontalTouchEventHelper(this);
                break;
            case Gravity.LEFT:
            case Gravity.START:
                layoutManager = new LinearLayoutManager(getContext(), VERTICAL, false);
                touchEventHelper = new VerticalTouchEventHelper(this);
                break;
            case Gravity.RIGHT:
            case Gravity.END:
                layoutManager = new LinearLayoutManager(getContext(), VERTICAL, false);
                touchEventHelper = new VerticalTouchEventHelper(this);
                break;
        }
        super.setLayoutManager(layoutManager);
    }

    public void setAdapter(final PianoAdapter adapter) {
        super.setAdapter(new Adapter() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View content = adapter.onCreateItemView(PianoView.this);
                PianoKeyView view = null;
                switch (gravity) {
                    case Gravity.TOP:
                        view = new VerticalPianoKeyView(PianoView.this, content, VerticalPianoKeyView.DOWN);
                        break;
                    case Gravity.BOTTOM:
                        view = new VerticalPianoKeyView(PianoView.this, content, VerticalPianoKeyView.UP);
                        break;
                    case Gravity.LEFT:
                    case Gravity.START:
                        view = new HorizontalPianoKeyView(PianoView.this, content, HorizontalPianoKeyView.LEFT);
                        break;
                    case Gravity.RIGHT:
                    case Gravity.END:
                        view = new HorizontalPianoKeyView(PianoView.this, content, HorizontalPianoKeyView.RIGHT);
                        break;
                }
                return new ViewHolder(view) {
                };
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                if (holder.itemView instanceof PianoKeyView) {
                    ((PianoKeyView) holder.itemView).hide();
                }
                adapter.onBindItemView(holder.itemView, position);
            }

            @Override
            public int getItemCount() {
                return adapter.getItemCount();
            }
        });
    }

    public void setItemSpacing(int spacing) {
        itemSpacing = spacing;
    }

    public int getItemSpacing() {
        return itemSpacing;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    LinearLayoutManager getInnerLayoutManager() {
        return layoutManager;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        touchEventHelper.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    @Override
    public void smoothScrollBy(int dx, int dy) {
        scrollRunnable.setScrollBy(dx, dy);
        post(scrollRunnable);
    }

    private class ScrollRunnable implements Runnable {
        private int dx;
        private int dy;

        public void setScrollBy(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        @Override
        public void run() {
            PianoView.super.smoothScrollBy(dx, dy);
        }
    }

    @Deprecated
    @Override
    public void setAdapter(Adapter adapter) {
        throw new RuntimeException("instead of setAdapter(PianoAdapter adapter)");
    }

    @Deprecated
    @Override
    public void setLayoutManager(LayoutManager layout) {
        throw new RuntimeException("cannot call this method");
    }

    public interface PianoAdapter {
        View onCreateItemView(PianoView parent);

        void onBindItemView(View itemView, int position);

        int getItemCount();
    }

    public interface OnItemSelectedListener {
        void onItemSelected(PianoView view, View itemView, int position);
    }
}
