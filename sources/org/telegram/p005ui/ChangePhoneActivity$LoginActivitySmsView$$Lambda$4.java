package org.telegram.p005ui;

import org.telegram.p005ui.ChangePhoneActivity.LoginActivitySmsView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_changePhone;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$$Lambda$4 */
final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$Lambda$4 implements RequestDelegate {
    private final LoginActivitySmsView arg$1;
    private final TL_account_changePhone arg$2;

    ChangePhoneActivity$LoginActivitySmsView$$Lambda$4(LoginActivitySmsView loginActivitySmsView, TL_account_changePhone tL_account_changePhone) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = tL_account_changePhone;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onNextPressed$7$ChangePhoneActivity$LoginActivitySmsView(this.arg$2, tLObject, tL_error);
    }
}
