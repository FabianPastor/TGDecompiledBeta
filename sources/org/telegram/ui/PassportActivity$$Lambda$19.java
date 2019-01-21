package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

final /* synthetic */ class PassportActivity$$Lambda$19 implements OnKeyListener {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$19(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createPhoneInterface$31$PassportActivity(view, i, keyEvent);
    }
}
