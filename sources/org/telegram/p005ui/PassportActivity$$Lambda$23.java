package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$23 */
final /* synthetic */ class PassportActivity$$Lambda$23 implements OnEditorActionListener {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$23(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createAddressInterface$36$PassportActivity(textView, i, keyEvent);
    }
}
