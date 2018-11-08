package org.telegram.p005ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* renamed from: org.telegram.ui.ExternalActionActivity$$Lambda$0 */
final /* synthetic */ class ExternalActionActivity$$Lambda$0 implements OnTouchListener {
    private final ExternalActionActivity arg$1;

    ExternalActionActivity$$Lambda$0(ExternalActionActivity externalActionActivity) {
        this.arg$1 = externalActionActivity;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$onCreate$0$ExternalActionActivity(view, motionEvent);
    }
}
