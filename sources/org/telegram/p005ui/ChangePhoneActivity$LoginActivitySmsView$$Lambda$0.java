package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.p005ui.ChangePhoneActivity.LoginActivitySmsView;

/* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$$Lambda$0 */
final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$Lambda$0 implements OnEditorActionListener {
    private final LoginActivitySmsView arg$1;

    ChangePhoneActivity$LoginActivitySmsView$$Lambda$0(LoginActivitySmsView loginActivitySmsView) {
        this.arg$1 = loginActivitySmsView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$new$0$ChangePhoneActivity$LoginActivitySmsView(textView, i, keyEvent);
    }
}
