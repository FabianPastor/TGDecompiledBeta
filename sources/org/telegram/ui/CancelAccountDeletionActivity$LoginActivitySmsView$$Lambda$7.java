package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView;

final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$7 implements Runnable {
    private final LoginActivitySmsView arg$1;
    private final TL_error arg$2;
    private final Bundle arg$3;
    private final TLObject arg$4;
    private final TL_auth_resendCode arg$5;

    CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$7(LoginActivitySmsView loginActivitySmsView, TL_error tL_error, Bundle bundle, TLObject tLObject, TL_auth_resendCode tL_auth_resendCode) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = tL_error;
        this.arg$3 = bundle;
        this.arg$4 = tLObject;
        this.arg$5 = tL_auth_resendCode;
    }

    public void run() {
        this.arg$1.lambda$null$2$CancelAccountDeletionActivity$LoginActivitySmsView(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
