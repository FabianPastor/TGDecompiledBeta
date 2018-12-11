package org.telegram.p005ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$32 */
final /* synthetic */ class PassportActivity$$Lambda$32 implements OnTouchListener {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$32(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$createIdentityInterface$47$PassportActivity(view, motionEvent);
    }
}
