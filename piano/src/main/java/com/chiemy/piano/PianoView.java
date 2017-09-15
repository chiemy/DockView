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
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created: chiemy
 * Date: 9/14/17
 * Description:
 */

public class PianoView extends FrameLayout {
    private LinearLayoutManager layoutManager;
    private int itemSpacing;
    float showMiniPercent = 0.2f;
    int offsetCount = 5;
    private ScrollRunnable scrollRunnable = new ScrollRunnable();
    OnItemSelectedListener listener;

    private List<RecyclerView.OnScrollListener> onScrollListeners;

    private TouchEventHelper touchEventHelper;

    private int gravity = Gravity.BOTTOM;

    private RecyclerView recyclerView;

    private int selectedPosition;

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
        onScrollListeners = new ArrayList<>(3);
        recyclerView = new RecyclerView(getContext());
        addView(recyclerView);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return true;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                if (touchEventHelper != null) {
                    touchEventHelper.onTouchEvent(e);
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        if (touchEventHelper != null) {
            onScrollListeners.remove(touchEventHelper);
        }
        switch (gravity) {
            case Gravity.TOP:
                layoutManager = new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                );
                touchEventHelper = new HorizontalTouchEventHelper(this);
                break;
            case Gravity.BOTTOM:
                layoutManager = new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                );
                touchEventHelper = new HorizontalTouchEventHelper(this);
                break;
            case Gravity.LEFT:
            case Gravity.START:
                layoutManager = new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                );
                touchEventHelper = new VerticalTouchEventHelper(this);
                break;
            case Gravity.RIGHT:
            case Gravity.END:
                layoutManager = new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                );
                touchEventHelper = new VerticalTouchEventHelper(this);
                break;
        }
        onScrollListeners.add(touchEventHelper);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(scrollListener);
        setSelection(selectedPosition);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            for (RecyclerView.OnScrollListener l: onScrollListeners) {
                l.onScrollStateChanged(recyclerView, newState);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            for (RecyclerView.OnScrollListener l: onScrollListeners) {
                l.onScrolled(recyclerView, dx, dy);
            }
        }
    };

    public void setAdapter(final PianoAdapter adapter) {
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View content = adapter.onCreateItemView(PianoView.this);
                PianoKeyView view = null;
                switch (gravity) {
                    case Gravity.TOP:
                        view = new VerticalPianoKeyView(PianoView.this, content,
                                VerticalPianoKeyView.DOWN);
                        break;
                    case Gravity.BOTTOM:
                        view = new VerticalPianoKeyView(PianoView.this, content,
                                VerticalPianoKeyView.UP);
                        break;
                    case Gravity.LEFT:
                    case Gravity.START:
                        view = new HorizontalPianoKeyView(PianoView.this, content,
                                HorizontalPianoKeyView.LEFT);
                        break;
                    case Gravity.RIGHT:
                    case Gravity.END:
                        view = new HorizontalPianoKeyView(PianoView.this, content,
                                HorizontalPianoKeyView.RIGHT);
                        break;
                }
                return new RecyclerView.ViewHolder(view) {
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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

    public void smoothScrollBy(int dx, int dy) {
        scrollRunnable.setScrollBy(dx, dy);
        post(scrollRunnable);
    }

    public void smoothScrollTo(int position) {
        recyclerView.smoothScrollToPosition(position);
    }

    public void setSelection(int position) {
        selectedPosition = position;
        post(new Runnable() {
            @Override
            public void run() {
                if (touchEventHelper != null) {
                    touchEventHelper.performTouch(selectedPosition);
                }
            }
        });
    }

    int getChildAdapterPosition(View view) {
        return recyclerView.getChildAdapterPosition(view);
    }

    View findChildViewUnder(float x, float y) {
        return recyclerView.findChildViewUnder(x, y);
    }

    LinearLayoutManager getInnerLayoutManager() {
        return layoutManager;
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
            recyclerView.smoothScrollBy(dx, dy);
        }
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
