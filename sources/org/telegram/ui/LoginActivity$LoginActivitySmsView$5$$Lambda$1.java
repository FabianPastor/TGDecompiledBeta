package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView.AnonymousClass5;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$5$$Lambda$1 implements RequestDelegate {
    private final AnonymousClass5 arg$1;

    LoginActivity$LoginActivitySmsView$5$$Lambda$1(AnonymousClass5 anonymousClass5) {
        this.arg$1 = anonymousClass5;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$1$LoginActivity$LoginActivitySmsView$5(tLObject, tL_error);
    }
}
