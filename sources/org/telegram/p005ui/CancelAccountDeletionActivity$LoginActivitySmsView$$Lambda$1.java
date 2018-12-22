package org.telegram.p005ui;

import android.os.Bundle;
import org.telegram.p005ui.CancelAccountDeletionActivity.LoginActivitySmsView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$1 */
final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$1 implements RequestDelegate {
    private final LoginActivitySmsView arg$1;
    private final Bundle arg$2;
    private final TL_auth_resendCode arg$3;

    CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$1(LoginActivitySmsView loginActivitySmsView, Bundle bundle, TL_auth_resendCode tL_auth_resendCode) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = bundle;
        this.arg$3 = tL_auth_resendCode;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.mo14194x5var_a36a(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
