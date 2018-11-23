package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.p005ui.LoginActivity.LoginActivitySmsView;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$$Lambda$3 */
final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$3 implements OnEditorActionListener {
    private final LoginActivitySmsView arg$1;

    LoginActivity$LoginActivitySmsView$$Lambda$3(LoginActivitySmsView loginActivitySmsView) {
        this.arg$1 = loginActivitySmsView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$setParams$4$LoginActivity$LoginActivitySmsView(textView, i, keyEvent);
    }
}
