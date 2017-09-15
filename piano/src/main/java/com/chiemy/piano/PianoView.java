package com.chiemy.piano;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.FloatRange;
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
    private static final float DEF_MIN_PERCENT = 0.2f;
    private static final float DEF_DELTA_H_PERCENT = 0.2f;
    private static final int DEF_POP_OFFSET = 5;

    /**
     * 子视图露出部分占总高度的百分比
     */
    float peekPercent = DEF_MIN_PERCENT;
    /**
     * 子视图露出部分的尺寸
     */
    float peekSize;
    /**
     * 当手指点击某位置时，该位置左右两边需要弹起的视图的偏移量
     */
    int popOffset = DEF_POP_OFFSET;
    OnItemSelectedListener listener;
    /**
     * 相邻两个弹起的视图的高度差百分比
     */
    float deltaHeightPercent = DEF_DELTA_H_PERCENT;
    /**
     * item 间距
     */
    private int itemSpacing;
    private ScrollRunnable scrollRunnable = new ScrollRunnable();

    private List<RecyclerView.OnScrollListener> onScrollListeners;

    private int gravity = Gravity.NO_GRAVITY;

    private RecyclerView recyclerView;

    private int selectedPosition;

    private PianoAdapter pianoAdapter;

    private PianoViewLayoutManager pianoLayoutManager;

    public PianoView(Context context) {
        this(context, null);
    }

    public PianoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PianoView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.PianoView);
            peekPercent = t.getFloat(R.styleable.PianoView_peekPercent, 0);
            peekSize = t.getDimensionPixelSize(R.styleable.PianoView_peekSize, 0);
            // percent 优先
            if (peekPercent > 0) {
                peekSize = 0;
            } else if (peekSize <= 0){
                peekPercent = DEF_MIN_PERCENT;
            }
            gravity = t.getInt(R.styleable.PianoView_gravity, Gravity.NO_GRAVITY);
            float deltaHeightPercent =
                    t.getFloat(R.styleable.PianoView_deltaHeightPercent, DEF_DELTA_H_PERCENT);
            setDeltaHeightPercent(deltaHeightPercent);
            popOffset = t.getInt(R.styleable.PianoView_popOffset, DEF_POP_OFFSET);
            t.recycle();
        }

        onScrollListeners = new ArrayList<>(3);
        recyclerView = new RecyclerView(getContext());
        recyclerView.setOnScrollListener(scrollListener);
        addView(recyclerView);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return true;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                if (pianoLayoutManager != null) {
                    pianoLayoutManager.onTouchEvent(e);
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
        if (gravity != Gravity.NO_GRAVITY) {
            setGravity(gravity);
        }
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        if (pianoLayoutManager != null) {
            onScrollListeners.remove(pianoLayoutManager);
        }
        pianoLayoutManager = new PianoViewLayoutManager(this, gravity);
        onScrollListeners.add(pianoLayoutManager);
        recyclerView.setLayoutManager(pianoLayoutManager.getLayoutManager());
        setSelection(selectedPosition);
    }

    void setLayoutManager(PianoViewLayoutManager layoutManager) {
        pianoLayoutManager = layoutManager;
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            for (RecyclerView.OnScrollListener l : onScrollListeners) {
                l.onScrollStateChanged(recyclerView, newState);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            for (RecyclerView.OnScrollListener l : onScrollListeners) {
                l.onScrolled(recyclerView, dx, dy);
            }
        }
    };

    public void setAdapter(final PianoAdapter adapter) {
        if (gravity == Gravity.NO_GRAVITY) {
            throw new RuntimeException("should call setGravity before this method");
        }
        if (pianoAdapter != null) {
            pianoAdapter.unregisterAdapterDataObserver(dataObserver);
        }
        pianoAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(dataObserver);
        }
        recyclerView.setAdapter(innerAdapter);
    }

    private AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    };

    private RecyclerView.Adapter innerAdapter = new RecyclerView.Adapter() {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View content = pianoAdapter.onCreateItemView(PianoView.this);
            return new RecyclerView.ViewHolder(pianoLayoutManager.createPianoKeyView(content)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.itemView instanceof PianoKeyView) {
                ((PianoKeyView) holder.itemView).hide();
            }
            pianoAdapter.onBindItemView(holder.itemView, position);
        }

        @Override
        public int getItemCount() {
            return pianoAdapter != null ? pianoAdapter.getItemCount() : 0;
        }
    };

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
                if (pianoLayoutManager != null) {
                    pianoLayoutManager.performTouch(selectedPosition);
                }
            }
        });
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setPeekPercent(float peekPercent) {
        this.peekPercent = peekPercent;
        if (pianoAdapter != null) {
            pianoAdapter.notifyDataSetChanged();
        }
    }

    public void setPeekSize(float peekSize) {
        this.peekSize = peekSize;
        if (peekSize > 0) {
            peekPercent = 0;
        }
        if (pianoAdapter != null) {
            pianoAdapter.notifyDataSetChanged();
        }
    }

    public void setDeltaHeightPercent(@FloatRange(from = 0, to = 1) float deltaHeghtPercent) {
        this.deltaHeightPercent = Math.min(Math.max(0, deltaHeightPercent), 1);
    }

    int getChildAdapterPosition(View view) {
        return recyclerView.getChildAdapterPosition(view);
    }

    View findChildViewUnder(float x, float y) {
        return recyclerView.findChildViewUnder(x, y);
    }

    LinearLayoutManager getInnerLayoutManager() {
        return pianoLayoutManager.getLayoutManager();
    }

    void onItemSelected(PianoKeyView view, int position) {
        selectedPosition = position;
        if (listener != null) {
            listener.onItemSelected(this, view.content, position);
        }
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

    public static abstract class PianoAdapter {
        private final List<AdapterDataObserver> dataObservers = new ArrayList<>(2);

        public abstract View onCreateItemView(PianoView parent);

        public abstract void onBindItemView(View itemView, int position);

        public abstract int getItemCount();

        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            if (observer == null) {
                throw new IllegalArgumentException("The observer is null.");
            }
            synchronized (dataObservers) {
                if (dataObservers.contains(observer)) {
                    throw new IllegalStateException("Observer " + observer + " is already " +
                            "registered.");
                }
                dataObservers.add(observer);
            }
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            if (observer == null) {
                throw new IllegalArgumentException("The observer is null.");
            }
            synchronized (dataObservers) {
                int index = dataObservers.indexOf(observer);
                if (index == -1) {
                    throw new IllegalStateException("Observer " + observer + " was not registered" +
                            ".");
                }
                dataObservers.remove(index);
            }
        }

        public void notifyDataSetChanged() {
            for (AdapterDataObserver observer : dataObservers) {
                observer.onChanged();
            }
        }
    }

    public static abstract class AdapterDataObserver {
        public void onChanged() {
        }
    }


    public interface OnItemSelectedListener {
        void onItemSelected(PianoView view, View itemView, int position);
    }
}
