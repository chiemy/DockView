/*
 * Copyright 2017 chiemy
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
