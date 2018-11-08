package org.telegram.p005ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$31 */
final /* synthetic */ class PassportActivity$$Lambda$31 implements OnTouchListener {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$31(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$createIdentityInterface$46$PassportActivity(view, motionEvent);
    }
}
