package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.PhoneConfirmationView.C11243.C11231;

final /* synthetic */ class PassportActivity$PhoneConfirmationView$3$1$$Lambda$0 implements RequestDelegate {
    private final C11231 arg$1;

    PassportActivity$PhoneConfirmationView$3$1$$Lambda$0(C11231 c11231) {
        this.arg$1 = c11231;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$1$PassportActivity$PhoneConfirmationView$3$1(tLObject, tL_error);
    }
}
