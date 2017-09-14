package com.chiemy.piano;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;


/**
 * Created: chiemy
 * Date: 9/14/17
 * Description:
 */

public class PianoView extends RecyclerView {
    private LinearLayoutManager layoutManager;
    private int itemSpacing;
    private float showMiniPercent = 0.2f;
    private float translatePercent = 1 - showMiniPercent;
    private int offsetCount = 3;
    private ScrollRunnable scrollRunnable = new ScrollRunnable();

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
        super.setLayoutManager(
                layoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false));
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

    public void setAdapter(final PianoAdapter adapter) {
        super.setAdapter(new Adapter() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                PianoKeyView view = new PianoKeyView(
                        parent.getContext(),
                        adapter.onCreateItemView(PianoView.this));
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

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        View touchView = findChildViewUnder(e.getX(), 0);
        if (touchView != null) {
            final int position = getChildAdapterPosition(touchView);
            final int startPosition = Math.max(0, position - offsetCount);
            final int endPosition = Math.min(layoutManager.getItemCount(), position + offsetCount);
            for (int i = startPosition; i < endPosition; i++) {
                View view = layoutManager.findViewByPosition(i);
                if (view != null && view instanceof PianoKeyView) {
                    int offset = Math.abs(i - position);
                    float percent = Math.max(1 - (float) offset / offsetCount, showMiniPercent);
                    ((PianoKeyView) view).show(percent);
                }
            }

            if (e.getAction() == MotionEvent.ACTION_UP) {
                final int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                final int lastVisiblePosition = layoutManager.findLastVisibleItemPosition() + 1;
                for (int i = firstVisiblePosition; i < lastVisiblePosition; i++) {
                    if (i != position) {
                        View view = layoutManager.findViewByPosition(i);
                        if (view != null && view instanceof PianoKeyView) {
                            ((PianoKeyView) view).show(showMiniPercent);
                        }
                    }
                }

                final int touchViewCenter = (int) touchView.getX() + touchView.getWidth() / 2;
                final int deltaX =  touchViewCenter - getWidth() / 2;
                scrollRunnable.setDistance(deltaX);
                post(scrollRunnable);
            }
        }
        return super.onTouchEvent(e);
    }

    private class ScrollRunnable implements Runnable {
        private int distance;

        public void setDistance(int distance) {
            this.distance = distance;
        }

        @Override
        public void run() {
            smoothScrollBy(distance, 0);
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

    private class PianoKeyView extends FrameLayout {
        private float currentPercent = 0;
        private View content;

        public PianoKeyView(@NonNull Context context, View content) {
            super(context);
            addView(content);
            this.content = content;
            setPadding(itemSpacing / 2, 0, itemSpacing / 2, 0);
            setClipToPadding(false);
        }

        public void show(float percent) {
            if (currentPercent == percent) {
                return;
            }
            currentPercent = percent;
            ObjectAnimator animator =
                    ObjectAnimator.ofFloat(
                            content,
                            "translationY",
                            content.getTranslationY(),
                            (1 - percent) * getHeight()
                    );
            animator.setDuration(500);
            animator.setInterpolator(new OvershootInterpolator());
            animator.start();
        }

        public void hide() {
            if (currentPercent != translatePercent) {
                currentPercent = translatePercent;
                post(runnable);
            }
        }

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                content.setTranslationY(getHeight() * translatePercent);
            }
        };
    }
}
