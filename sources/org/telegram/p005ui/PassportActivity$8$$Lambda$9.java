package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.CLASSNAME;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$9 */
final /* synthetic */ class PassportActivity$8$$Lambda$9 implements Runnable {
    private final CLASSNAME arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final boolean arg$4;

    PassportActivity$8$$Lambda$9(CLASSNAME CLASSNAME, TL_error tL_error, TLObject tLObject, boolean z) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$null$9$PassportActivity$8(this.arg$2, this.arg$3, this.arg$4);
    }
}
