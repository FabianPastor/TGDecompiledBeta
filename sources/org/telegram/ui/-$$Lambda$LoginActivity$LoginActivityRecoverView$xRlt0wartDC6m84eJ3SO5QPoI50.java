package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityRecoverView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityRecoverView$xRlt0wartDC6mNUMeJ3SO5QPoI50 implements Runnable {
    private final /* synthetic */ LoginActivityRecoverView f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityRecoverView$xRlt0wartDC6mNUMeJ3SO5QPoI50(LoginActivityRecoverView loginActivityRecoverView, TL_error tL_error, TLObject tLObject) {
        this.f$0 = loginActivityRecoverView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$4$LoginActivity$LoginActivityRecoverView(this.f$1, this.f$2);
    }
}
