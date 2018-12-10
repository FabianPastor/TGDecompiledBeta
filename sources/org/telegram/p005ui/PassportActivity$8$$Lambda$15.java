package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.CLASSNAME;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$15 */
final /* synthetic */ class PassportActivity$8$$Lambda$15 implements RequestDelegate {
    private final CLASSNAME arg$1;

    PassportActivity$8$$Lambda$15(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$1$PassportActivity$8(tLObject, tL_error);
    }
}
