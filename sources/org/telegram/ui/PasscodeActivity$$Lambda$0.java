package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class PasscodeActivity$$Lambda$0 implements OnEditorActionListener {
    private final PasscodeActivity arg$1;

    PasscodeActivity$$Lambda$0(PasscodeActivity passcodeActivity) {
        this.arg$1 = passcodeActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$0$PasscodeActivity(textView, i, keyEvent);
    }
}
