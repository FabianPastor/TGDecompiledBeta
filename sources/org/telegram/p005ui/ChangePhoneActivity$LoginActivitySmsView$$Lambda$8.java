package org.telegram.p005ui;

import org.telegram.p005ui.ChangePhoneActivity.LoginActivitySmsView;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_changePhone;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$$Lambda$8 */
final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$Lambda$8 implements Runnable {
    private final LoginActivitySmsView arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final TL_account_changePhone arg$4;

    ChangePhoneActivity$LoginActivitySmsView$$Lambda$8(LoginActivitySmsView loginActivitySmsView, TL_error tL_error, TLObject tLObject, TL_account_changePhone tL_account_changePhone) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = tL_account_changePhone;
    }

    public void run() {
        this.arg$1.lambda$null$6$ChangePhoneActivity$LoginActivitySmsView(this.arg$2, this.arg$3, this.arg$4);
    }
}
