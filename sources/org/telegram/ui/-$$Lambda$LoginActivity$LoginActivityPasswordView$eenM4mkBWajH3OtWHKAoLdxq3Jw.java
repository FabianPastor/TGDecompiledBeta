package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityPasswordView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityPasswordView$eenM4mkBWajH3OtWHKAoLdxq3Jw implements Runnable {
    private final /* synthetic */ LoginActivityPasswordView f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityPasswordView$eenM4mkBWajH3OtWHKAoLdxq3Jw(LoginActivityPasswordView loginActivityPasswordView, TL_error tL_error, TLObject tLObject) {
        this.f$0 = loginActivityPasswordView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$12$LoginActivity$LoginActivityPasswordView(this.f$1, this.f$2);
    }
}