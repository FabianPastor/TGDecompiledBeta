package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView;

final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$Lambda$3 implements OnEditorActionListener {
    private final LoginActivitySmsView arg$1;

    ChangePhoneActivity$LoginActivitySmsView$$Lambda$3(LoginActivitySmsView loginActivitySmsView) {
        this.arg$1 = loginActivitySmsView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$setParams$5$ChangePhoneActivity$LoginActivitySmsView(textView, i, keyEvent);
    }
}
