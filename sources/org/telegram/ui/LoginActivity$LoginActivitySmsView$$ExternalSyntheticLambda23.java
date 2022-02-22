package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda23 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda23(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLObject tLObject) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$onNextPressed$23(this.f$1);
    }
}
