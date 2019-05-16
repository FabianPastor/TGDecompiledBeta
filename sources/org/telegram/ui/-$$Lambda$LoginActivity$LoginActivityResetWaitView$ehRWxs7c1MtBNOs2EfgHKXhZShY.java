package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityResetWaitView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityResetWaitView$ehRWxs7c1MtBNOs2EfgHKXhZShY implements Runnable {
    private final /* synthetic */ LoginActivityResetWaitView f$0;
    private final /* synthetic */ TL_error f$1;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityResetWaitView$ehRWxs7c1MtBNOs2EfgHKXhZShY(LoginActivityResetWaitView loginActivityResetWaitView, TL_error tL_error) {
        this.f$0 = loginActivityResetWaitView;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.lambda$null$0$LoginActivity$LoginActivityResetWaitView(this.f$1);
    }
}
