package org.telegram.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final /* synthetic */ class PassportActivity$$Lambda$32 implements OnTouchListener {
    private final PassportActivity arg$1;
    private final Context arg$2;

    PassportActivity$$Lambda$32(PassportActivity passportActivity, Context context) {
        this.arg$1 = passportActivity;
        this.arg$2 = context;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$createIdentityInterface$49$PassportActivity(this.arg$2, view, motionEvent);
    }
}
