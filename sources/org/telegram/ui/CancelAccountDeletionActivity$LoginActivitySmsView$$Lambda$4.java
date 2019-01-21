package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView;

final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$4 implements RequestDelegate {
    private final LoginActivitySmsView arg$1;
    private final TL_account_confirmPhone arg$2;

    CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$4(LoginActivitySmsView loginActivitySmsView, TL_account_confirmPhone tL_account_confirmPhone) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = tL_account_confirmPhone;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onNextPressed$7$CancelAccountDeletionActivity$LoginActivitySmsView(this.arg$2, tLObject, tL_error);
    }
}
