package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.CLASSNAME;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$2 */
final /* synthetic */ class PassportActivity$8$$Lambda$2 implements RequestDelegate {
    private final CLASSNAME arg$1;
    private final boolean arg$2;

    PassportActivity$8$$Lambda$2(CLASSNAME CLASSNAME, boolean z) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$10$PassportActivity$8(this.arg$2, tLObject, tL_error);
    }
}
