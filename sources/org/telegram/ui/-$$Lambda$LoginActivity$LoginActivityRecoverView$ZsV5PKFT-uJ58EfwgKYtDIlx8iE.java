package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityRecoverView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityRecoverView$ZsV5PKFT-uJ58EfwgKYtDIlx8iE implements Runnable {
    private final /* synthetic */ LoginActivityRecoverView f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ TL_error f$2;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityRecoverView$ZsV5PKFT-uJ58EfwgKYtDIlx8iE(LoginActivityRecoverView loginActivityRecoverView, TLObject tLObject, TL_error tL_error) {
        this.f$0 = loginActivityRecoverView;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
    }

    public final void run() {
        this.f$0.lambda$null$4$LoginActivity$LoginActivityRecoverView(this.f$1, this.f$2);
    }
}
