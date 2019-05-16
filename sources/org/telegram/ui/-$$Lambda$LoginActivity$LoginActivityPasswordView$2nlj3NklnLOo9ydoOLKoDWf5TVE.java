package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityPasswordView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityPasswordView$2nlj3NklnLOo9ydoOLKoDWf5TVE implements RequestDelegate {
    private final /* synthetic */ LoginActivityPasswordView f$0;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityPasswordView$2nlj3NklnLOo9ydoOLKoDWf5TVE(LoginActivityPasswordView loginActivityPasswordView) {
        this.f$0 = loginActivityPasswordView;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$6$LoginActivity$LoginActivityPasswordView(tLObject, tL_error);
    }
}
