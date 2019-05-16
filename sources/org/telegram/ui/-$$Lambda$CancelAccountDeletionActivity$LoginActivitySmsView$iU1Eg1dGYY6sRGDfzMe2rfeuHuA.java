package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$iU1Eg1dGYY6sRGDfzMe2rfeuHuA implements Runnable {
    private final /* synthetic */ LoginActivitySmsView f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TL_account_confirmPhone f$2;

    public /* synthetic */ -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$iU1Eg1dGYY6sRGDfzMe2rfeuHuA(LoginActivitySmsView loginActivitySmsView, TL_error tL_error, TL_account_confirmPhone tL_account_confirmPhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
        this.f$2 = tL_account_confirmPhone;
    }

    public final void run() {
        this.f$0.lambda$null$6$CancelAccountDeletionActivity$LoginActivitySmsView(this.f$1, this.f$2);
    }
}
