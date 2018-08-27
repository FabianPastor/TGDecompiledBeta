package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureValue;
import org.telegram.ui.PassportActivity.19.C16441;

final /* synthetic */ class PassportActivity$19$1$$Lambda$3 implements RequestDelegate {
    private final C16441 arg$1;
    private final TL_secureValue arg$2;

    PassportActivity$19$1$$Lambda$3(C16441 c16441, TL_secureValue tL_secureValue) {
        this.arg$1 = c16441;
        this.arg$2 = tL_secureValue;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$4$PassportActivity$19$1(this.arg$2, tLObject, tL_error);
    }
}
