package org.telegram.p005ui;

import org.telegram.p005ui.ChangePhoneActivity.LoginActivitySmsView.C05333.C05321;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$3$1$$Lambda$0 */
final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$3$1$$Lambda$0 implements RequestDelegate {
    private final C05321 arg$1;

    ChangePhoneActivity$LoginActivitySmsView$3$1$$Lambda$0(C05321 c05321) {
        this.arg$1 = c05321;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$1$ChangePhoneActivity$LoginActivitySmsView$3$1(tLObject, tL_error);
    }
}
