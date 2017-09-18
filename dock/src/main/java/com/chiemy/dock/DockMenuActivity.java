package com.chiemy.dock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created: chiemy
 * Date: 9/15/17
 * Description:
 */

public class DockMenuActivity extends AppCompatActivity implements DockView.OnItemSelectedListener {
    private int orientation = DockMenuView.BOTTOM_TO_TOP;
    private DockMenuView menuView;
    private DockView pianoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuView = new DockMenuView(this, orientation);
        pianoView = menuView.getPianoView();
        pianoView.setItemSpacing(20);
        pianoView.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(DockView view, View itemView, int position) {
    }

    public void setMenuAdapter(DockView.DockAdapter adapter) {
        pianoView.setAdapter(adapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        menuView.attachToActivity(this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    /**
     * Should call before super.onCreate
     * @param orientation {@link DockMenuView.MenuOrientation}
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
