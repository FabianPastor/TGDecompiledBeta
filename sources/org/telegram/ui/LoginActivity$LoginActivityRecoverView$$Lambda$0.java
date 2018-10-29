package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.ui.LoginActivity.LoginActivityRecoverView;

final /* synthetic */ class LoginActivity$LoginActivityRecoverView$$Lambda$0 implements OnEditorActionListener {
    private final LoginActivityRecoverView arg$1;

    LoginActivity$LoginActivityRecoverView$$Lambda$0(LoginActivityRecoverView loginActivityRecoverView) {
        this.arg$1 = loginActivityRecoverView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$new$0$LoginActivity$LoginActivityRecoverView(textView, i, keyEvent);
    }
}
