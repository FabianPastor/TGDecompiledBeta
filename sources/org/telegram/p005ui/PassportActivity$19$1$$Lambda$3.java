package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C190319.C19021;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureValue;

/* renamed from: org.telegram.ui.PassportActivity$19$1$$Lambda$3 */
final /* synthetic */ class PassportActivity$19$1$$Lambda$3 implements RequestDelegate {
    private final C19021 arg$1;
    private final TL_secureValue arg$2;

    PassportActivity$19$1$$Lambda$3(C19021 c19021, TL_secureValue tL_secureValue) {
        this.arg$1 = c19021;
        this.arg$2 = tL_secureValue;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$4$PassportActivity$19$1(this.arg$2, tLObject, tL_error);
    }
}
