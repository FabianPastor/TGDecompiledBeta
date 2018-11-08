package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C21978;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$0 */
final /* synthetic */ class PassportActivity$8$$Lambda$0 implements RequestDelegate {
    private final C21978 arg$1;

    PassportActivity$8$$Lambda$0(C21978 c21978) {
        this.arg$1 = c21978;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$resetSecret$3$PassportActivity$8(tLObject, tL_error);
    }
}
