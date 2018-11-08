package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C19198;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$4 */
final /* synthetic */ class PassportActivity$8$$Lambda$4 implements Runnable {
    private final C19198 arg$1;
    private final boolean arg$2;
    private final TL_error arg$3;

    PassportActivity$8$$Lambda$4(C19198 c19198, boolean z, TL_error tL_error) {
        this.arg$1 = c19198;
        this.arg$2 = z;
        this.arg$3 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$run$16$PassportActivity$8(this.arg$2, this.arg$3);
    }
}
