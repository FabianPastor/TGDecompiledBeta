package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.CLASSNAME;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$11 */
final /* synthetic */ class PassportActivity$8$$Lambda$11 implements Runnable {
    private final CLASSNAME arg$1;
    private final TL_error arg$2;

    PassportActivity$8$$Lambda$11(CLASSNAME CLASSNAME, TL_error tL_error) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$6$PassportActivity$8(this.arg$2);
    }
}
