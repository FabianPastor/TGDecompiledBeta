package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final /* synthetic */ class InstantCameraView$$Lambda$0 implements OnTouchListener {
    private final InstantCameraView arg$1;

    InstantCameraView$$Lambda$0(InstantCameraView instantCameraView) {
        this.arg$1 = instantCameraView;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$new$0$InstantCameraView(view, motionEvent);
    }
}