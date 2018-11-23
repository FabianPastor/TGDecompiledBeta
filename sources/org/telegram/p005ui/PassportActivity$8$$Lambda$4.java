package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C14998;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$4 */
final /* synthetic */ class PassportActivity$8$$Lambda$4 implements Runnable {
    private final C14998 arg$1;
    private final boolean arg$2;
    private final TL_error arg$3;

    PassportActivity$8$$Lambda$4(C14998 c14998, boolean z, TL_error tL_error) {
        this.arg$1 = c14998;
        this.arg$2 = z;
        this.arg$3 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$run$16$PassportActivity$8(this.arg$2, this.arg$3);
    }
}
