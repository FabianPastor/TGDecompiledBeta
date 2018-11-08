package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.PhoneConfirmationView.C15333.C15321;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$3$1$$Lambda$0 */
final /* synthetic */ class PassportActivity$PhoneConfirmationView$3$1$$Lambda$0 implements RequestDelegate {
    private final C15321 arg$1;

    PassportActivity$PhoneConfirmationView$3$1$$Lambda$0(C15321 c15321) {
        this.arg$1 = c15321;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$1$PassportActivity$PhoneConfirmationView$3$1(tLObject, tL_error);
    }
}
