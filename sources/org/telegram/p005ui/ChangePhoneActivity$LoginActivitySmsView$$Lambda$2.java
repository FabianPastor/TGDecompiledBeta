package org.telegram.p005ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import org.telegram.p005ui.ChangePhoneActivity.LoginActivitySmsView;

/* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$$Lambda$2 */
final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$Lambda$2 implements OnKeyListener {
    private final LoginActivitySmsView arg$1;
    private final int arg$2;

    ChangePhoneActivity$LoginActivitySmsView$$Lambda$2(LoginActivitySmsView loginActivitySmsView, int i) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = i;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$setParams$4$ChangePhoneActivity$LoginActivitySmsView(this.arg$2, view, i, keyEvent);
    }
}
