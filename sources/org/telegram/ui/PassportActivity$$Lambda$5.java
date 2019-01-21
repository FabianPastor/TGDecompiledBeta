package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class PassportActivity$$Lambda$5 implements OnEditorActionListener {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$5(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createPasswordInterface$7$PassportActivity(textView, i, keyEvent);
    }
}
