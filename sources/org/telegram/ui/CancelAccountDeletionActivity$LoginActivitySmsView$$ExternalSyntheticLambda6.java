package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.CancelAccountDeletionActivity;

public final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ CancelAccountDeletionActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_account_confirmPhone f$2;

    public /* synthetic */ CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda6(CancelAccountDeletionActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_error tL_error, TLRPC.TL_account_confirmPhone tL_account_confirmPhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
        this.f$2 = tL_account_confirmPhone;
    }

    public final void run() {
        this.f$0.m1511x5164e2ef(this.f$1, this.f$2);
    }
}
