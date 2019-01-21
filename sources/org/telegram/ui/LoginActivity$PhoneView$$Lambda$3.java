package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import org.telegram.ui.LoginActivity.PhoneView;

final /* synthetic */ class LoginActivity$PhoneView$$Lambda$3 implements OnKeyListener {
    private final PhoneView arg$1;

    LoginActivity$PhoneView$$Lambda$3(PhoneView phoneView) {
        this.arg$1 = phoneView;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$new$5$LoginActivity$PhoneView(view, i, keyEvent);
    }
}
