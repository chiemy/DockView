package com.chiemy.piano;

import android.content.Context;
import android.content.res.TypedArray;
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
    private int itemSpacing;
    float showMiniPercent = 0.2f;
    int offsetCount = 5;
    private ScrollRunnable scrollRunnable = new ScrollRunnable();
    OnItemSelectedListener listener;

    private List<RecyclerView.OnScrollListener> onScrollListeners;

    private int gravity = Gravity.BOTTOM;

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
            return new RecyclerView.ViewHolder(pianoLayoutManager.createPianoKeyView(content)) {};
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

    int getChildAdapterPosition(View view) {
        return recyclerView.getChildAdapterPosition(view);
    }

    View findChildViewUnder(float x, float y) {
        return recyclerView.findChildViewUnder(x, y);
    }

    LinearLayoutManager getInnerLayoutManager() {
        return pianoLayoutManager.getLayoutManager();
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
            synchronized(dataObservers) {
                if (dataObservers.contains(observer)) {
                    throw new IllegalStateException("Observer " + observer + " is already registered.");
                }
                dataObservers.add(observer);
            }
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            if (observer == null) {
                throw new IllegalArgumentException("The observer is null.");
            }
            synchronized(dataObservers) {
                int index = dataObservers.indexOf(observer);
                if (index == -1) {
                    throw new IllegalStateException("Observer " + observer + " was not registered.");
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
