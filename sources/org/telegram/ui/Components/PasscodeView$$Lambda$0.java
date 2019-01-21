package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class PasscodeView$$Lambda$0 implements OnEditorActionListener {
    private final PasscodeView arg$1;

    PasscodeView$$Lambda$0(PasscodeView passcodeView) {
        this.arg$1 = passcodeView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$new$0$PasscodeView(textView, i, keyEvent);
    }
}
