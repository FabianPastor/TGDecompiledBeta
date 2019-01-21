package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final /* synthetic */ class LaunchActivity$$Lambda$0 implements OnTouchListener {
    private final LaunchActivity arg$1;

    LaunchActivity$$Lambda$0(LaunchActivity launchActivity) {
        this.arg$1 = launchActivity;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$onCreate$0$LaunchActivity(view, motionEvent);
    }
}
