package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda2 implements View.OnKeyListener {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda2(LoginActivity.LoginActivitySmsView loginActivitySmsView, int i) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = i;
    }

    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$setParams$3(this.f$1, view, i, keyEvent);
    }
}
