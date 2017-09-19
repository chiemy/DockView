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

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chiemy on 2017/9/15.
 */
 class HorizontalTouchEventHelper extends TouchEventHelper {

    HorizontalTouchEventHelper(DockView pianoView) {
        super(pianoView);
    }

    @Override
    View findTouchView(MotionEvent e) {
        float x = Math.min(pianoView.getWidth(), e.getX());
        x = Math.max(0, x);
        return pianoView.findChildViewUnder(x, 0);
    }

    @Override
    void scrollToViewCenter(View touchView) {
        final int touchViewCenter = calculate(touchView);
        final int deltaX =  touchViewCenter - pianoView.getWidth() / 2;
        pianoView.smoothScrollBy(deltaX, 0);
    }

    private int calculate(View view) {
        return (int) view.getX() + view.getWidth() / 2;
    }
}
