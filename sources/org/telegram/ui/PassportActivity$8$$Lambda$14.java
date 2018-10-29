package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.C15298;

final /* synthetic */ class PassportActivity$8$$Lambda$14 implements Runnable {
    private final C15298 arg$1;
    private final TL_error arg$2;

    PassportActivity$8$$Lambda$14(C15298 c15298, TL_error tL_error) {
        this.arg$1 = c15298;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$2$PassportActivity$8(this.arg$2);
    }
}
