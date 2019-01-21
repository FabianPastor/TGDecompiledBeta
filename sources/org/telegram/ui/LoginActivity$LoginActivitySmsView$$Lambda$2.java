package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$2 implements OnKeyListener {
    private final LoginActivitySmsView arg$1;
    private final int arg$2;

    LoginActivity$LoginActivitySmsView$$Lambda$2(LoginActivitySmsView loginActivitySmsView, int i) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = i;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$setParams$3$LoginActivity$LoginActivitySmsView(this.arg$2, view, i, keyEvent);
    }
}
