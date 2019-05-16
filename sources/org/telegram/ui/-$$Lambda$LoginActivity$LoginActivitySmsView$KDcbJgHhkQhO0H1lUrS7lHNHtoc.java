package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_signIn;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivitySmsView$KDcbJgHhkQhO0H1lUrS7lHNHtoc implements Runnable {
    private final /* synthetic */ LoginActivitySmsView f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_auth_signIn f$3;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivitySmsView$KDcbJgHhkQhO0H1lUrS7lHNHtoc(LoginActivitySmsView loginActivitySmsView, TL_error tL_error, TLObject tLObject, TL_auth_signIn tL_auth_signIn) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_auth_signIn;
    }

    public final void run() {
        this.f$0.lambda$null$7$LoginActivity$LoginActivitySmsView(this.f$1, this.f$2, this.f$3);
    }
}
