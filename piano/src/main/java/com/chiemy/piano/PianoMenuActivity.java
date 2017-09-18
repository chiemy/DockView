package com.chiemy.piano;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created: chiemy
 * Date: 9/15/17
 * Description:
 */

public class PianoMenuActivity extends AppCompatActivity implements PianoView.OnItemSelectedListener {
    private int orientation = PianoMenuView.BOTTOM_TO_TOP;
    private PianoMenuView menuView;
    private PianoView pianoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuView = new PianoMenuView(this, orientation);
        pianoView = menuView.getPianoView();
        pianoView.setItemSpacing(20);
        pianoView.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(PianoView view, View itemView, int position) {
    }

    public void setMenuAdapter(PianoView.PianoAdapter adapter) {
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
     * @param orientation {@link com.chiemy.piano.PianoMenuView.MenuOrientation}
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
