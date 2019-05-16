package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_signIn;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivitySmsView$QuZqrlgFwDiyZUcg7gbDXVsiiUk implements RequestDelegate {
    private final /* synthetic */ LoginActivitySmsView f$0;
    private final /* synthetic */ TL_auth_signIn f$1;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivitySmsView$QuZqrlgFwDiyZUcg7gbDXVsiiUk(LoginActivitySmsView loginActivitySmsView, TL_auth_signIn tL_auth_signIn) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_auth_signIn;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onNextPressed$8$LoginActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
    }
}
