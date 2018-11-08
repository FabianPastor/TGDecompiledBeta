package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C21978;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$16 */
final /* synthetic */ class PassportActivity$8$$Lambda$16 implements Runnable {
    private final C21978 arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    PassportActivity$8$$Lambda$16(C21978 c21978, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = c21978;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$0$PassportActivity$8(this.arg$2, this.arg$3);
    }
}
