package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView;

final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$6 implements Runnable {
    private final LoginActivitySmsView arg$1;
    private final TL_error arg$2;
    private final TL_account_confirmPhone arg$3;

    CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$6(LoginActivitySmsView loginActivitySmsView, TL_error tL_error, TL_account_confirmPhone tL_account_confirmPhone) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = tL_error;
        this.arg$3 = tL_account_confirmPhone;
    }

    public void run() {
        this.arg$1.lambda$null$6$CancelAccountDeletionActivity$LoginActivitySmsView(this.arg$2, this.arg$3);
    }
}
