package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureValue;
import org.telegram.ui.PassportActivity.19.AnonymousClass1;

final /* synthetic */ class PassportActivity$19$1$$Lambda$3 implements RequestDelegate {
    private final AnonymousClass1 arg$1;
    private final TL_secureValue arg$2;

    PassportActivity$19$1$$Lambda$3(AnonymousClass1 anonymousClass1, TL_secureValue tL_secureValue) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = tL_secureValue;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$4$PassportActivity$19$1(this.arg$2, tLObject, tL_error);
    }
}
