package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.PhoneConfirmationView.C19243.C19231;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$3$1$$Lambda$0 */
final /* synthetic */ class PassportActivity$PhoneConfirmationView$3$1$$Lambda$0 implements RequestDelegate {
    private final C19231 arg$1;

    PassportActivity$PhoneConfirmationView$3$1$$Lambda$0(C19231 c19231) {
        this.arg$1 = c19231;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$1$PassportActivity$PhoneConfirmationView$3$1(tLObject, tL_error);
    }
}
