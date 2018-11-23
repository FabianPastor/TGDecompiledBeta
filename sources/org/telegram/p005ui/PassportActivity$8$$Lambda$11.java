package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C14998;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$11 */
final /* synthetic */ class PassportActivity$8$$Lambda$11 implements Runnable {
    private final C14998 arg$1;
    private final TL_error arg$2;

    PassportActivity$8$$Lambda$11(C14998 c14998, TL_error tL_error) {
        this.arg$1 = c14998;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$6$PassportActivity$8(this.arg$2);
    }
}
