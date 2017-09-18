package com.chiemy.dock;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chiemy.piano.R;

/**
 * Created: chiemy
 * Date: 9/15/17
 * Description:
 */

public class DockMenuView extends LinearLayout {
    public static final int LEFT_TO_RIGHT = 0;
    public static final int RIGHT_TO_LEFT = 1;
    public static final int TOP_TO_BOTTOM = 2;
    public static final int BOTTOM_TO_TOP = 3;

    @IntDef({
            LEFT_TO_RIGHT,
            RIGHT_TO_LEFT,
            TOP_TO_BOTTOM,
            BOTTOM_TO_TOP
    })
    public @interface MenuOrientation {
    }

    private int orientation = BOTTOM_TO_TOP;
    private DockView pianoView;
    private View contentView;
    private LayoutParams containerViewParams;
    private LayoutParams pianoViewParams;
    private int containerViewIndex;

    public DockMenuView(Context context) {
        this(context, null);
    }

    public DockMenuView(Context context, @MenuOrientation int orientation) {
        super(context);
        this.orientation = orientation;
        init();
    }

    public DockMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DockMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.DockMenuView);
            orientation = t.getInt(R.styleable.DockMenuView_orientation, BOTTOM_TO_TOP);
            int layoutId = t.getResourceId(R.styleable.DockMenuView_content_layout, 0);
            if (layoutId != 0) {
                contentView = LayoutInflater.from(context).inflate(layoutId, this, false);
            }
            t.recycle();
        }
        init();
    }

    private void init() {
        pianoView = new DockView(getContext());
        switch (orientation) {
            case LEFT_TO_RIGHT:
                setOrientation(HORIZONTAL);
                pianoViewParams = createHeightMatchParams();
                containerViewIndex = 1;
                containerViewParams = createHeightMatchParams();
                containerViewParams.weight = 1;
                pianoView.setGravity(Gravity.START);
                addView(pianoView, pianoViewParams);
                break;
            case RIGHT_TO_LEFT:
                setOrientation(HORIZONTAL);
                pianoViewParams = createHeightMatchParams();
                containerViewParams = createHeightMatchParams();
                containerViewParams.weight = 1;
                pianoView.setGravity(Gravity.END);
                addView(pianoView, pianoViewParams);
                break;
            case TOP_TO_BOTTOM:
                setOrientation(VERTICAL);
                pianoViewParams = createWidthMatchParams();
                containerViewParams = createWidthMatchParams();
                containerViewParams.weight = 1;
                containerViewIndex = 1;
                pianoView.setGravity(Gravity.TOP);
                addView(pianoView, pianoViewParams);
                break;
            case BOTTOM_TO_TOP:
                setOrientation(VERTICAL);
                pianoViewParams = createWidthMatchParams();
                containerViewParams = createWidthMatchParams();
                containerViewParams.weight = 1;
                pianoView.setGravity(Gravity.BOTTOM);
                addView(pianoView, pianoViewParams);
                break;
        }
        if (contentView != null) {
            addView(contentView, containerViewIndex, containerViewParams);
        }
    }

    @NonNull
    private LayoutParams createWidthMatchParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    private LayoutParams createHeightMatchParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    }

    public void setMenuOrientation(@MenuOrientation int orientation) {
        if (this.orientation == orientation) {
            return;
        }
        this.orientation = orientation;
        removeAllViews();
        init();
    }

    public void setContentView(View contentView) {
        if (this.contentView != null) {
            removeView(this.contentView);
        }
        this.contentView = contentView;
        addView(contentView, containerViewIndex, containerViewParams);
    }

    public DockView getPianoView() {
        return pianoView;
    }

    public void attachToActivity(Activity activity) {
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup contentView = (ViewGroup) decor.findViewById(android.R.id.content);
        View child = contentView.getChildAt(0);
        contentView.removeView(child);
        setContentView(child);
        contentView.addView(this);
    }
}
