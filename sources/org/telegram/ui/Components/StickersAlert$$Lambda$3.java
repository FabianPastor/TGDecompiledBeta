package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final /* synthetic */ class StickersAlert$$Lambda$3 implements OnTouchListener {
    private final StickersAlert arg$1;

    StickersAlert$$Lambda$3(StickersAlert stickersAlert) {
        this.arg$1 = stickersAlert;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$init$5$StickersAlert(view, motionEvent);
    }
}