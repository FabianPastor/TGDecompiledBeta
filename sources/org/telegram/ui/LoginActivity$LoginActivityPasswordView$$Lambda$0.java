package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.ui.LoginActivity.LoginActivityPasswordView;

final /* synthetic */ class LoginActivity$LoginActivityPasswordView$$Lambda$0 implements OnEditorActionListener {
    private final LoginActivityPasswordView arg$1;

    LoginActivity$LoginActivityPasswordView$$Lambda$0(LoginActivityPasswordView loginActivityPasswordView) {
        this.arg$1 = loginActivityPasswordView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$new$0$LoginActivity$LoginActivityPasswordView(textView, i, keyEvent);
    }
}
