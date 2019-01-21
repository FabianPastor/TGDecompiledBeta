package org.telegram.ui;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

final /* synthetic */ class LaunchActivity$$Lambda$3 implements OnGlobalLayoutListener {
    private final View arg$1;

    LaunchActivity$$Lambda$3(View view) {
        this.arg$1 = view;
    }

    public void onGlobalLayout() {
        LaunchActivity.lambda$onCreate$3$LaunchActivity(this.arg$1);
    }
}
